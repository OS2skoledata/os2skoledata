using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.DirectoryServices;
using System.DirectoryServices.AccountManagement;
using System.Linq;

namespace os2skoledata_affiliation_checker.Services.ActiveDirectory
{
    internal class ActiveDirectoryService : ServiceBase<ActiveDirectoryService>
    {
        private readonly string employeeSecurityGroup;
        private readonly string cprField;

        public ActiveDirectoryService(IServiceProvider sp) : base(sp)
        {
            cprField = settings.ActiveDirectorySettings.CprField;
            employeeSecurityGroup = settings.ActiveDirectorySettings.EmployeeSecurityGroupDN;
        }

        public void handleGroup(List<string> memberCprNumbers, string groupDn, string type)
        {
            if (String.IsNullOrEmpty(groupDn))
            {
                return;
            }

            using DirectoryEntry group = GetGroupFromDN(groupDn);
            if (group == null)
            {
                throw new Exception($"Failed to find {type} with dN {groupDn}");
            }

            logger.LogInformation($"Handling members for group {groupDn}");
            PrincipalContext context = new PrincipalContext(ContextType.Domain, Environment.UserDomainName);
            GroupPrincipal groupPrincipal = GroupPrincipal.FindByIdentity(context, IdentityType.Guid, group.Guid.ToString());
            List<Principal> members = new List<Principal>();
            try
            {
                members = groupPrincipal.GetMembers().ToList();
            }
            catch (Exception e)
            {
                logger.LogInformation($"Failed to find current members for group {group.Name}. Maybe there are none. Therefore not checking if members should be removed");
            }

            List<string> currentUsernames = members.Select(m => m.SamAccountName).ToList();

            // find users from the cpr numbers supplied
            List<DirectoryEntry> users = new List<DirectoryEntry>();
            List<string> usernames = new List<string>();
            foreach (var cpr in memberCprNumbers)
            {
                DirectoryEntry entry = GetUserFromCpr(cpr);
                if (entry != null)
                {
                    users.Add(entry);
                    usernames.Add(entry.Properties["sAMAccountName"][0].ToString());
                }
            }

            foreach (var member in members)
            {
                var usernameForMember = member.SamAccountName;
                var dn = member.DistinguishedName;

                if (!usernames.Contains(usernameForMember))
                {
                    group.Properties["member"].Remove(dn);
                    logger.LogInformation($"Removed member with dn {dn} from group {group.Name}");
                }
            }

            foreach (DirectoryEntry user in users)
            {
                string username = user.Properties["sAMAccountName"][0].ToString();
                if (!currentUsernames.Contains(username))
                {
                    string distinguishedName = user.Properties["distinguishedName"].Value.ToString();
                    group.Properties["member"].Add(distinguishedName);
                    logger.LogInformation($"Added member with dn {distinguishedName} to group {group.Name}");

                    currentUsernames.Add(username);
                }
            }
            group.CommitChanges();
        }

        public Dictionary<string, List<string>> FetchAllEmployeesFromAD()
        {
            var dictionary = new Dictionary<string, List<string>>();

            using var directoryEntry = new DirectoryEntry();
            var allProperties = new string[]
            {
                "DistinguishedName",
                "sAMAccountName",
                cprField
            };

            string filter = CreateFilter(
                "!(isDeleted=TRUE)",
                cprField + "=*",
                "memberOf:1.2.840.113556.1.4.1941:=" + employeeSecurityGroup
            );

            using var directorySearcher = new DirectorySearcher(directoryEntry, filter, allProperties, SearchScope.Subtree);
            directorySearcher.PageSize = 1000;

            using var searchResultCollection = directorySearcher.FindAll();
            logger.LogInformation("Found {0} users in Active Directory with a value in {1}", searchResultCollection.Count, cprField);

            foreach (SearchResult searchResult in searchResultCollection)
            {
                var cpr = (string)searchResult.Properties[cprField][0];
                var dn = (string)searchResult.Properties["DistinguishedName"][0];

                List<string> dns = null;
                if (dictionary.ContainsKey(cpr))
                {
                    dns = dictionary[cpr];
                }
                else
                {
                    dns = new List<string>();

                    dictionary.Add(cpr, dns);
                }

                dns.Add(dn);
            }

            return dictionary;
        }

        private string CreateFilter(params string[] filters)
        {
            var allFilters = filters.ToList();
            allFilters.Add("objectClass=user");
            allFilters.Add("!(objectclass=computer)");

            return string.Format("(&{0})", string.Concat(allFilters.Where(x => !string.IsNullOrEmpty(x)).Select(x => '(' + x + ')').ToArray()));
        }

        private DirectoryEntry GetUserFromCpr(string cpr)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person)({0}={1}))", cprField, cpr);
            return SearchForDirectoryEntry(filter);
        }

        private DirectoryEntry GetGroupFromDN(string distinguishedName)
        {
            var filter = string.Format("(&(objectClass=group)(distinguishedName={0}))", distinguishedName);
            return SearchForDirectoryEntry(filter);
        }

        private DirectoryEntry SearchForDirectoryEntry(string filter)
        {
            using DirectoryEntry entry = new DirectoryEntry();
            using DirectorySearcher search = new DirectorySearcher(entry);
            search.Filter = filter;

            var result = search.FindOne();
            if (result != null)
            {
                return result.GetDirectoryEntry();
            }

            return null;
        }
    }
}
