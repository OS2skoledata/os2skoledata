using Google.Apis.Admin.Directory.directory_v1.Data;
using Google.Apis.Drive.v3.Data;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace;
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

        public SyncService(IServiceProvider sp) : base(sp)
        {
            workspaceService = sp.GetService<WorkspaceService>();
            oS2skoledataService = sp.GetService<OS2skoledataService>();
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

                // reset
                workspaceService.InitializeDictionaries();

                // ous - institutions
                List<Institution> institutions = oS2skoledataService.GetInstitutions();
                workspaceService.UpdateInstitutions(institutions);

                List<string> groupIds = new List<string>();
                List<string> driveIds = new List<string>();
                List<DBUser> allUsers = new List<DBUser>();
                Dictionary<string, List<DBUser>> institutionUserMap = new Dictionary<string, List<DBUser>>();
                foreach (Institution institution in institutions)
                {
                    // get institution ou
                    OrgUnit institutionOrgUnit = workspaceService.GetOrgUnit(institution.GoogleWorkspaceId);
                    if (institutionOrgUnit == null)
                    {
                        throw new Exception("Could not find institution orgunit for intitution with number " + institution.InstitutionNumber);
                    }

                    // ous - classes
                    List<DBGroup> classes = oS2skoledataService.GetClassesForInstitution(institution);
                    workspaceService.UpdateClassesForInstitution(classes, institution, institutionOrgUnit);

                    // users
                    List<DBUser> users = oS2skoledataService.GetUsersForInstitution(institution);
                    institutionUserMap.Add(institution.InstitutionNumber, users);

                    List<string> excludedRoles = workspaceService.GetExcludedRoles(institution.InstitutionNumber);

                    workspaceService.DisableInactiveUsers(users, institution, excludedRoles);

                    foreach (DBUser user in users)
                    {
                        // users have to be created somewhere else and have a username before being created in Google Workspace
                        if (user.Username == null)
                        {
                            logger.LogInformation($"Skipping user with databaseId {user.DatabaseId} because it has no username. It has to be created somewhere else first.");
                            continue;
                        }

                        var match = workspaceService.GetUser(user.Username);
                        if (match == null)
                        {
                            // should user be created?
                            bool shouldBeExcluded = workspaceService.shouldBeExcluded(user, excludedRoles);
                            if (shouldBeExcluded)
                            {
                                user.IsExcluded = true;
                                logger.LogInformation($"Not creating user with databaseId {user.DatabaseId} because it has an excluded role");
                                continue;
                            }

                            workspaceService.CreateUser(user, institution, classes, institutionOrgUnit);
                            logger.LogInformation($"Created Account for user with databaseId {user.DatabaseId} and username {user.Username}");
                        }
                        else
                        {
                            // maybe update and/or move user in AD
                            workspaceService.UpdateAndMoveUser(user, match, institution, classes, institutionOrgUnit);
                        }

                        allUsers.Add(user);
                    }

                    // groups for institution
                    workspaceService.UpdateGroups(institution, users, classes, groupIds);

                    // shared drives for institution
                    workspaceService.UpdateSharedDrives(institution, users, classes, driveIds);
                }

                workspaceService.UpdateGlobalGroups(allUsers, groupIds);

                // delete groups that are not needed anymore
                workspaceService.DeleteGroups(groupIds);

                // 'delete' drives that are not needed anymore
                workspaceService.RenameDrivesToDelete(driveIds);

                // set head
                oS2skoledataService.SetHead(head);

                stopWatch.Stop();
                logger.LogInformation($"Finsihed executing FullSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                oS2skoledataService.ReportError(e.Message);
                logger.LogError(e, "Failed to execute FullSyncJob");
            }
        }

        private void DeltaSync()
        {
            try
            {
                logger.LogInformation("Executing DeltaSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                // reset
                workspaceService.InitializeDictionaries();

                List<ModificationHistory> changes = oS2skoledataService.GetChanges();
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
                    HandlePersonChanges(typePerson, changedUsers);

                    // setting head last
                    oS2skoledataService.SetHead(changes.First().Id);
                }

                stopWatch.Stop();
                logger.LogInformation($"Finsihed executing DeltaSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds.");
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
                    oS2skoledataService.ReportError(e.Message);
                    logger.LogError(e, "Failed to execute DeltaSyncJob");
                }
            }
        }

        private void HandlePersonChanges(List<ModificationHistory> typePerson, List<DBUser> changedUsers)
        {
            // ous - institutions
            List<Institution> institutions = oS2skoledataService.GetInstitutions();
            Dictionary<string, List<DBGroup>> institutionGroupMap = new Dictionary<string, List<DBGroup>>();
            foreach (Institution institution in institutions)
            {
                List<DBGroup> classes = oS2skoledataService.GetClassesForInstitution(institution);
                institutionGroupMap.Add(institution.InstitutionNumber, classes);
            }

            foreach (DBUser user in changedUsers)
            {
                logger.LogInformation($"Delta sync handling user with databaseId {user.DatabaseId}");
                if (user.Username == null)
                {
                    continue;
                }

                List<ModificationHistory> userChanges = typePerson.Where(c => c.EntityId == user.DatabaseId).ToList();
                bool create = userChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = userChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = userChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                // get institution ou
                OrgUnit institutionOrgUnit = workspaceService.GetOrgUnit(user.CurrentInstitution.GoogleWorkspaceId);
                if (institutionOrgUnit == null)
                {
                    throw new Exception("Could not find institution orgunit for intitution with number " + user.CurrentInstitution.InstitutionNumber);
                }

                // check for match
                var match = workspaceService.GetUser(user.Username);
                if (create && match == null)
                {
                    // should user be created?
                    List<string> excludedRoles = workspaceService.GetExcludedRoles(user.CurrentInstitution.InstitutionNumber);
                    bool shouldBeExcluded = workspaceService.shouldBeExcluded(user, excludedRoles);
                    if (shouldBeExcluded)
                    {
                        user.IsExcluded = true;
                        continue;
                    }

                    workspaceService.CreateUser(user, user.CurrentInstitution, institutionGroupMap[user.CurrentInstitution.InstitutionNumber], institutionOrgUnit);
                    logger.LogInformation($"Delta sync created account for user with databaseId {user.DatabaseId} and username {user.Username}");
                }
                else if (create && match != null)
                {
                    update = true;
                }

                if (update && match != null)
                {
                    // maybe update and/or move user
                    workspaceService.UpdateAndMoveUser(user, match, user.CurrentInstitution, institutionGroupMap[user.CurrentInstitution.InstitutionNumber], institutionOrgUnit);
                }

                if (delete && match != null)
                {
                    workspaceService.HandleSuspendUser(user.Username);
                }

            }
        }

        private void HandleGroupChanges(List<ModificationHistory> typeGroup, List<DBGroup> changedGroups)
        {
            foreach (DBGroup group in changedGroups)
            {
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
                List<ModificationHistory> institutionChanges = typeInstitution.Where(c => c.EntityId == institution.DatabaseId).ToList();
                bool create = institutionChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = institutionChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = institutionChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                OrgUnit entry = workspaceService.GetOrgUnit(institution.GoogleWorkspaceId);
                if (create && entry == null)
                {
                    workspaceService.DeltaSyncCreateInstitution(institution);
                }
                else if (create && entry != null)
                {
                    update = true;
                }

                if (update && entry != null)
                {
                    workspaceService.DeltaSyncUpdateInstitution(institution, entry);
                }

                if (delete && entry != null)
                {
                    workspaceService.MoveToDeletedOrgUnits(entry.OrgUnitId, entry.OrgUnitPath);
                }

            }
        }
    }
}
