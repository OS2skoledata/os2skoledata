using Google.Apis.Admin.Directory.directory_v1.Data;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace.SettingsModel;
using os2skoledata_google_workspace_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata
{
    internal class SyncService : ServiceBase<SyncService>
    {
        private readonly WorkspaceService workspaceService;
        private readonly OS2skoledataService oS2skoledataService;
        private bool performFullSyncNext = true;
        private bool resetFullSyncFlag = false;
        private bool userDryRun;
        private int nonCriticalErrorCount = 0;
        private List<string> globalLockedInstitutionNumbers;
        private readonly string domain;
        private readonly UsernameStandardType usernameStandard;
        private readonly HierarchyType hierarchyType;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            workspaceService = sp.GetService<WorkspaceService>();
            oS2skoledataService = sp.GetService<OS2skoledataService>();
            userDryRun = settings.WorkspaceSettings.UserDryRun;
            globalLockedInstitutionNumbers = new List<string>();
            domain = settings.WorkspaceSettings.Domain;
            usernameStandard = settings.WorkspaceSettings.usernameSettings == null ? UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN : settings.WorkspaceSettings.usernameSettings.UsernameStandard;
            hierarchyType = settings.WorkspaceSettings.HierarchyType;
        }

        public void SetFullSync()
        {
            resetFullSyncFlag = true;
        }

        public void Synchronize()
        {
            if (performFullSyncNext)
            {
                FullSync();
            }
            else
            {
                DeltaSync();
            }

            performFullSyncNext = resetFullSyncFlag;
            resetFullSyncFlag = false;
        }

        private void FullSync()
        {
            try
            {
                logger.LogInformation("Mainflow - Executing FullSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                // get head
                long head = oS2skoledataService.GetHead();

                // add to cpr and username map in case there is multiple institution people with same cpr that should be created in same sync
                // if not in same sync it is handled by the backend, so this map only contains users created in this sync
                Dictionary<string, string> cprUsernameMap = new Dictionary<string, string>();

                // handle usernameMap
                Dictionary<string, List<string>> usernameMap = null;
                if (!userDryRun)
                {
                    // only adding usernames from OS2skoledata (both active and deleted) and not fetching all users from Google Workspace as it will perform badly
                    // we always check if a username exists before setting it, so no harm done
                    List<string> skoledataUsernames = oS2skoledataService.GetAllUsernames();
                    usernameMap = workspaceService.GenerateUsernameMap(skoledataUsernames);
                    if (usernameMap == null)
                    {
                        throw new Exception("Failed to generate username map from AD. Will not perform sync.");
                    }
                }

                // reset
                workspaceService.InitializeDictionaries();

                // make sure our custom OS2skoledata Schema is present
                workspaceService.UpdateCustomOS2skoledataSchema();

                // ous - institutions
                List<Institution> institutions = oS2skoledataService.GetInstitutions();
                List<string> lockedInstitutionNumbers = institutions.Where(i => i.Locked).Select(i => i.InstitutionNumber).ToList();

                // add locked institutions to global list
                // institutions that are no longer locked will be removed from the list when the full sync is done (to make sure first sync of unlocked institutions is a full sync)
                globalLockedInstitutionNumbers.AddRange(lockedInstitutionNumbers);

                // delete users fully after x days if enabled
                logger.LogInformation("Mainflow - deleting users");
                workspaceService.deleteUsers();

                // suspend users and fetch the usernames from locked institutions and make sure they are not suspended or removed from the global groups
                logger.LogInformation("Mainflow - suspend users");
                List<string> lockedUsernames = oS2skoledataService.GetLockedUsernames();
                List<DBUser> allActiveUsers = new List<DBUser>();
                foreach (Institution institution in institutions)
                {
                    if (!institution.Locked)
                    {
                        List<DBUser> inInstitution = oS2skoledataService.GetUsersForInstitution(institution);

                        // check and remove excluded
                        List<string> excludedRoles = workspaceService.GetExcludedRoles(institution.InstitutionNumber);
                        foreach (DBUser user in inInstitution)
                        {
                            if (!workspaceService.shouldBeExcluded(user, excludedRoles))
                            {
                                allActiveUsers.Add(user);
                            }
                        }
                    }
                }

                workspaceService.DisableInactiveUsersFromRoot(allActiveUsers, lockedUsernames);

                // update institutions
                logger.LogInformation("Mainflow - updating institutions");
                workspaceService.UpdateInstitutions(institutions);

                // fetch all classes and add to map
                Dictionary<string, List<DBGroup>> institutionGroupMap = new Dictionary<string, List<DBGroup>>();
                foreach (Institution institution in institutions)
                {
                    logger.LogInformation("Mainflow - fetching classes " + institution.InstitutionName);
                    List<DBGroup> classes = oS2skoledataService.GetClassesForInstitution(institution);
                    institutionGroupMap.Add(institution.InstitutionNumber, classes);
                }

                // delete groups and prefix drives that are no longer needed (we need to do it before creating/ updating to avoid naming issues)
                logger.LogInformation("Mainflow - delete groups and prefix drives");
                DeleteGroupsAndDrives(institutions, institutionGroupMap);

                logger.LogInformation($"Mainflow - fetching all users from GW");
                List<DBUser> allUsers = new List<DBUser>();
                Dictionary<string, List<DBUser>> institutionUserMap = new Dictionary<string, List<DBUser>>();
                List<Google.Apis.Admin.Directory.directory_v1.Data.User> allGWUsers = workspaceService.ListUsers(null);
                
                if (allGWUsers == null)
                {
                    throw new Exception("Fetched users in all of gw - result was null. Something is wrong");
                } else
                {
                    logger.LogInformation($"Mainflow - fetched all users from GW and found {allGWUsers.Count()} users");
                }

                logger.LogInformation($"Mainflow - fetching all groups from GW");
                List<Group> allOurGWGroups = workspaceService.ListGroups(true);

                if (allOurGWGroups == null)
                {
                    throw new Exception("Fetched groups in all of gw - result was null. Something is wrong");
                }
                else
                {
                    logger.LogInformation($"Mainflow - fetched all groups from GW and found {allOurGWGroups.Count()} groups");
                }

                foreach (Institution institution in institutions)
                {
                    logger.LogInformation("Mainflow - handling classes " + institution.InstitutionName);
                    if (institution.Locked)
                    {
                        logger.LogInformation($"Not updating users and groups for institution {institution.InstitutionNumber}. It is locked due to school change year.");
                        continue;                    
                    }

                    // get institution ous
                    OrgUnit institutionOrgUnit = workspaceService.GetOrgUnit(institution.GoogleWorkspaceId);
                    OrgUnit studentInstitutionOrgUnit = workspaceService.GetOrgUnit(institution.StudentInstitutionGoogleWorkspaceId);
                    OrgUnit employeeInstitutionOrgUnit = workspaceService.GetOrgUnit(institution.EmployeeInstitutionGoogleWorkspaceId);

                    // ous - classes
                    List<DBGroup> classes = institutionGroupMap[institution.InstitutionNumber];
                    workspaceService.UpdateClassesForInstitution(classes, institution, institutionOrgUnit);

                    // users
                    logger.LogInformation("Mainflow - handling users " + institution.InstitutionName);
                    List<DBUser> users = oS2skoledataService.GetUsersForInstitution(institution);
                    institutionUserMap.Add(institution.InstitutionNumber, users);

                    List<string> excludedRoles = workspaceService.GetExcludedRoles(institution.InstitutionNumber);

                    foreach (DBUser user in users)
                    {
                        bool shouldBeExcluded = workspaceService.shouldBeExcluded(user, excludedRoles);
                        if (shouldBeExcluded)
                        {
                            user.IsExcluded = true;
                            logger.LogInformation($"Skipping user with databaseId {user.DatabaseId} and username {user.Username} because it has an excluded role");
                            continue;
                        }

                        // users have to be created somewhere else and have a username before being created in Google Workspace unless their role is included in the setting rolesToBeCreatedDirectlyInGW
                        bool newlyCreatedUsername = false;
                        if (user.Username == null)
                        {
                            if (!userDryRun && workspaceService.CheckIfUserShouldBeCreatedInGW(user))
                            {
                                string username = null;
                                if (cprUsernameMap.ContainsKey(user.Cpr))
                                {
                                    username = cprUsernameMap[user.Cpr];
                                }
                                else
                                {
                                    if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) && user.StilUsername != null)
                                    {
                                        bool exists = workspaceService.AccountExists(user.StilUsername);
                                        if (!exists)
                                        {
                                            username = user.StilUsername;
                                            AddUsernameToMap(user.StilUsername, usernameMap);
                                        }
                                        else
                                        {
                                            username = workspaceService.GenerateUsername(user.Firstname, usernameMap);
                                        }

                                        newlyCreatedUsername = true;
                                    }
                                    else if (usernameStandard.Equals(UsernameStandardType.UNIID))
                                    {
                                        username = user.UniId;
                                    }
                                    else
                                    {
                                        username = workspaceService.GenerateUsername(user.Firstname, usernameMap);
                                        newlyCreatedUsername = true;
                                    }
                                }
                               
                                if (username == null)
                                {
                                    throw new Exception("Failed to generate username for user with databaseId " + user.DatabaseId);
                                }

                                user.Username = username;
                                oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);

                                if (!cprUsernameMap.ContainsKey(user.Cpr))
                                {
                                    cprUsernameMap.Add(user.Cpr, username);
                                }

                                logger.LogInformation($"Generated username {username} for user with databaseId {user.DatabaseId}");
                                
                            } else
                            {
                                if (userDryRun)
                                {
                                    logger.LogInformation($"UserDryRun: Skipping user with databaseId {user.DatabaseId} because it has no username. Would have generated username and created user");
                                    continue;
                                }
                                else
                                {
                                    logger.LogInformation($"Skipping user with databaseId {user.DatabaseId} because it has no username. It has to be created somewhere else first.");
                                    continue;
                                }
                            }
                        }

                        Google.Apis.Admin.Directory.directory_v1.Data.User match = null;
                        if (!newlyCreatedUsername)
                        {
                            match = allGWUsers.Where(u => Object.Equals(u.PrimaryEmail, user.Username + "@" + domain)).FirstOrDefault();
                        }
                        
                        if (match == null)
                        {
                            Google.Apis.Admin.Directory.directory_v1.Data.User createdUser = workspaceService.CreateUser(user, institution, classes, institutionOrgUnit, studentInstitutionOrgUnit, employeeInstitutionOrgUnit);
                            if (!userDryRun)
                            {
                                logger.LogInformation($"Created Account for user with databaseId {user.DatabaseId} and username {user.Username}");
                                allGWUsers.Add(createdUser);
                            }
                        }
                        else
                        {
                            // maybe update and/or move user in AD
                            workspaceService.UpdateAndMoveUser(user, match, institution, classes, institutionOrgUnit, studentInstitutionOrgUnit, employeeInstitutionOrgUnit);
                        }

                        allUsers.Add(user);
                    }

                    logger.LogInformation("Mainflow - handling groups " + institution.InstitutionName);
                    // groups for institution
                    workspaceService.UpdateGroups(institution, users, classes, allOurGWGroups);

                    logger.LogInformation("Mainflow - handling drives " + institution.InstitutionName);
                    // shared drives for institution
                    workspaceService.UpdateSharedDrives(institution, users, classes);
                }

                logger.LogInformation("Mainflow - handling global groups ");
                workspaceService.UpdateGlobalGroups(allUsers, lockedUsernames);

                // set head on not locked institutions
                foreach (Institution institution in institutions)
                {
                    if (!institution.Locked)
                    {
                        oS2skoledataService.SetHead(head, institution.InstitutionNumber);
                    }
                }

                // full sync was a success - update the locked institution list to only have the current locked institutions
                globalLockedInstitutionNumbers = lockedInstitutionNumbers;

                nonCriticalErrorCount = 0;
                stopWatch.Stop();
                logger.LogInformation($"Mainflow - Finished executing FullSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                if (IsCriticalError(e))
                {
                    nonCriticalErrorCount = 0;
                    oS2skoledataService.ReportError(e.Message);
                    logger.LogError(e, "Failed to execute FullSyncJob");
                } else
                {
                    nonCriticalErrorCount++;
                    logger.LogWarning(e, "Failed to execute FullSyncJob. Error marked as not critical");
                    SetFullSync();
                }
            }
        }

        private void DeleteGroupsAndDrives(List<Institution> institutions, Dictionary<string, List<DBGroup>> institutionGroupMap)
        {
            List<string> groupEmails = new List<string>();
            List<string> driveIds = new List<string>();
            foreach (Institution institution in institutions)
            {
                // class groups and drives
                List<DBGroup> classes = institutionGroupMap[institution.InstitutionNumber];
                foreach (DBGroup currentClass in classes)
                {
                    if (currentClass.DriveGoogleWorkspaceId != null)
                    {
                        driveIds.Add(currentClass.DriveGoogleWorkspaceId);
                    }
                    if (currentClass.GroupGoogleWorkspaceEmail != null)
                    {
                        groupEmails.Add(currentClass.GroupGoogleWorkspaceEmail.ToLower());
                    }
                    if (currentClass.GroupOnlyStudentsGoogleWorkspaceEmail != null)
                    {
                        groupEmails.Add(currentClass.GroupOnlyStudentsGoogleWorkspaceEmail.ToLower());
                    }
                }

                // employee group
                if (institution.EmployeeGroupGoogleWorkspaceEmail != null)
                {
                    groupEmails.Add(institution.EmployeeGroupGoogleWorkspaceEmail.ToLower());
                }

                // employee drive
                if (institution.EmployeeDriveGoogleWorkspaceId != null)
                {
                    driveIds.Add(institution.EmployeeDriveGoogleWorkspaceId);
                }

                // employee type groups
                addEmployeeTypeGroups(groupEmails, institution);

                // groups for students in same year
                List<int> classStartYears = workspaceService.GetClassStartYears(classes);
                foreach (int year in classStartYears)
                {
                    string email =  workspaceService.GetEmail(institution, year + "");
                    if (email != null)
                    {
                        groupEmails.Add(email.ToLower());
                    }
                }
            }

            // global group
            groupEmails.Add("alle-ansatte@" + domain.ToLower());

            // fetch the groupEmails from locked institutions to make sure they are not deleted and delete groups that are not needed anymore
            List<string> lockedGroupEmails = oS2skoledataService.GetLockedGroupEmails();
            logger.LogInformation("Before delete groups call");
            workspaceService.DeleteGroups(groupEmails, lockedGroupEmails);
            logger.LogInformation("after delete groups call");

            // fetch the drive ids from locked institutions to make sure they are not deleted and 'delete' drives that are not needed anymore
            logger.LogInformation("Before fetch locked drive ids");
            List<string> lockedDriveIds = oS2skoledataService.GetLockedDriveIds();
            workspaceService.RenameDrivesToDelete(driveIds, lockedDriveIds);
            logger.LogInformation("After fetch locked drive ids");
        }

        private void addEmployeeTypeGroups(List<string> groupEmails, Institution institution)
        {
            string LÆREREmail = workspaceService.GetEmail(institution, "LÆRER");
            if (LÆREREmail != null)
            {
                groupEmails.Add(LÆREREmail.ToLower());
            }
            string PÆDAGOGEmail = workspaceService.GetEmail(institution, "PÆDAGOG");
            if (PÆDAGOGEmail != null)
            {
                groupEmails.Add(PÆDAGOGEmail.ToLower());
            }
            string VIKAREmail = workspaceService.GetEmail(institution, "VIKAR");
            if (VIKAREmail != null)
            {
                groupEmails.Add(VIKAREmail.ToLower());
            }
            string LEDEREmail = workspaceService.GetEmail(institution, "LEDER");
            if (LEDEREmail != null)
            {
                groupEmails.Add(LEDEREmail.ToLower());
            }
            string LEDELSEEmail = workspaceService.GetEmail(institution, "LEDELSE");
            if (LEDELSEEmail != null)
            {
                groupEmails.Add(LEDELSEEmail.ToLower());
            }
            string TAPEmail = workspaceService.GetEmail(institution, "TAP");
            if (TAPEmail != null)
            {
                groupEmails.Add(TAPEmail.ToLower());
            }
            string KONSULENTEmail = workspaceService.GetEmail(institution, "KONSULENT");
            if (KONSULENTEmail != null)
            {
                groupEmails.Add(KONSULENTEmail.ToLower());
            }
            string UNKNOWNEmail = workspaceService.GetEmail(institution, "UNKNOWN");
            if (UNKNOWNEmail != null)
            {
                groupEmails.Add(UNKNOWNEmail.ToLower());
            }
            string PRAKTIKANTEmail = workspaceService.GetEmail(institution, "PRAKTIKANT");
            if (PRAKTIKANTEmail != null)
            {
                groupEmails.Add(PRAKTIKANTEmail.ToLower());
            }
            string EKSTERNEmail = workspaceService.GetEmail(institution, "EKSTERN");
            if (EKSTERNEmail != null)
            {
                groupEmails.Add(EKSTERNEmail.ToLower());
            }
        }

        private void DeltaSync()
        {
            try
            {
                logger.LogInformation("Executing DeltaSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                // add to cpr and username map in case there is multiple institution people with same cpr that should be created in same sync
                // if not in same sync it is handled by the backend, so this map only contains users created in this sync
                Dictionary<string, string> cprUsernameMap = new Dictionary<string, string>();

                // handle usernameMap
                Dictionary<string, List<string>> usernameMap = null;
                if (!userDryRun)
                {
                    // only adding usernames from OS2skoledata and not fetching all users from Google Workspace as it will perform badly
                    // we always check if a username exists before setting it, so no harm done
                    List<string> skoledataUsernames = oS2skoledataService.GetAllUsernames();
                    usernameMap = workspaceService.GenerateUsernameMap(skoledataUsernames);
                    if (usernameMap == null)
                    {
                        throw new Exception("Failed to generate username map from AD. Will not perform sync.");
                    }
                }

                // reset
                workspaceService.InitializeDictionaries();

                List<Institution> institutions = oS2skoledataService.GetInstitutions();
                foreach (Institution institution in institutions)
                {
                    if (institution.Locked || globalLockedInstitutionNumbers.Contains(institution.InstitutionNumber))
                    {
                        logger.LogInformation($"Not performing delta sync on institution with number {institution.InstitutionNumber}. Institution is locked.");
                        if (institution.Locked && !globalLockedInstitutionNumbers.Contains(institution.InstitutionNumber))
                        {
                            globalLockedInstitutionNumbers.Add(institution.InstitutionNumber);
                        }
                        continue;
                    }

                    List<ModificationHistory> changes = oS2skoledataService.GetChangesForInstitution(institution.InstitutionNumber);
                    if (changes.Count() > 0)
                    {
                        changes = changes.OrderByDescending(c => c.Id).ToList();

                        List<ModificationHistory> typePerson = changes.Where(c => c.EntityType.Equals(EntityType.INSTITUTION_PERSON)).ToList();
                        List<ModificationHistory> typeInstitution = changes.Where(c => c.EntityType.Equals(EntityType.INSTITUTION)).ToList();
                        List<ModificationHistory> typeGroup = changes.Where(c => c.EntityType.Equals(EntityType.GROUP)).ToList();

                        List<long> changedPersonIds = typePerson.Select(c => c.EntityId).ToList();
                        List<long> changedInstitutionIds = typeInstitution.Select(c => c.EntityId).ToList();
                        List<long> changedGroupIds = typeGroup.Select(c => c.EntityId).ToList();

                        List<DBUser> changedUsers = oS2skoledataService.GetChangedUsers(changedPersonIds);
                        List<Institution> changedInstitutions = oS2skoledataService.GetChangedInstitutions(changedInstitutionIds);
                        List<DBGroup> changedGroups = oS2skoledataService.GetChangedGroups(changedGroupIds);

                        // handle changes
                        HandleInstitutionChanges(typeInstitution, changedInstitutions);
                        HandleGroupChanges(typeGroup, changedGroups);
                        HandlePersonChanges(typePerson, changedUsers, usernameMap, cprUsernameMap);

                        // setting head last
                        oS2skoledataService.SetHead(changes.First().Id, institution.InstitutionNumber);
                    }
                }

                nonCriticalErrorCount = 0;
                stopWatch.Stop();
                logger.LogInformation($"Finished executing DeltaSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds.");
            }
            catch (Exception e)
            {
                if (e.Message.Equals("Do a full sync"))
                {
                    nonCriticalErrorCount = 0;
                    logger.LogInformation("Too many changes for delta sync. Doing a full sync instead.");
                    SetFullSync();
                }
                else
                {
                    if (IsCriticalError(e))
                    {
                        nonCriticalErrorCount = 0;
                        oS2skoledataService.ReportError(e.Message);
                        logger.LogError(e, "Failed to execute DeltaSyncJob");
                    }
                    else
                    {
                        nonCriticalErrorCount++;
                        logger.LogWarning(e, "Failed to execute DeltaSyncJob. Error marked as not critical");
                    }
                }
            }
        }

        private void HandlePersonChanges(List<ModificationHistory> typePerson, List<DBUser> changedUsers, Dictionary<string, List<string>> usernameMap, Dictionary<string, string> cprUsernameMap)
        {
            // ous - institutions
            List<Institution> institutions = oS2skoledataService.GetInstitutions();
            Dictionary<string, List<DBGroup>> institutionGroupMap = new Dictionary<string, List<DBGroup>>();
            Dictionary<string, OrgUnit> institutionStudentOrgUnitMap = new Dictionary<string, OrgUnit>();
            Dictionary<string, OrgUnit> institutionEmployeeOrgUnitMap = new Dictionary<string, OrgUnit>();
            foreach (Institution institution in institutions)
            {
                if (institution.Locked)
                {
                    continue;
                }

                List<DBGroup> classes = oS2skoledataService.GetClassesForInstitution(institution);
                institutionGroupMap.Add(institution.InstitutionNumber, classes);

                if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
                {
                    OrgUnit studentInstitution = workspaceService.GetOrgUnit(institution.StudentInstitutionGoogleWorkspaceId);
                    OrgUnit employeeInstitution = workspaceService.GetOrgUnit(institution.EmployeeInstitutionGoogleWorkspaceId);

                    institutionStudentOrgUnitMap.Add(institution.InstitutionNumber, studentInstitution);
                    institutionEmployeeOrgUnitMap.Add(institution.InstitutionNumber, employeeInstitution);
                }
            }

            foreach (DBUser user in changedUsers)
            {
                // if user is in one or more locked institutions -> skip
                if (user.Institutions.Any(i => i.Locked || globalLockedInstitutionNumbers.Contains(i.InstitutionNumber)))
                {
                    logger.LogInformation($"Not performing delta sync on user with db id {user.DatabaseId}, at least one of the user's institions are locked due to school year change.");
                    continue;
                }

                logger.LogInformation($"Delta sync handling user with databaseId {user.DatabaseId}");
                bool newlyCreatedUsername = false;
                if (user.Username == null)
                {
                    if (workspaceService.CheckIfUserShouldBeCreatedInGW(user))
                    {
                        string username = null;
                        if (cprUsernameMap.ContainsKey(user.Cpr))
                        {
                            username = cprUsernameMap[user.Cpr];
                        } else
                        {
                            if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) && user.StilUsername != null)
                            {
                                bool exists = workspaceService.AccountExists(user.StilUsername);
                                if (!exists)
                                {
                                    username = user.StilUsername;
                                    AddUsernameToMap(user.StilUsername, usernameMap);
                                }
                                else
                                {
                                    username = workspaceService.GenerateUsername(user.Firstname, usernameMap);
                                }
                                newlyCreatedUsername = true;
                            }
                            else if (usernameStandard.Equals(UsernameStandardType.UNIID))
                            {
                                username = user.UniId;
                            }
                            else
                            {
                                username = workspaceService.GenerateUsername(user.Firstname, usernameMap);
                                newlyCreatedUsername = true;
                            }
                        }

                        if (username == null)
                        {
                            throw new Exception("Failed to generate username for user with databaseId " + user.DatabaseId);
                        }

                        user.Username = username;
                        oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);

                        if (!cprUsernameMap.ContainsKey(user.Cpr))
                        {
                            cprUsernameMap.Add(user.Cpr, username);
                        }

                        logger.LogInformation($"Generated username {username} for user with databaseId {user.DatabaseId}");
                    }
                    else
                    {
                        logger.LogInformation($"Skipping user with databaseId {user.DatabaseId} because it has no username. It has to be created somewhere else first.");
                        continue;
                    }
                }

                

                List<ModificationHistory> userChanges = typePerson.Where(c => c.EntityId == user.DatabaseId).ToList();
                bool create = userChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = userChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = userChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                // get institution ou, null if HierarchyType.INSTITUTION_LAST
                OrgUnit institutionOrgUnit = workspaceService.GetOrgUnit(user.CurrentInstitution.GoogleWorkspaceId);

                OrgUnit studentInstitution = null;
                if (institutionStudentOrgUnitMap.ContainsKey(user.CurrentInstitution.InstitutionNumber))
                {
                    studentInstitution = institutionStudentOrgUnitMap[user.CurrentInstitution.InstitutionNumber];
                }

                OrgUnit employeeInstitution = null;
                if (institutionEmployeeOrgUnitMap.ContainsKey(user.CurrentInstitution.InstitutionNumber))
                {
                    employeeInstitution = institutionEmployeeOrgUnitMap[user.CurrentInstitution.InstitutionNumber];
                }

                // check for match
                Google.Apis.Admin.Directory.directory_v1.Data.User match = null;
                if (!newlyCreatedUsername)
                {
                    match = workspaceService.GetUser(user.Username);
                }

                if (!user.Deleted && create && match == null)
                {
                    // should user be created?
                    List<string> excludedRoles = workspaceService.GetExcludedRoles(user.CurrentInstitution.InstitutionNumber);
                    bool shouldBeExcluded = workspaceService.shouldBeExcluded(user, excludedRoles);
                    if (shouldBeExcluded)
                    {
                        user.IsExcluded = true;
                        continue;
                    }

                    workspaceService.CreateUser(user, user.CurrentInstitution, institutionGroupMap[user.CurrentInstitution.InstitutionNumber], institutionOrgUnit, studentInstitution, employeeInstitution);
                    logger.LogInformation($"Delta sync created account for user with databaseId {user.DatabaseId} and username {user.Username}");
                }
                else if (!user.Deleted && create && match != null)
                {
                    update = true;
                }

                if (!user.Deleted && update && match != null)
                {
                    // maybe update and/or move user
                    workspaceService.UpdateAndMoveUser(user, match, user.CurrentInstitution, institutionGroupMap[user.CurrentInstitution.InstitutionNumber], institutionOrgUnit, studentInstitution, employeeInstitution);
                }

                if (delete && match != null)
                {
                    workspaceService.HandleSuspendUser(user.Username, match);
                }

            }
        }

        private void HandleGroupChanges(List<ModificationHistory> typeGroup, List<DBGroup> changedGroups)
        {
            foreach (DBGroup group in changedGroups)
            {
                if (group.InstitutionLocked || globalLockedInstitutionNumbers.Contains(group.InstitutionNumber))
                {
                    logger.LogInformation($"Not performing delta sync on group with db id {group.DatabaseId}. Institution {group.InstitutionNumber} is locked due to school year change.");
                    return;
                }

                List<ModificationHistory> groupChanges = typeGroup.Where(c => c.EntityId == group.DatabaseId).ToList();
                bool create = groupChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = groupChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = groupChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                OrgUnit entry = workspaceService.GetOrgUnit(group.GoogleWorkspaceId);
                if (create && entry == null)
                {
                    workspaceService.DeltaSyncCreateGroup(group);
                }
                else if (create && entry != null)
                {
                    update = true;
                }

                if (update && entry != null)
                {
                    workspaceService.DeltaSyncUpdateGroup(group, entry);
                }

                if (delete && entry != null)
                {
                    workspaceService.MoveToDeletedOrgUnits(entry.OrgUnitId, entry.OrgUnitPath);
                }

            }
        }

        private void HandleInstitutionChanges(List<ModificationHistory> typeInstitution, List<Institution> changedInstitutions)
        {
            foreach (Institution institution in changedInstitutions)
            {
                if (institution.Locked || globalLockedInstitutionNumbers.Contains(institution.InstitutionNumber))
                {
                    logger.LogInformation($"Not performing delta sync on institution {institution.InstitutionNumber} is locked due to school year change.");
                    return;
                }

                List<ModificationHistory> institutionChanges = typeInstitution.Where(c => c.EntityId == institution.DatabaseId).ToList();
                bool create = institutionChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = institutionChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = institutionChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                OrgUnit entry = workspaceService.GetOrgUnit(institution.GoogleWorkspaceId);
                OrgUnit studentInstitutionOrgUnit = workspaceService.GetOrgUnit(institution.StudentInstitutionGoogleWorkspaceId);
                OrgUnit employeeInstitutionOrgUnit = workspaceService.GetOrgUnit(institution.EmployeeInstitutionGoogleWorkspaceId);

                if (create && (entry == null && studentInstitutionOrgUnit == null && employeeInstitutionOrgUnit == null))
                {
                    workspaceService.DeltaSyncCreateInstitution(institution);
                }
                else if (create && (entry != null || studentInstitutionOrgUnit != null && employeeInstitutionOrgUnit != null))
                {
                    update = true;
                }

                if (update && (entry != null || studentInstitutionOrgUnit != null && employeeInstitutionOrgUnit != null))
                {
                    workspaceService.DeltaSyncUpdateInstitution(institution, entry, studentInstitutionOrgUnit, employeeInstitutionOrgUnit);
                }

                if (delete && (entry != null || studentInstitutionOrgUnit != null && employeeInstitutionOrgUnit != null))
                {
                    if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
                    {
                        workspaceService.MoveToDeletedOrgUnits(entry.OrgUnitId, entry.OrgUnitPath);
                    }
                    else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
                    {
                        workspaceService.MoveToDeletedOrgUnits(studentInstitutionOrgUnit.OrgUnitId, studentInstitutionOrgUnit.OrgUnitPath, "Elever_", studentInstitutionOrgUnit);
                        workspaceService.MoveToDeletedOrgUnits(employeeInstitutionOrgUnit.OrgUnitId, employeeInstitutionOrgUnit.OrgUnitPath, "Medarbejdere_", employeeInstitutionOrgUnit);
                    }
                }
            }
        }

        private bool IsCriticalError(Exception e)
        {
            // after three non critical errors in a row, we need to look at it and therefore log and error
            if (nonCriticalErrorCount > 3)
            {
                return true;
            }

            // sometimes Google workspace just likes to throw errors to mess with us. So ignore some errors
            if (e.Message.Contains("The service admin has thrown an exception. HttpStatusCode is Conflict. Entity already exists."))
            {
                return false;
            }
            else if (e.Message.Contains("The service admin has thrown an exception. HttpStatusCode is InternalServerError. Internal error encountered."))
            {
                return false;
            } 
            else if (e.Message.Contains("A task was canceled")) 
            {
                return false;
            }

            return true;
        }

        private void AddUsernameToMap(string stilUsername, Dictionary<string, List<string>> usernameMap)
        {
            var key = stilUsername.Substring(0, 4);
            bool mapContainsKey = usernameMap.ContainsKey(key);
            if (!mapContainsKey)
            {
                usernameMap.Add(key, new List<string>());
            }
            usernameMap[key].Add(stilUsername);
        }
    }
}
