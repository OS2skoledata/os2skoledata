using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Services.ActiveDirectory;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.DirectoryServices;
using System.Linq;

namespace os2skoledata_ad_sync.Services.OS2skoledata
{
    internal class SyncService : ServiceBase<SyncService>
    {
        private readonly ActiveDirectoryService activeDirectoryService;
        private readonly OS2skoledataService oS2skoledataService;
        private readonly bool moveUsersEnabled;
        private bool performFullSyncNext = true;
        private bool resetFullSyncFlag = false;
        private bool useUsernameAsKey = false;
        private readonly bool createOUHierarchy;
        private readonly bool dryRun;
        private readonly UsernameStandardType usernameStandard;
        private List<string> globalLockedInstitutionNumbers;
        private readonly List<string> usersToInclude;
        private readonly UsernameKeyType usernameKeyType;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            activeDirectoryService = sp.GetService<ActiveDirectoryService>();
            oS2skoledataService = sp.GetService<OS2skoledataService>();

            moveUsersEnabled = settings.ActiveDirectorySettings.MoveUsersEnabled;
            useUsernameAsKey = settings.ActiveDirectorySettings.UseUsernameAsKey;
            createOUHierarchy = settings.ActiveDirectorySettings.CreateOUHierarchy;
            dryRun = settings.ActiveDirectorySettings.DryRun;
            usernameStandard = settings.ActiveDirectorySettings.usernameSettings.UsernameStandard;
            usersToInclude = settings.ActiveDirectorySettings.UsersToInclude == null ? new List<string>() : settings.ActiveDirectorySettings.UsersToInclude;
            globalLockedInstitutionNumbers = new List<string>();
            usernameKeyType = settings.ActiveDirectorySettings.UsernameKeyType;
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
                logger.LogInformation("Executing FullSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                // get head
                long head = oS2skoledataService.GetHead();

                Dictionary<string, List<string>> usernameMap = null;
                if (moveUsersEnabled)
                {
                    //all usernames
                    var allUsernames = activeDirectoryService.GetAllUsernames();
                    if (allUsernames == null)
                    {
                        throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
                    }

                    List<string> skoledataUsernames = oS2skoledataService.GetAllUsernames();
                    usernameMap = activeDirectoryService.GenerateUsernameMap(allUsernames, skoledataUsernames);
                    if (usernameMap == null)
                    {
                        throw new Exception("Failed to generate username map from AD. Will not perform sync.");
                    }
                }

                // ous - institutions
                List<Institution> institutions = oS2skoledataService.GetInstitutions();
                List<string> lockedInstitutionNumbers = institutions.Where(i => i.Locked).Select(i => i.InstitutionNumber).ToList();
                List<string> lockedUsernames = oS2skoledataService.GetLockedUsernames();
                List<string> keepAliveUsernames = oS2skoledataService.GetKeepAliveUsernames();

                // add locked institutions to global list
                // institutions that are no longer locked will be removed from the list when the full sync is done (to make sure first sync of unlocked institutions is a full sync)
                globalLockedInstitutionNumbers.AddRange(lockedInstitutionNumbers);

                // delete users fully after x days if enabled
                activeDirectoryService.deleteUsers();

                // check and disable users
                if (moveUsersEnabled)
                {
                    List<User> allUsers = new List<User>();
                    foreach (Institution institution in institutions)
                    {
                        if (!institution.Locked)
                        {
                            List<User> inInstitution = oS2skoledataService.GetUsersForInstitution(institution);

                            // check and remove excluded
                            List<string> excludedRoles = activeDirectoryService.GetExcludedRoles(institution.InstitutionNumber);
                            foreach (User user in inInstitution)
                            {
                                if (!activeDirectoryService.shouldBeExcluded(user, excludedRoles))
                                {
                                    allUsers.Add(user);
                                }
                            }
                        }
                    }

                    if (!createOUHierarchy)
                    {
                        activeDirectoryService.DisableInactiveUsersNoHierarchy(allUsers, lockedUsernames, keepAliveUsernames);
                    } else
                    {
                        activeDirectoryService.DisableInactiveUsersFromRoot(allUsers, lockedUsernames, keepAliveUsernames);
                    }
                }

                if (createOUHierarchy)
                {
                    activeDirectoryService.UpdateInstitutions(institutions);
                }

                // this map is used to keep the newest known path pr username
                Dictionary<string, string> usernameADPathMap = new Dictionary<string, string>();

                // pr institution update classes and users
                HashSet<string> allClassLevels = new HashSet<string>();
                List<string> allSecurityGroupIds = new List<string>();
                List<string> allRenamedGroupIds = new List<string>();
                Dictionary<string, List<User>> institutionUserMap = new Dictionary<string, List<User>>();
                foreach (Institution institution in institutions)
                {
                    if (institution.Locked)
                    {
                        logger.LogInformation($"Skipping institution with number {institution.InstitutionNumber}. Institution is locked due to school year change.");
                        continue;
                    }

                    // ous - classes
                    List<Group> classes = oS2skoledataService.GetClassesForInstitution(institution);
                    if (createOUHierarchy)
                    {
                        activeDirectoryService.UpdateClassesForInstitution(classes, institution);
                    }

                    // users
                    List<User> users = new List<User>();
                    if (moveUsersEnabled)
                    {
                        users = oS2skoledataService.GetUsersForInstitution(institution);
                        institutionUserMap.Add(institution.InstitutionNumber, users);

                        List<string> excludedRoles = activeDirectoryService.GetExcludedRoles(institution.InstitutionNumber);

                        foreach (User user in users)
                        {
                            logger.LogInformation($"Checking if user with databaseId {user.DatabaseId} should be created or updated");

                            if (useUsernameAsKey)
                            {
                                using DirectoryEntry entry = activeDirectoryService.GetUserFromUsername(UsernameKeyType.UNI_ID.Equals(usernameKeyType) ? user.UniId : user.Username);
                                HandleFullSyncUser(user, entry, institution, excludedRoles, usernameMap, usernameADPathMap, keepAliveUsernames);
                            } else
                            {
                                using DirectoryEntry entry = activeDirectoryService.GetUserFromCpr(user.Cpr);
                                HandleFullSyncUser(user, entry, institution, excludedRoles, usernameMap, usernameADPathMap, keepAliveUsernames);
                            }
                        }
                    }
                    
                    // security groups for institution
                    if (createOUHierarchy)
                    {
                        activeDirectoryService.UpdateSecurityGroups(institution, users, classes, null, null, usernameADPathMap, allClassLevels);
                    } else
                    {
                        activeDirectoryService.UpdateSecurityGroups(institution, users, classes, allSecurityGroupIds, allRenamedGroupIds, usernameADPathMap, allClassLevels);
                    }
                }

                if (!createOUHierarchy)
                {
                    activeDirectoryService.DeleteSecurityGroups(allSecurityGroupIds, null, lockedInstitutionNumbers, allRenamedGroupIds);
                }
 
                // global security groups
                activeDirectoryService.UpdateGlobalSecurityGroups(institutionUserMap, lockedInstitutionNumbers, usernameADPathMap, institutions, allClassLevels);

                // move keep alive users
                activeDirectoryService.MoveKeepAliveUsers(keepAliveUsernames);

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

                stopWatch.Stop();
                logger.LogInformation($"Finsihed executing FullSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                oS2skoledataService.ReportError(e.Message + "\n" + e.StackTrace);
                logger.LogError(e, "Failed to execute FullSyncJob");
            }
        }

        private void HandleFullSyncUser(User user, DirectoryEntry entry, Institution institution, List<string> excludedRoles, Dictionary<string, List<string>> usernameMap, Dictionary<string,string> usernameADPathMap, List<string> keepAliveUsernames)
        {
            bool shouldBeExcluded = activeDirectoryService.shouldBeExcluded(user, excludedRoles);
            if (shouldBeExcluded)
            {
                logger.LogInformation($"skipping user with databaseId {user.DatabaseId} and username {user.Username} because it has an excluded role");
                return;
            }

            if (entry == null)
            {
                if (usersToInclude.Count() != 0)
                {
                    logger.LogInformation($"Not creating user with databaseId {user.DatabaseId}. Only handeling users on include list.");
                    return;
                }

                // find available username and create

                string username = null;
                if (user.ReservedUsername != null && !activeDirectoryService.AccountExists(user.ReservedUsername))
                {
                    // we can't add this username to the usernameMap as we do not know the form of it - the municipality create the reservedUsername
                    username = user.ReservedUsername;
                }
                else if (user.StilUsername != null && (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM)))
                {
                    bool exists = activeDirectoryService.AccountExists(user.StilUsername);
                    if (!exists)
                    {
                        username = user.StilUsername;
                        AddUsernameToMap(user.StilUsername, usernameMap);
                    }
                    else
                    {
                        username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                    }
                }
                else
                {
                    username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                }
                
                if (username == null)
                {
                    throw new Exception("Failed to generate username for user with databaseId " + user.DatabaseId);
                }

                user.Username = username;
                oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);
                logger.LogInformation($"Generated username {username} for user with databaseId {user.DatabaseId}");
                string path = activeDirectoryService.CreateAccount(username, user, institution.InstitutionNumber);
                user.ADPath = path;
                AddPathToMap(path, username, usernameADPathMap);
                
                if (!dryRun)
                {
                    logger.LogInformation($"Created Account for user with databaseId {user.DatabaseId} and username {username}. ADPath = {path}");
                }
            }
            else
            {
                // update username in OS2skoledata
                string username = entry.Properties["sAMAccountName"][0].ToString();
                if (user.Username == null || !username.Equals(user.Username))
                {
                    user.Username = username;
                    oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);
                }

                // maybe update and/or move user in AD
                string path = activeDirectoryService.UpdateAndMoveUser(username, user, entry, keepAliveUsernames);
                user.ADPath = path;
                AddPathToMap(path, username, usernameADPathMap);
            }
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

        private void AddPathToMap(string path, string username, Dictionary<string, string> usernameADPathMap)
        {
            if (usernameADPathMap.ContainsKey(username))
            {
                usernameADPathMap[username] = path;
            } else
            {
                usernameADPathMap.Add(username, path);
            }
        }

        private void DeltaSync()
        {
            try
            {
                logger.LogInformation("Executing DeltaSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                Dictionary<string, List<string>> usernameMap = null;
                if (moveUsersEnabled)
                {
                    //all usernames
                    var allUsernames = activeDirectoryService.GetAllUsernames();
                    if (allUsernames == null)
                    {
                        throw new Exception("Failed to get all usernames from AD. Will not perform delta sync.");
                    }

                    List<string> skoledataUsernames = oS2skoledataService.GetAllUsernames();
                    usernameMap = activeDirectoryService.GenerateUsernameMap(allUsernames, skoledataUsernames);
                    if (usernameMap == null)
                    {
                        throw new Exception("Failed to generate username map from AD. Will not perform delta sync.");
                    }
                }

                var totalChanges = 0;
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
                        totalChanges = totalChanges + changes.Count();
                        changes = changes.OrderByDescending(c => c.Id).ToList();

                        List<ModificationHistory> typePerson = changes.Where(c => c.EntityType.Equals(EntityType.INSTITUTION_PERSON)).ToList();
                        List<ModificationHistory> typeInstitution = changes.Where(c => c.EntityType.Equals(EntityType.INSTITUTION)).ToList();
                        List<ModificationHistory> typeGroup = changes.Where(c => c.EntityType.Equals(EntityType.GROUP)).ToList();

                        List<long> changedPersonIds = typePerson.Select(c => c.EntityId).ToList();
                        List<long> changedInstitutionIds = typeInstitution.Select(c => c.EntityId).ToList();
                        List<long> changedGroupIds = typeGroup.Select(c => c.EntityId).ToList();

                        List<User> changedUsers = oS2skoledataService.GetChangedUsers(changedPersonIds);
                        List<Institution> changedInstitutions = oS2skoledataService.GetChangedInstitutions(changedInstitutionIds);
                        List<Group> changedGroups = oS2skoledataService.GetChangedGroups(changedGroupIds);

                        // handle changes
                        if (createOUHierarchy)
                        {
                            HandleInstitutionChanges(typeInstitution, changedInstitutions);
                            HandleGroupChanges(typeGroup, changedGroups);
                        }

                        if (moveUsersEnabled)
                        {
                            HandlePersonChanges(typePerson, changedUsers, usernameMap);
                        }

                        // setting head last
                        oS2skoledataService.SetHead(changes.First().Id, institution.InstitutionNumber);
                    }
                }

                stopWatch.Stop();
                logger.LogInformation($"Finsihed executing DeltaSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds. Found {totalChanges} changes");
            }
            catch (Exception e)
            {
                if (e.Message.Equals("Do a full sync"))
                {
                    logger.LogInformation("Too many changes for delta sync. Doing a full sync instead.");
                    SetFullSync();
                }
                else
                {
                    oS2skoledataService.ReportError("Delta sync error:\n" + e.Message + "\n" + e.StackTrace);
                    logger.LogError(e, "Failed to execute DeltaSyncJob");
                }
            }
        }

        private void HandlePersonChanges(List<ModificationHistory> typePerson, List<User> changedUsers, Dictionary<string, List<string>> usernameMap)
        {
            List<string> keepAliveUsernames = oS2skoledataService.GetKeepAliveUsernames();
            Dictionary<string, string> usernameADPathMap = new Dictionary<string, string>();
            foreach (User user in changedUsers)
            {
                // if user is in one or more locked institutions -> skip
                if (user.Institutions.Any(i => i.Locked || globalLockedInstitutionNumbers.Contains(i.InstitutionNumber)))
                {
                    logger.LogInformation($"Not performing delta sync on user with db id {user.DatabaseId}, at least one of the user's institions are locked due to school year change.");
                    continue;
                }

                List<ModificationHistory> userChanges = typePerson.Where(c => c.EntityId == user.DatabaseId).ToList();
                bool create = userChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = userChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = userChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                logger.LogInformation($"Delta sync handling user with databaseId {user.DatabaseId}");

                if (useUsernameAsKey)
                {
                    using DirectoryEntry entry = activeDirectoryService.GetUserFromUsername(UsernameKeyType.UNI_ID.Equals(usernameKeyType) ? user.UniId : user.Username);
                    HandleOnePersonChanges(entry, user, create, update, delete, usernameMap, usernameADPathMap, keepAliveUsernames);
                } else
                {
                    using DirectoryEntry entry = activeDirectoryService.GetUserFromCpr(user.Cpr);
                    HandleOnePersonChanges(entry, user, create, update, delete, usernameMap, usernameADPathMap, keepAliveUsernames);
                }
            }
        }

        private void HandleOnePersonChanges(DirectoryEntry entry, User user, bool create, bool update, bool delete, Dictionary<string, List<string>> usernameMap, Dictionary<string, string> usernameADPathMap, List<string> keepAliveUsernames)
        {
            // if any of the users institutions are locked, we will not perform a delta sync
            if (user.Institutions.Any(i => i.Locked))
            {
                logger.LogInformation($"Not performing delta sync on user with db id {user.DatabaseId}, at least one of the user's institions are locked due to school year change.");
                return;
            }

            if (!user.Deleted && create && entry == null)
            {
                // should user be created?
                List<string> excludedRoles = activeDirectoryService.GetExcludedRoles(user.CurrentInstitutionNumber);
                bool shouldBeExcluded = activeDirectoryService.shouldBeExcluded(user, excludedRoles);
                if (shouldBeExcluded)
                {
                    return;
                }

                // find available username and create
                string username = null;
                if (user.ReservedUsername != null && !activeDirectoryService.AccountExists(user.ReservedUsername))
                {
                    // we can't add this username to the usernameMap as we do not know the form of it - the municipality create the reservedUsername
                    username = user.ReservedUsername;
                }
                else if (user.StilUsername != null && (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM)))
                {
                    bool exists = activeDirectoryService.AccountExists(user.StilUsername);
                    if (!exists)
                    {
                        username = user.StilUsername;
                        AddUsernameToMap(user.StilUsername, usernameMap);
                    }
                    else
                    {
                        username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                    }
                }
                else
                {
                    username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                }

                if (username == null)
                {
                    throw new Exception("Failed to generate username for user with databaseId " + user.DatabaseId);
                }

                user.Username = username;
                oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);
                logger.LogInformation($"Delta sync generated username {username} for user with databaseId {user.DatabaseId}");
                string path = activeDirectoryService.CreateAccount(username, user, user.CurrentInstitutionNumber);
                user.ADPath = path;
                AddPathToMap(path, username, usernameADPathMap);
                logger.LogInformation($"Delta sync created account for user with databaseId {user.DatabaseId} and username {username}. ADPath = {path}");
            }
            else if (!user.Deleted && create && entry != null)
            {
                update = true;
            }

            if (!user.Deleted && update && entry != null)
            {
                // update username in OS2skoledata
                string username = entry.Properties["sAMAccountName"][0].ToString();
                if (user.Username == null || !username.Equals(user.Username))
                {
                    user.Username = username;
                    oS2skoledataService.SetUsernameOnUser(username, user.DatabaseId);
                }

                // maybe update and/or move user in AD
                string path = activeDirectoryService.UpdateAndMoveUser(username, user, entry, keepAliveUsernames);
                user.ADPath = path;
                AddPathToMap(path, username, usernameADPathMap);
            }

            if (delete && entry != null)
            {
                if ( user.Institutions.Count == 0 || (user.Institutions.Count == 1 && user.Institutions.Select(i => i.InstitutionNumber).ToList().Contains(user.CurrentInstitutionNumber)))
                {
                    bool allowDisabling = true;
                    string username = entry.Properties["sAMAccountName"][0].ToString();

                    if (keepAliveUsernames != null && keepAliveUsernames.Contains(username))
                    {
                        allowDisabling = false;
                    }

                    if (allowDisabling)
                    {
                        activeDirectoryService.DisableAccount(username);
                    }
                }
            }
        }

        private void HandleGroupChanges(List<ModificationHistory> typeGroup, List<Group> changedGroups)
        {
            foreach (Group group in changedGroups)
            {
                if (globalLockedInstitutionNumbers.Contains(group.InstitutionNumber))
                {
                    continue;
                }

                List<ModificationHistory> groupChanges = typeGroup.Where(c => c.EntityId == group.DatabaseId).ToList();
                bool create = groupChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = groupChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = groupChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                using DirectoryEntry entry = activeDirectoryService.GetOUFromId("" + group.DatabaseId);
                if (create && entry == null)
                {
                    activeDirectoryService.DeltaSyncCreateGroup(group);
                }
                else if (create && entry != null)
                {
                    update = true;
                }

                if (update && entry != null)
                {
                    activeDirectoryService.DeltaSyncUpdateGroup(group, entry);
                }

                if (delete && entry != null)
                {
                    activeDirectoryService.MoveToDeletedOUs(entry, activeDirectoryService.GetNameForOU(true, group.InstitutionName, group.InstitutionNumber, group.InstitutionAbbreviation, null, null, null, 0, null));
                }

            }
        }

        private void HandleInstitutionChanges(List<ModificationHistory> typeInstitution, List<Institution> changedInstitutions)
        {
            foreach (Institution institution in changedInstitutions)
            {
                if (globalLockedInstitutionNumbers.Contains(institution.InstitutionNumber))
                {
                    continue;
                }

                List<ModificationHistory> institutionChanges = typeInstitution.Where(c => c.EntityId == institution.DatabaseId).ToList();
                bool create = institutionChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = institutionChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = institutionChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                using DirectoryEntry entry = activeDirectoryService.GetOUFromId("inst" + institution.InstitutionNumber);
                if (create && entry == null)
                {
                    activeDirectoryService.DeltaSyncCreateInstitution(institution);
                }
                else if (create && entry != null)
                {
                    update = true;
                }

                if (update && entry != null)
                {
                    activeDirectoryService.DeltaSyncUpdateInstitution(institution, entry);
                }

                if (delete && entry != null)
                {
                    activeDirectoryService.MoveToDeletedOUs(entry, activeDirectoryService.GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, institution.Abbreviation, null, null, null, 0, null));
                }

            }
        }
    }
}
