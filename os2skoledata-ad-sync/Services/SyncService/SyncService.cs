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
        private bool performFullSyncNext = true;
        private bool resetFullSyncFlag = false;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            activeDirectoryService = sp.GetService<ActiveDirectoryService>();
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

                //all usernames
                var allUsernames = activeDirectoryService.GetAllUsernames();
                if (allUsernames == null)
                {
                    throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
                }
                var usernameMap = activeDirectoryService.GenerateUsernameMap(allUsernames);
                if (usernameMap == null)
                {
                    throw new Exception("Failed to generate username map from AD. Will not perform sync.");
                }

                // ous - institutions
                List<Institution> institutions = oS2skoledataService.GetInstitutions();
                activeDirectoryService.UpdateInstitutions(institutions);

                // pr institution update classes and users
                Dictionary<string, List<User>> institutionUserMap = new Dictionary<string, List<User>>();
                foreach (Institution institution in institutions)
                {
                    // ous - classes
                    List<Group> classes = oS2skoledataService.GetClassesForInstitution(institution);
                    activeDirectoryService.UpdateClassesForInstitution(classes, institution);

                    // users
                    List<User> users = oS2skoledataService.GetUsersForInstitution(institution);
                    institutionUserMap.Add(institution.InstitutionNumber, users);

                    List<string> excludedRoles = activeDirectoryService.GetExcludedRoles(institution.InstitutionNumber);

                    activeDirectoryService.DisableInactiveUsers(users, institution, excludedRoles);

                    foreach (User user in users)
                    {
                        logger.LogInformation($"Checking if user with databaseId {user.DatabaseId} should be created or updated");
                        using DirectoryEntry entry = activeDirectoryService.GetUserFromCpr(user.Cpr);
                        if (entry == null)
                        {
                            // should user be created?
                            bool shouldBeExcluded = activeDirectoryService.shouldBeExcluded(user, excludedRoles);
                            if (shouldBeExcluded)
                            {
                                logger.LogInformation($"Not creating user with databaseId {user.DatabaseId} because it has an excluded role");
                                continue;
                            }

                            // find available username and create
                            string username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                            if (username == null)
                            {
                                throw new Exception("Failed to generate username for user with databaseId " + user.DatabaseId);
                            }

                            user.Username = username;
                            oS2skoledataService.SetUsernameOnUser(username, user.LocalPersonId);
                            logger.LogInformation($"Generated username {username} for user with databaseId {user.DatabaseId}");
                            string path = activeDirectoryService.CreateAccount(username, user, institution.InstitutionNumber);
                            user.ADPath = path;
                            logger.LogInformation($"Created Account for user with databaseId {user.DatabaseId} and username {username}. ADPath = {path}");
                        }
                        else
                        {
                            // update username in OS2skoledata
                            string username = entry.Properties["sAMAccountName"][0].ToString();
                            if (user.Username == null || !username.Equals(user.Username))
                            {
                                user.Username = username;
                                oS2skoledataService.SetUsernameOnUser(username, user.LocalPersonId);
                            }

                            // maybe update and/or move user in AD
                            string path = activeDirectoryService.UpdateAndMoveUser(username, user, entry);
                            user.ADPath = path;
                        }
                    }

                    // security groups for institution
                    activeDirectoryService.UpdateSecurityGroups(institution, users, classes);
                }

                // global security groups
                activeDirectoryService.UpdateGlobalSecurityGroups(institutionUserMap);

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

                List<ModificationHistory> changes = oS2skoledataService.GetChanges();
                if (changes.Count() > 0)
                {
                    //all usernames
                    var allUsernames = activeDirectoryService.GetAllUsernames();
                    if (allUsernames == null)
                    {
                        throw new Exception("Failed to get all usernames from AD. Will not perform delta sync.");
                    }
                    var usernameMap = activeDirectoryService.GenerateUsernameMap(allUsernames);
                    if (usernameMap == null)
                    {
                        throw new Exception("Failed to generate username map from AD. Will not perform delta sync.");
                    }

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
                    HandleInstitutionChanges(typeInstitution, changedInstitutions);
                    HandleGroupChanges(typeGroup, changedGroups);
                    HandlePersonChanges(typePerson, changedUsers, usernameMap);

                    // setting head last
                    oS2skoledataService.SetHead(changes.First().Id);
                }

                stopWatch.Stop();
                logger.LogInformation($"Finsihed executing DeltaSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds. Found {changes.Count()} changes");
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

        private void HandlePersonChanges(List<ModificationHistory> typePerson, List<User> changedUsers, Dictionary<string, List<string>> usernameMap)
        {
            foreach (User user in changedUsers)
            {
                List<ModificationHistory> userChanges = typePerson.Where(c => c.EntityId == user.DatabaseId).ToList();
                bool create = userChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = userChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = userChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                logger.LogInformation($"Delta sync handling user with databaseId {user.DatabaseId}");

                using DirectoryEntry entry = activeDirectoryService.GetUserFromCpr(user.Cpr);
                if (create && entry == null)
                {
                    // should user be created?
                    List<string> excludedRoles = activeDirectoryService.GetExcludedRoles(user.CurrentInstitutionNumber);
                    bool shouldBeExcluded = activeDirectoryService.shouldBeExcluded(user, excludedRoles);
                    if (shouldBeExcluded)
                    {
                        continue;
                    }

                    // find available username and create
                    string username = activeDirectoryService.GenerateUsername(user.Firstname, usernameMap);
                    if (username == null)
                    {
                        throw new Exception("Failed to generate username for user with LocalPersonId " + user.LocalPersonId);
                    }

                    user.Username = username;
                    oS2skoledataService.SetUsernameOnUser(username, user.LocalPersonId);
                    logger.LogInformation($"Delta sync generated username {username} for user with databaseId {user.DatabaseId}");
                    string path = activeDirectoryService.CreateAccount(username, user, user.CurrentInstitutionNumber);
                    user.ADPath = path;
                    logger.LogInformation($"Delta sync created account for user with databaseId {user.DatabaseId} and username {username}. ADPath = {path}");
                }
                else if (create && entry != null)
                {
                    update = true;
                }

                if (update && entry != null)
                {
                    // update username in OS2skoledata
                    string username = entry.Properties["sAMAccountName"][0].ToString();
                    if (user.Username == null || !username.Equals(user.Username))
                    {
                        user.Username = username;
                        oS2skoledataService.SetUsernameOnUser(username, user.LocalPersonId);
                    }

                    // maybe update and/or move user in AD
                    string path = activeDirectoryService.UpdateAndMoveUser(username, user, entry);
                    user.ADPath = path;
                }

                if (delete && entry != null)
                {
                    string username = entry.Properties["sAMAccountName"][0].ToString();
                    activeDirectoryService.DisableAccount(username);
                }

            }
        }

        private void HandleGroupChanges(List<ModificationHistory> typeGroup, List<Group> changedGroups)
        {
            foreach (Group group in changedGroups)
            {
                List<ModificationHistory> groupChanges = typeGroup.Where(c => c.EntityId == group.DatabaseId).ToList();
                bool create = groupChanges.Any(c => c.EventType.Equals(EventType.CREATE));
                bool update = groupChanges.Any(c => c.EventType.Equals(EventType.UPDATE));
                bool delete = groupChanges.Any(c => c.EventType.Equals(EventType.DELETE));

                using DirectoryEntry entry = activeDirectoryService.GetOUFromId(group.GroupId);
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
                    activeDirectoryService.MoveToDeletedOUs(entry);
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

                using DirectoryEntry entry = activeDirectoryService.GetOUFromId(institution.InstitutionNumber);
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
                    activeDirectoryService.MoveToDeletedOUs(entry);
                }

            }
        }
    }
}
