using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Services.ActiveDirectory.Model;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using os2skoledata_ad_sync.Services.PowerShellRunner;
using System;
using System.Collections.Generic;
using System.DirectoryServices;
using System.DirectoryServices.AccountManagement;
using System.Linq;
using System.Text.RegularExpressions;
using Unidecode.NET;

namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    class ActiveDirectoryService : ServiceBase<ActiveDirectoryService>
    {
        private readonly string rootOU;
        private readonly string emailDomain;
        private readonly string disabledUsersOU;
        private readonly string oUIdField;
        private readonly string rootDeletedOusOu;
        private readonly string employeeOUName;
        private readonly string studentOUName;
        private readonly string studentsWithoutGroupsOUName;
        private readonly string securityGroupOUName;
        private readonly string[] oUsToAlwaysCreate;
        private readonly UsernameStandardType usernameStandard;
        private readonly string usernamePrefix;
        private readonly string cprField;
        private readonly string classOUNameStandard;
        private readonly string institutionOUNameStandard;
        private readonly string securityGroupIdField;
        private readonly string globalStudentSecurityGroupName;
        private readonly string globalEmployeeSecurityGroupName;
        private readonly string allInInstitutionSecurityGroupNameStandard;
        private readonly string allStudentsInInstitutionSecurityGroupNameStandard;
        private readonly string allEmployeesInInstitutionSecurityGroupNameStandard;
        private readonly string classSecurityGroupNameStandard;
        private readonly string securityGroupForEmployeeTypeNameStandard;
        private readonly string securityGroupForYearNameStandard;
        private readonly string[] globallyExcludedRoles;
        private readonly Dictionary<string, string[]> exludedRolesInInstitution;
        private readonly string institutionNumberField;
        private readonly string institutionNameField;
        private readonly string schoolOUName;
        private readonly string daycareOUName;

        private char[] first;
        private char[] second;
        private char[] third;
        private Dictionary<long, string> uniqueIds;

        private readonly PowerShellRunnerService powerShellRunner;

        public ActiveDirectoryService(IServiceProvider sp) : base(sp)
        {
            rootOU = settings.ActiveDirectorySettings.RootOU;
            emailDomain = settings.ActiveDirectorySettings.EmailDomain;
            disabledUsersOU = settings.ActiveDirectorySettings.DisabledUsersOU;
            oUIdField = settings.ActiveDirectorySettings.requiredOUFields.OUIdField;
            rootDeletedOusOu = settings.ActiveDirectorySettings.RootDeletedOusOu;
            employeeOUName = settings.ActiveDirectorySettings.namingSettings.EmployeeOUName;
            studentOUName = settings.ActiveDirectorySettings.namingSettings.StudentOUName;
            studentsWithoutGroupsOUName = settings.ActiveDirectorySettings.namingSettings.StudentsWithoutGroupsOUName;
            securityGroupOUName = settings.ActiveDirectorySettings.namingSettings.SecurityGroupOUName;
            oUsToAlwaysCreate = settings.ActiveDirectorySettings.OUsToAlwaysCreate;
            usernameStandard = settings.ActiveDirectorySettings.usernameSettings.UsernameStandard;
            usernamePrefix = settings.ActiveDirectorySettings.usernameSettings.UsernamePrefix;
            cprField = settings.ActiveDirectorySettings.requiredUserFields.CprField;
            classOUNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassOUNameStandard;
            institutionOUNameStandard = settings.ActiveDirectorySettings.namingSettings.InstitutionOUNameStandard;
            securityGroupIdField = settings.ActiveDirectorySettings.requiredSecurityGroupFields.SecurityGroupIdField;
            globalStudentSecurityGroupName = settings.ActiveDirectorySettings.namingSettings.GlobalStudentSecurityGroupName;
            globalEmployeeSecurityGroupName = settings.ActiveDirectorySettings.namingSettings.GlobalEmployeeSecurityGroupName;
            allInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllInInstitutionSecurityGroupNameStandard;
            allStudentsInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllStudentsInInstitutionSecurityGroupNameStandard;
            allEmployeesInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllEmployeesInInstitutionSecurityGroupNameStandard;
            classSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassSecurityGroupNameStandard;
            securityGroupForEmployeeTypeNameStandard = settings.ActiveDirectorySettings.namingSettings.SecurityGroupForEmployeeTypeNameStandard;
            securityGroupForYearNameStandard = settings.ActiveDirectorySettings.namingSettings.SecurityGroupForYearNameStandard;
            globallyExcludedRoles = settings.ActiveDirectorySettings.filteringSettings.GloballyExcludedRoles;
            exludedRolesInInstitution = settings.ActiveDirectorySettings.filteringSettings.ExludedRolesInInstitution;
            institutionNumberField = settings.ActiveDirectorySettings.optionalUserFields.InstitutionNumberField;
            institutionNameField = settings.ActiveDirectorySettings.optionalUserFields.InstitutionNameField;
            schoolOUName = settings.ActiveDirectorySettings.namingSettings.SchoolOUName;
            daycareOUName = settings.ActiveDirectorySettings.namingSettings.DaycareOUName;

            first = "0123456789abcdefghjklmnpqrstuvxyz".ToCharArray();
            second = "abcdefghjklmnpqrstuvxyz".ToCharArray();
            third = "0123456789".ToCharArray();
            uniqueIds = new Dictionary<long, string>();

            PopulateTable();

            powerShellRunner = sp.GetService<PowerShellRunnerService>();
        }

        public string GenerateUsername(string firstname, Dictionary<string, List<string>> usernameMap)
        {
            string namePart = GetNamePart(firstname);
            string prefix = GetPrefix();
            bool nameFirst = IsNameFirst();
            bool mapContainsKey = usernameMap.ContainsKey(namePart);
            List<string> usernamesWithNamePart = new List<string>();
            if (mapContainsKey)
            {
                usernamesWithNamePart = usernameMap[namePart];
            }

            // 1000 tries is crazy many
            for (int i = 0; i < 1000; i++)
            {
                string username = prefix + ((nameFirst) ? namePart : uniqueIds[i]) + ((nameFirst) ? uniqueIds[i] : namePart);

                if (!usernamesWithNamePart.Contains(username))
                {
                    if (!AccountExists(username))
                    {
                        if (!mapContainsKey)
                        {
                            usernameMap.Add(namePart, new List<string>());
                        }

                        usernameMap[namePart].Add(username);

                        return username;
                    }
                }
            }

            return null;
        }

        public void DisableInactiveUsers(List<User> usersShouldBeInInstitution, Institution institution, List<string> excludedRoles)
        {
            using DirectoryEntry rootOUEntry = new DirectoryEntry(@"LDAP://" + rootOU);
            using DirectoryEntry institutionEntry = GetOUFromId("inst" + institution.InstitutionNumber);
                
            // it only works with usernames because we delete before we create/update
            List<string> usernamesInInstitution = GetAllUsernames(institutionEntry);
            foreach (string username in usernamesInInstitution)
            {
                User user = usersShouldBeInInstitution.Where(u => username.Equals(u.Username)).FirstOrDefault();
                if (user != null)
                {
                    if (shouldBeExcluded(user, excludedRoles))
                    {
                        DisableAccount(username);
                    }
                } else
                {
                    DisableAccount(username);
                }
            }
        }

        public bool shouldBeExcluded(User user, List<string> excludedRoles)
        {
            if (user.Role.Equals(Role.STUDENT))
            {
                if (excludedRoles.Contains(user.StudentRole.ToString()))
                {
                    return true;
                }
            }
            else if (user.Role.Equals(Role.EMPLOYEE))
            {
                foreach (EmployeeRole role in user.EmployeeRoles ?? new List<EmployeeRole>())
                {
                    if (!excludedRoles.Contains(role.ToString()))
                    {
                        return false;
                    }
                }
                return true;
            }
            else if (user.Role.Equals(Role.EXTERNAL))
            {
                if (excludedRoles.Contains(user.ExternalRole.ToString()))
                {
                    return true;
                }
            }
            else
            {
                logger.LogInformation("Unknow role " + user.Role + " for user with username " + user.Username + ". Disabling user / not creating user.");
                return true;
            }

            return false;
        }

        public List<string> GetExcludedRoles(string institutionNumber)
        {
            List<string> result = new List<string>();
            if (globallyExcludedRoles != null)
            {
                result.AddRange(globallyExcludedRoles.ToList());
            }

            if (exludedRolesInInstitution != null)
            {
                if (exludedRolesInInstitution.ContainsKey(institutionNumber))
                {
                    result.AddRange(exludedRolesInInstitution[institutionNumber].ToList());
                }
            }
            return result;
        }

        public string UpdateAndMoveUser(string username, User user, DirectoryEntry entry)
        {
            // check for updates
            bool nameChanges = false;
            bool changes = false;
            bool reactivated = false;
            using PrincipalContext ctx = GetPrincipalContext();
            using UserPrincipal userPrincipal = UserPrincipal.FindByIdentity(ctx, IdentityType.SamAccountName, username);
            if (userPrincipal == null)
            {
                logger.LogError("Failed to update AD user with userId: No account with userId: " + username);
            }
            else
            {
                if (!userPrincipal.GivenName.Equals(user.Firstname))
                {
                    userPrincipal.GivenName = user.Firstname;
                    changes = true;
                    nameChanges = true;
                }
                if (!userPrincipal.Surname.Equals(user.FamilyName))
                {
                    userPrincipal.Surname = user.FamilyName;
                    changes = true;
                    nameChanges = true;
                }
                if (userPrincipal.Enabled.Equals(false))
                {
                    userPrincipal.Enabled = true;
                    changes = true;
                    reactivated = true;
                }

                using DirectoryEntry principalEntry = userPrincipal.GetUnderlyingObject() as DirectoryEntry;

                // optional fields
                string institutionNumbers = String.Join(", ", user.Institutions.Select(i => i.InstitutionNumber).ToArray());
                if (institutionNumberField != null && !institutionNumberField.Equals("") && !object.Equals(principalEntry.Properties[institutionNumberField].Value, institutionNumbers))
                {
                    principalEntry.Properties[institutionNumberField].Value = institutionNumbers;
                    changes = true;
                }

                string institutionNames = String.Join(", ", user.Institutions.Select(i => i.InstitutionName).ToArray());
                if (institutionNameField != null && !institutionNameField.Equals("") && !object.Equals(principalEntry.Properties[institutionNameField].Value, institutionNames))
                {
                    principalEntry.Properties[institutionNameField].Value = institutionNames;
                    changes = true;
                }

                if (changes)
                {
                    userPrincipal.Save();

                    if (nameChanges)
                    {
                        principalEntry.Rename("CN=" + user.Firstname + " " + user.FamilyName + " (" + username + ")");
                    }
                    
                    principalEntry.CommitChanges();
                }
            }

            // make sure we have the right dn
            if (nameChanges || changes)
            {
                entry = GetUserFromCpr(user.Cpr);
            }

            // check if user should be moved
            bool moved = false;
            string cn = entry.Properties["cn"][0].ToString();
            string dn = entry.Properties["distinguishedName"].Value.ToString();
            string ouDN = dn.Replace("CN=" + cn + ",", "");
            using DirectoryEntry currentOU = new DirectoryEntry(@"LDAP://" + ouDN);
            if (user.Role.Equals(Role.STUDENT))
            {
                using DirectoryEntry institutionEntry = GetOUFromId("inst" + user.CurrentInstitutionNumber);
                using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);

                if (user.StudentMainGroups == null || user.StudentMainGroups.Count() == 0)
                {
                    if (!currentOU.Properties["distinguishedName"].Value.ToString().Equals(emptyGroupsOU.Properties["distinguishedName"].Value.ToString()))
                    {
                        MoveUser(username, dn, emptyGroupsOU.Properties["distinguishedName"].Value.ToString());
                        moved = true;
                    }

                } else
                {
                    List<string> possibleDns = new List<string>();
                    foreach (var mainGroup in user.StudentMainGroups)
                    {
                        using DirectoryEntry possible = GetOUFromId(mainGroup);
                        if (possible != null)
                        {
                            possibleDns.Add(possible.Properties["distinguishedName"].Value.ToString());
                        }
                    }

                    // if user is already in one of the possible ous - don't move, else move to first of list
                    if (possibleDns.Count() == 0 && !ouDN.Equals(emptyGroupsOU.Properties["distinguishedName"].Value.ToString()))
                    {
                        MoveUser(username, dn, emptyGroupsOU.Properties["distinguishedName"].Value.ToString());
                        moved = true;
                    } else if (possibleDns.Count() != 0 && !possibleDns.Contains(ouDN))
                    {
                        MoveUser(username, dn, possibleDns[0]);
                        moved = true;
                    }
                }
            }
            else if (user.Role.Equals(Role.EMPLOYEE) || user.Role.Equals(Role.EXTERNAL))
            {
                List<string> possibleDns = new List<string>();
                foreach (var institution in user.Institutions)
                {
                    using var institutionOU = GetOUFromId("inst" + institution.InstitutionNumber);
                    using var employeeOU = GetEmployeeOU(institutionOU);
                    if (employeeOU != null)
                    {
                        possibleDns.Add(employeeOU.Properties["distinguishedName"].Value.ToString());
                    }
                }

                // if user is already in one of the possible ous - don't move, else move to first of list
                if (!possibleDns.Contains(ouDN))
                {
                    MoveUser(username, dn, possibleDns[0]);
                    moved = true;
                }
            }

            // make sure we have the right dn
            if (moved)
            {
                entry = GetUserFromCpr(user.Cpr);
            }

            if (reactivated)
            {
                // run powerShell
                powerShellRunner.Run(username, user.Firstname + " " + user.FamilyName);
            }

            if (changes || nameChanges || moved || reactivated)
            {
                logger.LogInformation($"Updated user with username {username}. AD path = {entry.Properties["distinguishedName"].Value}");
            }

            return entry.Properties["distinguishedName"].Value.ToString();
        }

        public void UpdateSecurityGroups(Institution institution, List<User> users, List<OS2skoledata.Model.Group> classes)
        {
            logger.LogInformation($"Handling security groups for institution {institution.InstitutionName}");
            using var institutionEntry = GetOUFromId("inst" + institution.InstitutionNumber);
            using var securityGroupOU = GetSecurityGroupOUInEntry(institutionEntry);

            // keeping a list of ids for when we need to delete later
            List<string> securityGroupIds = new List<string>();

            // if ADPath is null it means that the user has been excluded
            foreach (OS2skoledata.Model.Group currentClass in classes) {
                List<User> usersInClass = users.Where( u => u.GroupIds.Contains(currentClass.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + currentClass.DatabaseId)) && u.ADPath != null).ToList();
                string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_klasse_" + currentClass.DatabaseId;
                using DirectoryEntry classGroupEntry = UpdateGroup(securityGroupOU, id, GetClassSecurityGroupName(currentClass, institution));
                securityGroupIds.Add(id);
                HandleGroupMembers(classGroupEntry, usersInClass);
            }

            using DirectoryEntry institutionEmployeeGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte", GetInstitutionGroupName("EMPLOYEES", institution));
            using DirectoryEntry institutionStudentGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_elever", GetInstitutionGroupName("STUDENTS", institution));
            using DirectoryEntry institutionAllGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_alle", GetInstitutionGroupName("ALL", institution));

            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_elever");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_alle");

            // all users in institution security group
            List<User> usersInAll = users.Where(u=>u.ADPath != null).ToList();
            HandleGroupMembers(institutionAllGroup, usersInAll);

            // all students in institution security group
            List<User> usersInStudent = users.Where(u => u.ADPath != null && u.Role.Equals(Role.STUDENT)).ToList();
            HandleGroupMembers(institutionStudentGroup, usersInStudent);

            // all employees in institution security group
            List<User> usersInEmployee = users.Where(u => u.ADPath != null && (u.Role.Equals(Role.EMPLOYEE) || u.Role.Equals(Role.EXTERNAL))).ToList();
            HandleGroupMembers(institutionEmployeeGroup, usersInEmployee);

            // securityGroups for adults based on role
            HandleInstitutionEmployeeTypeSecurityGroups(securityGroupIds, securityGroupOU, institution, users);

            // securityGroups for students in same year
            List<int> classStartYears = GetClassStartYears(classes);
            foreach (int year in classStartYears)
            {
                List<User> usersInYear = users.Where(u => u.Role.Equals(Role.STUDENT) && u.ADPath != null && u.StudentMainGroupStartYearForInstitution != 0 && u.StudentMainGroupStartYearForInstitution == year).ToList();
                string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_år_" + year;
                using DirectoryEntry yearGroupEntry = UpdateGroup(securityGroupOU, id, GetYearSecurityGroupName(year, institution));
                securityGroupIds.Add(id);
                HandleGroupMembers(yearGroupEntry, usersInYear);
            }

            // delete other securityGroups
            DeleteSecurityGroups(securityGroupIds, securityGroupOU);

            logger.LogInformation($"Finished handling security groups for institution {institution.InstitutionName}");
        }

        private string GetYearSecurityGroupName(int year, Institution institution)
        {
            string name = securityGroupForYearNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{YEAR}", year + "");

            name = EscapeCharactersForAD(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private List<int> GetClassStartYears(List<OS2skoledata.Model.Group> classes)
        {
            List<int> years = new List<int>();
            foreach (OS2skoledata.Model.Group group in classes)
            {
                if (group.StartYear != 0 && !years.Contains(group.StartYear))
                {
                    years.Add(group.StartYear);
                }
            }
            return years;
        }

        private void HandleInstitutionEmployeeTypeSecurityGroups(List<string> securityGroupIds, DirectoryEntry securityGroupOU, Institution institution, List<User> users)
        {
            using DirectoryEntry institutionEmployeeLærerGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LÆRER", GetInstitutionEmployeeTypeGroupName("LÆRER", institution));
            using DirectoryEntry institutionEmployeePædagogGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PÆDAGOG", GetInstitutionEmployeeTypeGroupName("PÆDAGOG", institution));
            using DirectoryEntry institutionEmployeeVikarGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_VIKAR", GetInstitutionEmployeeTypeGroupName("VIKAR", institution));
            using DirectoryEntry institutionEmployeeLederGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDER", GetInstitutionEmployeeTypeGroupName("LEDER", institution));
            using DirectoryEntry institutionEmployeeLedelseGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDELSE", GetInstitutionEmployeeTypeGroupName("LEDELSE", institution));
            using DirectoryEntry institutionEmployeeTapGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_TAP", GetInstitutionEmployeeTypeGroupName("TAP", institution));
            using DirectoryEntry institutionEmployeeKonsulentGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_KONSULENT", GetInstitutionEmployeeTypeGroupName("KONSULENT", institution));
            using DirectoryEntry institutionEmployeeUnknownGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_UNKNOWN", GetInstitutionEmployeeTypeGroupName("UNKNOWN", institution));
            using DirectoryEntry institutionEmployeePraktikantGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PRAKTIKANT", GetInstitutionEmployeeTypeGroupName("PRAKTIKANT", institution));
            using DirectoryEntry institutionEmployeeEksternGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_EKSTERN", GetInstitutionEmployeeTypeGroupName("EKSTERN", institution));

            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LÆRER");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PÆDAGOG");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_VIKAR");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDER");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDELSE");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_TAP");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_KONSULENT");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_UNKNOWN");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PRAKTIKANT");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_EKSTERN");

            List<User> usersInLærer = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LÆRER)).ToList();
            HandleGroupMembers(institutionEmployeeLærerGroup, usersInLærer);

            List<User> usersInPædagog = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.PÆDAGOG)).ToList();
            HandleGroupMembers(institutionEmployeePædagogGroup, usersInPædagog);

            List<User> usersInVikar = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.VIKAR)).ToList();
            HandleGroupMembers(institutionEmployeeVikarGroup, usersInVikar);

            List<User> usersInLeder = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDER)).ToList();
            HandleGroupMembers(institutionEmployeeLederGroup, usersInLeder);

            List<User> usersInLedelse = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDELSE)).ToList();
            HandleGroupMembers(institutionEmployeeLedelseGroup, usersInLedelse);

            List<User> usersInTAP = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.TAP)).ToList();
            HandleGroupMembers(institutionEmployeeTapGroup, usersInTAP);

            List<User> usersInKonsulent = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.KONSULENT)).ToList();
            HandleGroupMembers(institutionEmployeeKonsulentGroup, usersInKonsulent);

            List<User> usersInPraktikant = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.PRAKTIKANT)).ToList();
            HandleGroupMembers(institutionEmployeePraktikantGroup, usersInPraktikant);

            List<User> usersInEkstern = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.EKSTERN)).ToList();
            HandleGroupMembers(institutionEmployeeEksternGroup, usersInEkstern);

            List<User> usersInUnknown = users.Where(u => u.ADPath != null && ((u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.UNKNOWN)) || (u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.UNKNOWN)))).ToList();
            HandleGroupMembers(institutionEmployeeUnknownGroup, usersInUnknown);
        }

        private string GetInstitutionEmployeeTypeGroupName(string type, Institution institution)
        {
            string name = securityGroupForEmployeeTypeNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{TYPE}", type);

            name = EscapeCharactersForAD(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private void DeleteSecurityGroups(List<string> securityGroupIds, DirectoryEntry securityGroupOU)
        {
            PrincipalContext context = new PrincipalContext(ContextType.Domain, null, securityGroupOU.Properties["distinguishedName"].Value.ToString());
            GroupPrincipal findAllGroups = new GroupPrincipal(context, "*");
            PrincipalSearcher ps = new PrincipalSearcher(findAllGroups);
            foreach (var group in ps.FindAll())
            {
                using DirectoryEntry entry = group.GetUnderlyingObject() as DirectoryEntry;
                string id = entry.Properties[securityGroupIdField].Value.ToString();
                string name = entry.Name;
                if (id.StartsWith("os2skoledata_klasse_") && !securityGroupIds.Contains(id))
                {
                    securityGroupOU.Children.Remove(entry);
                    logger.LogInformation($"Deleted class security group with name {name}");
                }

            }
        }

        private void HandleGroupMembers(DirectoryEntry group, List<User> users)
        {
            logger.LogInformation($"Handling members for group {group.Name}");
            PrincipalContext context = new PrincipalContext(ContextType.Domain, Environment.UserDomainName);
            GroupPrincipal groupPrincipal = GroupPrincipal.FindByIdentity(context, group.Properties["cn"].Value.ToString());
            var members = groupPrincipal.GetMembers().ToList();
            List<string> currentPaths = members.Select(m => m.DistinguishedName).ToList();
            List<string> actualPaths = users.Select(u => u.ADPath).ToList();

            foreach (var member in members)
            {
                var dn = member.DistinguishedName;
                if (!actualPaths.Contains(dn))
                {
                    group.Properties["member"].Remove(dn);
                    logger.LogInformation($"Removed member with dn {dn} from group {group.Name}");
                }
            }

            foreach (User user in users)
            {
                if (!currentPaths.Contains(user.ADPath))
                {
                    group.Properties["member"].Add(user.ADPath);
                    currentPaths.Add(user.ADPath);
                    logger.LogInformation($"Added member with dn {user.ADPath} to group {group.Name}");
                }
            }

            group.CommitChanges();
        }

        public void UpdateGlobalSecurityGroups(Dictionary<string, List<User>> institutionUserMap)
        {
            logger.LogInformation("Handling global security groups");
            using var rootEntry = new DirectoryEntry("LDAP://" + rootOU);
            using var securityGroupOU = GetSecurityGroupOUInEntry(rootEntry);

            using DirectoryEntry globalStudentGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_elever", globalStudentSecurityGroupName);
            using DirectoryEntry globalEmployeeGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_ansatte", globalEmployeeSecurityGroupName);

            List<User> students = new List<User>();
            List<User> employees = new List<User>();
            foreach (List<User> userList in institutionUserMap.Values)
            {
                foreach (User user in userList)
                {
                    // if ADPath is null it means that the user has been excluded
                    if (user.ADPath == null)
                    {
                        continue;
                    }

                    // the ADPath is not necesarily correct anymore because people can have users in multiple institutions and with different names
                    // so fetch the correct one.
                    using DirectoryEntry entry = GetUserFromCpr(user.Cpr);
                    if (entry == null)
                    {
                        continue;
                    }

                    user.ADPath = entry.Properties["distinguishedName"].Value.ToString();

                    if (user.Role.Equals(Role.EMPLOYEE) || user.Role.Equals(Role.EXTERNAL))
                    {
                        employees.Add(user);
                    }
                    else if (user.Role.Equals(Role.STUDENT))
                    {
                        students.Add(user);
                    }
                }
            }

            HandleGroupMembers(globalStudentGroup, students);
            HandleGroupMembers(globalEmployeeGroup, employees);

            logger.LogInformation("Finished handling global security groups");
        }

        public void PopulateTable()
        {
           
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN))
            {
                int idx = 0;

                for (int i = 0; i < third.Length; i++)
                {
                    for (int j = 0; j < third.Length; j++)
                    {
                        for (int k = 0; k < third.Length; k++)
                        {
                            for (int l = 0; l < third.Length; l++)
                            {
                                uniqueIds.Add(idx++, ("" + third[i] + third[j] + third[k] + third[l]));
                            }
                        }
                    }
                }
            } else
            {
                int idx = 0;

                for (int i = 0; i < third.Length; i++)
                {
                    for (int j = 0; j < second.Length; j++)
                    {
                        for (int k = 0; k < first.Length; k++)
                        {
                            uniqueIds.Add(idx++, ("" + third[i] + second[j] + first[k]));
                        }
                    }
                }
            }
        }

        public List<string> GetAllUsernames()
        {
            using var entry = new DirectoryEntry();
            return GetAllUsernames(entry);
        }

        public Dictionary<string, List<string>> GenerateUsernameMap(List<string> allUsernames)
        {
            Dictionary<string, List<string>> map = new Dictionary<string, List<string>>();
            foreach (string username in allUsernames)
            {
                string key = "";
                if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN))
                {
                    try
                    {
                        if (username.Length != 8)
                        {
                            continue;
                        }

                        if (username.Length >= 4)
                        {
                            key = username.Substring(0, 4);
                        } else
                        {
                            key = username;
                        }
                        
                    }
                    catch (Exception e)
                    {
                        logger.LogWarning(e, "Failed to add username " + username + " to username map");
                        continue;
                    }
                } else if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_FIRST))
                {
                    try {
                        string usernameWithoutPrefix = username.Replace(usernamePrefix, "");
                        if (usernameWithoutPrefix.Length < 3)
                        {
                            continue;
                        }
                        key = usernameWithoutPrefix.Substring(0, 3);
                    } catch (Exception e)
                    {
                        logger.LogWarning(e, "Failed to add username " + username + " to username map");
                        continue;
                    }
                } else if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_LAST))
                {
                    try
                    {
                        if (username.Length < 3)
                        {
                            continue;
                        }
                        key = username.Substring(username.Length - 3);
                    }
                    catch (Exception e)
                    {
                        logger.LogWarning(e, "Failed to add username " + username + " to username map");
                        continue;
                    }
                } else {
                    throw new Exception("Failed to generate username map. Unknown usernameStandardType: " + usernameStandard);
                }

                if (!map.ContainsKey(key))
                {
                    map.Add(key, new List<string>());
                }

                map[key].Add(username);
            }

            logger.LogInformation("Generated username map");
            return map;
        }

        public void UpdateInstitutions(List<Institution> institutions)
        {
            logger.LogInformation("Handling institutions");
            using var rootOUEntry = new DirectoryEntry(@"LDAP://" + rootOU);
            List<OUDTO> dtos = institutions.Select(i => new OUDTO(i)).ToList();
            using DirectoryEntry schoolsOU = GetSchoolsOU(rootOUEntry);
            using DirectoryEntry daycareOU = GetDaycareOU(rootOUEntry);
            syncOUs(dtos, rootOUEntry, true, null, schoolsOU, daycareOU);
        }

        public void UpdateClassesForInstitution(List<OS2skoledata.Model.Group> classes, Institution institution)
        {
            logger.LogInformation($"Handling classes for institution {institution.InstitutionName}");
            using DirectoryEntry rootOUEntry = new DirectoryEntry(@"LDAP://" + rootOU);

            using DirectoryEntry institutionEntry = GetOUFromId("inst" + institution.InstitutionNumber);
            if (institutionEntry == null)
            {
                logger.LogError("Failed to update classes for institution with name " + institution.InstitutionName + " and id " + institution.InstitutionNumber + ". Could not find OU in AD with matching id.");
                return;
            }

            using DirectoryEntry studentEntry = GetStudentsOU(institutionEntry);
            if (studentEntry == null)
            {
                logger.LogError("Failed to update classes for institution with name " + institution.InstitutionName + " and id " + institution.InstitutionNumber + ". Could not find student OU under institution in AD with matching id.");
                return;
            }

            List<OUDTO> dtos = classes.Select(c => new OUDTO(c)).ToList();
            syncOUs(dtos, studentEntry, false, institution, null, null);
        }

        public DirectoryEntry GetUserFromCpr(string cpr)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person)({0}={1}))", cprField, cpr);
            return SearchForDirectoryEntry(filter);
        }

        public bool AccountExists(string sAMAccountName)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person)(sAMAccountName={0}))", sAMAccountName);

            using var de = new DirectoryEntry();
            using DirectorySearcher search = new DirectorySearcher(de);
            search.Filter = filter;
            search.PropertiesToLoad.Add("sAMAccountName");

            var result = search.FindOne();
            if (result != null)
            {
                return true;
            }

            return false;
        }

        public string CreateAccount(string username, User user, string institutionNumber)
        {
            // ou
            string ouDN = null;
            if (user.Role.Equals(Role.STUDENT))
            {
                if (user.StudentMainGroups == null || user.StudentMainGroups.Count() == 0)
                {
                    using DirectoryEntry institutionEntry = GetOUFromId("inst" + institutionNumber);
                    using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);
                    ouDN = emptyGroupsOU.Properties["distinguishedName"].Value.ToString();
                } else
                {
                    using DirectoryEntry entry = GetOUFromId(user.StudentMainGroups.First());
                    if (entry != null)
                    {
                        ouDN = entry.Properties["distinguishedName"].Value.ToString();
                    } else
                    {
                        using DirectoryEntry institutionEntry = GetOUFromId("inst" + institutionNumber);
                        using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);
                        ouDN = emptyGroupsOU.Properties["distinguishedName"].Value.ToString();
                    }
                }
            } else if (user.Role.Equals(Role.EMPLOYEE) || user.Role.Equals(Role.EXTERNAL))
            {
                using DirectoryEntry institutionEntry = GetOUFromId("inst"+ institutionNumber);
                using DirectoryEntry employeeOU = GetEmployeeOU(institutionEntry);
                if (employeeOU != null)
                {
                    ouDN = employeeOU.Properties["distinguishedName"].Value.ToString();
                }
            }

            if (ouDN == null)
            {
                throw new Exception("Failed to find ou to create user in.");
            }
            

            // the user should be under root ou
            if (!ouDN.EndsWith(rootOU))
            {
                throw new Exception("The user " + username + " was not created. It has to be placed under the root ou. Tried to place user in " + ouDN);
            }

            using PrincipalContext ctx = GetPrincipalContext(ouDN);
            using UserPrincipal newUser = new UserPrincipal(ctx);
            string name = user.Firstname + " " + user.FamilyName + " (" + username + ")";
            newUser.Name = name.Unidecode();
            newUser.GivenName = user.Firstname;
            newUser.Surname = user.FamilyName;
            newUser.SamAccountName = username;
            newUser.DisplayName = user.Firstname + " " + user.FamilyName;
            newUser.AccountExpirationDate = null;
            newUser.UserPrincipalName = username + emailDomain;
            newUser.Enabled = true;
            newUser.Save();

            // set cpr
            using DirectoryEntry directoryEntry = newUser.GetUnderlyingObject() as DirectoryEntry;
            directoryEntry.Properties[cprField].Value = user.Cpr;

            // optional fields
            if (institutionNumberField != null && !institutionNumberField.Equals(""))
            {
                directoryEntry.Properties[institutionNumberField].Value = String.Join(", ", user.Institutions.Select(i => i.InstitutionNumber).ToArray());
            }
            if (institutionNameField != null && !institutionNameField.Equals(""))
            {
                directoryEntry.Properties[institutionNameField].Value = String.Join(", ", user.Institutions.Select(i => i.InstitutionName).ToArray());
            }
            
            directoryEntry.CommitChanges();

            // run powerShell
            powerShellRunner.Run(username, user.Firstname + " " + user.FamilyName);

            return directoryEntry.Properties["distinguishedName"].Value.ToString();
        }

        public void DeltaSyncCreateGroup(OS2skoledata.Model.Group group)
        {
            using DirectoryEntry institutionEntry = GetOUFromId("inst" + group.InstitutionNumber);
            using DirectoryEntry studentEntry = GetStudentsOU(institutionEntry);

            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
            CreateOU(studentEntry, false, group.GroupId, name);
        }

        public void DeltaSyncCreateInstitution(Institution institution)
        {
            using DirectoryEntry rootEntry = new DirectoryEntry(@"LDAP://" + rootOU);

            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
            CreateOU(rootEntry, true, institution.InstitutionNumber, name);
        }

        public void DeltaSyncUpdateGroup(OS2skoledata.Model.Group group, DirectoryEntry entry)
        {
            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
            UpdateOU(entry, name);
        }

        public void DeltaSyncUpdateInstitution(Institution institution, DirectoryEntry entry)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
            UpdateOU(entry, name);
        }

        private DirectoryEntry GetEmployeeOU(DirectoryEntry institutionOU)
        {
            foreach (DirectoryEntry ou in institutionOU.Children)
            {
                if (ou.Name.Equals("OU=" + employeeOUName))
                {
                    return ou;
                }
                ou.Close();
            }

            DirectoryEntry employeeOU = institutionOU.Children.Add("OU=" + employeeOUName, "OrganizationalUnit");
            employeeOU.CommitChanges();

            return employeeOU;
        }

        private DirectoryEntry GetSchoolsOU(DirectoryEntry rootOU)
        {
            foreach (DirectoryEntry ou in rootOU.Children)
            {
                if (ou.Name.Equals("OU=" + schoolOUName))
                {
                    return ou;
                }
                ou.Close();
            }

            DirectoryEntry schoolsOU = rootOU.Children.Add("OU=" + schoolOUName, "OrganizationalUnit");
            schoolsOU.CommitChanges();

            return schoolsOU;
        }

        private DirectoryEntry GetDaycareOU(DirectoryEntry rootOU)
        {
            foreach (DirectoryEntry ou in rootOU.Children)
            {
                if (ou.Name.Equals("OU=" + daycareOUName))
                {
                    return ou;
                }
                ou.Close();
            }

            DirectoryEntry daycareOU = rootOU.Children.Add("OU=" + daycareOUName, "OrganizationalUnit");
            daycareOU.CommitChanges();

            return daycareOU;
        }

        private DirectoryEntry GetStudentWithoutGroupsOU(DirectoryEntry institutionOU)
        {
            using DirectoryEntry studentsOU = GetStudentsOU(institutionOU);

            foreach (DirectoryEntry ou in studentsOU.Children)
            {
                if (ou.Name.Equals("OU=" + studentsWithoutGroupsOUName))
                {
                    return ou;
                }
                ou.Close();
            }

            
            DirectoryEntry studentWithoutGroupsOU = studentsOU.Children.Add("OU=" + studentsWithoutGroupsOUName, "OrganizationalUnit");
            studentWithoutGroupsOU.CommitChanges();

            return studentWithoutGroupsOU;
        }

        private DirectoryEntry GetStudentsOU(DirectoryEntry institutionOU)
        {
            foreach (DirectoryEntry ou in institutionOU.Children)
            {
                if (ou.Name.Equals("OU=" + studentOUName))
                {
                    return ou;
                }
                ou.Close();
            }

            DirectoryEntry studentsOU = institutionOU.Children.Add("OU=" + studentOUName, "OrganizationalUnit");
            studentsOU.CommitChanges();

            return studentsOU;
        }

        private DirectoryEntry GetGroupFromId(string id)
        {
            var filter = string.Format("(&(objectClass=group)({0}={1}))", securityGroupIdField, id);
            return SearchForDirectoryEntry(filter);
        }

        public DirectoryEntry GetOUFromId(string id)
        {
            var filter = string.Format("(&(objectClass=organizationalUnit)({0}={1}))", oUIdField, id);
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

        private void syncOUs(List<OUDTO> dtos, DirectoryEntry ouToCreateIn, bool institutionLevel, Institution institution, DirectoryEntry schoolsOU, DirectoryEntry daycareOU)
        {
            // delete
            foreach (DirectoryEntry ou in ouToCreateIn.Children)
            {
                if (ou.Name.Equals("OU=" + schoolOUName) || ou.Name.Equals("OU=" + daycareOUName) || ou.Properties["distinguishedName"].Equals(rootDeletedOusOu) || ou.Properties["distinguishedName"].Equals(disabledUsersOU))
                {
                    continue;
                }

                if (ou.Properties[oUIdField].Value != null)
                {
                    string id = ou.Properties[oUIdField].Value.ToString();
                    if (dtos.Where(i => i.Id.Equals(id)).ToList().Count == 0)
                    {
                        MoveToDeletedOUs(ou);
                    }
                }
                ou.Close();
            }

            if (institutionLevel)
            {
                DeleteInstitutionLevelOusFor(dtos, schoolsOU);
                DeleteInstitutionLevelOusFor(dtos, daycareOU);
            }

            // create or update
            foreach (OUDTO dto in dtos)
            {
                logger.LogInformation($"Handling OU with database Id {dto.Id} and name  {dto.Name}");
                using DirectoryEntry match = GetOUFromId(dto.Id);
                string name = "";
                if (institutionLevel)
                {
                    name = GetNameForOU(institutionLevel, dto.Name, dto.StilId, null, null, null, 0);
                }
                else
                {
                    name = GetNameForOU(institutionLevel, institution.InstitutionName, institution.InstitutionNumber, dto.Name, dto.StilId, dto.Level, dto.StartYear);
                }

                if (match == null)
                {
                    if (institutionLevel)
                    {
                        if (dto.Type.Equals(InstitutionType.SCHOOL))
                        {
                            CreateOU(schoolsOU, institutionLevel, dto.Id, name);
                        } else if (dto.Type.Equals(InstitutionType.DAYCARE))
                        {
                            CreateOU(daycareOU, institutionLevel, dto.Id, name);
                        } else if (dto.Type.Equals(InstitutionType.MUNICIPALITY)) {
                            CreateOU(ouToCreateIn, institutionLevel, dto.Id, name);
                        } else
                        {
                            throw new Exception($"Unknown institution type: {institution.Type.ToString()}. Institution with database id: {institution.DatabaseId} and institution number: {institution.InstitutionNumber}");
                        }
                    } else
                    {
                        CreateOU(ouToCreateIn, institutionLevel, dto.Id, name);
                    }
                }
                else
                {
                    // check if ou should be updated
                    UpdateOU(match, name);
                }
            }
        }

        private void DeleteInstitutionLevelOusFor(List<OUDTO> dtos, DirectoryEntry underOu)
        {
            foreach (DirectoryEntry ou in underOu.Children)
            {
                if (ou.Properties[oUIdField].Value != null)
                {
                    string id = ou.Properties[oUIdField].Value.ToString();
                    if (dtos.Where(i => i.Id.Equals(id)).ToList().Count == 0)
                    {
                        MoveToDeletedOUs(ou);
                    }
                }
                ou.Close();
            }
        }

        private void UpdateOU(DirectoryEntry match, string name)
        {
            if (!match.Name.Equals("OU=" + name))
            {
                match.Rename("OU=" + name);
                match.CommitChanges();
                logger.LogInformation($"Updated OU with DN {match.Properties["distinguishedName"].Value} and id {match.Properties[oUIdField].Value}");
            }
        }

        private void CreateOU(DirectoryEntry ouToCreateIn, bool institutionLevel, string id, string name)
        {
            using DirectoryEntry newOU = ouToCreateIn.Children.Add("OU=" + name, "OrganizationalUnit");
            newOU.Properties[oUIdField].Value = id;
            newOU.CommitChanges();
            logger.LogInformation($"Created OU with DN {newOU.Properties["distinguishedName"].Value} and id {id}");

            if (institutionLevel)
            {
                using DirectoryEntry studentOU = newOU.Children.Add("OU=" + studentOUName, "OrganizationalUnit");
                studentOU.CommitChanges();
                logger.LogInformation($"Created student OU with DN {studentOU.Properties["distinguishedName"].Value}");

                using DirectoryEntry studentWithoutGroupsOU = studentOU.Children.Add("OU=" + studentsWithoutGroupsOUName, "OrganizationalUnit");
                studentWithoutGroupsOU.CommitChanges();
                logger.LogInformation($"Created student-without-groups OU with DN {studentWithoutGroupsOU.Properties["distinguishedName"].Value}");

                using DirectoryEntry employeeOU = newOU.Children.Add("OU=" + employeeOUName, "OrganizationalUnit");
                employeeOU.CommitChanges();
                logger.LogInformation($"Created employee OU with DN {employeeOU.Properties["distinguishedName"].Value}");

                using DirectoryEntry securityGroupOU = newOU.Children.Add("OU=" + securityGroupOUName, "OrganizationalUnit");
                securityGroupOU.CommitChanges();
                logger.LogInformation($"Created SecurityGroup OU with DN {securityGroupOU.Properties["distinguishedName"].Value}");

                if (oUsToAlwaysCreate != null)
                {
                    foreach (string alwaysCreate in (oUsToAlwaysCreate != null ? oUsToAlwaysCreate.ToList() : new List<string>()))
                    {
                        using DirectoryEntry alwaysCreateOU = newOU.Children.Add("OU=" + alwaysCreate, "OrganizationalUnit");
                        alwaysCreateOU.CommitChanges();
                        logger.LogInformation($"Created OU configured to always be created with DN {alwaysCreateOU.Properties["distinguishedName"].Value}");
                    }
                }
            }
        }

        private string GetNameForOU(bool institutionLevel, string institutionName, string institutionNumber, string name, string id, string level, int startYear)
        {
            string calculatedName = "";
            if (institutionLevel)
            {
                calculatedName = institutionOUNameStandard
                    .Replace("{INSTITUTION_NAME}", institutionName.Unidecode())
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber);
            }
            else
            {
                calculatedName = classOUNameStandard
                    .Replace("{INSTITUTION_NAME}", institutionName.Unidecode())
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber)
                    .Replace("{CLASS_NAME}", name.Unidecode())
                    .Replace("{CLASS_ID}", id)
                    .Replace("{CLASS_LEVEL}", level);
                
                if (startYear != 0)
                {
                    calculatedName = calculatedName.Replace("{CLASS_YEAR}", startYear + "");
                }
            }

            calculatedName = EscapeCharactersForAD(calculatedName);

            if (calculatedName.Length > 64)
            {
                calculatedName = calculatedName.Substring(0, 64);
            }

            return calculatedName;
        }

        private string EscapeCharactersForAD(string name)
        {
            name = name.Replace("+", "\\+");
            name = name.Replace(",", "\\,");
            name = name.Replace("\"", "\\\"");
            name = name.Replace("<", "\\<");
            name = name.Replace(">", "\\>");
            name = name.Replace(";", "\\;");
            name = name.Replace("#", "\\#");
            name = name.Replace("&", " og ");
            name = name.Replace("/", " ");

            return name;
        }

        public void MoveToDeletedOUs(DirectoryEntry toMove)
        {
            var newOUName = "OU=slettede_ous_" + DateTime.Now.ToString("yyyy_MM_dd");
            DirectoryEntry rootDeletedOusOuEntry = new DirectoryEntry(@"LDAP://" + rootDeletedOusOu);
            DirectoryEntry match = null;
            foreach (DirectoryEntry ou in rootDeletedOusOuEntry.Children)
            {
                if (ou.Name.Equals(newOUName))
                {
                    match = ou;
                }
            }

            if (match == null)
            {
                match = rootDeletedOusOuEntry.Children.Add(newOUName, "OrganizationalUnit");
                match.CommitChanges();
                logger.LogInformation("Created deleted ous ou for today: " + match.Properties["distinguishedName"][0].ToString());
            }

            toMove.MoveTo(match);
            logger.LogInformation("Moved OU with name " + toMove.Name + " to deleted ous ou for today");
            match.Close();
        }

        public bool DisableAccount(string username)
        {
            bool success = false;
            using PrincipalContext ctx = GetPrincipalContext();
            using UserPrincipal user = UserPrincipal.FindByIdentity(ctx, IdentityType.SamAccountName, username);
            if (user == null)
            {
                logger.LogError("Failed to disable AD user with userId: No account with userId: " + username);
            }
            else
            {
                // disable user
                user.Enabled = false;
                user.Save();

                // move user to date specific disabled user group
                string ouDn = CreateOrGetDisabledUserOUDN();
                MoveUser(username, user.DistinguishedName, ouDn);
                logger.LogInformation($"Disabled user with username {username}");

                success = true;
            }

            return success;
        }

        private string CreateOrGetDisabledUserOUDN()
        {
            var newOUName = "OU=disabled_users_" + DateTime.Now.ToString("yyyy_MM_dd");
            using DirectoryEntry rootDisabledOu = new DirectoryEntry(@"LDAP://" + disabledUsersOU);
            foreach (DirectoryEntry ou in rootDisabledOu.Children)
            {
                if (ou.Name.Equals(newOUName))
                {
                    return ou.Properties["distinguishedName"][0].ToString();
                }
            }


            using DirectoryEntry newOU = rootDisabledOu.Children.Add(newOUName, "OrganizationalUnit");
            newOU.CommitChanges();
            var dn = newOU.Properties["distinguishedName"][0].ToString();
            logger.LogInformation("Created disabled ou for today: " + dn);

            return dn;
        }

        private void MoveUser(string userId, string from, string to)
        {
            using DirectoryEntry eLocation = new DirectoryEntry("LDAP://" + from);
            using DirectoryEntry nLocation = new DirectoryEntry("LDAP://" + to);

            eLocation.MoveTo(nLocation);
            
            logger.LogInformation("Moved user " + userId + " from " + from + " to " + to);
        }

        private PrincipalContext GetPrincipalContext(string ldapPath = null)
        {
            if (ldapPath != null)
            {
                return new PrincipalContext(ContextType.Domain, null, ldapPath);
            }

            return new PrincipalContext(ContextType.Domain);
        }

        private string GetNamePart(string firstname)
        {
            int namePartLength = getNamePartLength();
            string name = firstname.ToLower();
            name.Replace("æ", "ae");
            name.Replace("ø", "oe");
            name.Replace("å", "aa");
            name = name.Unidecode();
            name = Regex.Replace(name, "[^a-zA-Z0-9]*", "", RegexOptions.None);

            if (name.Length >= namePartLength)
            {
                return name.Substring(0, namePartLength).ToLower();
            }
            else
            {
                while (name.Length < namePartLength)
                {
                    name = name + "x";
                }
            }

            return name;
        }

        private string GetPrefix()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN))
            {
                return "";
            }
            return usernamePrefix == null ? "" : usernamePrefix;
        }

        private int getNamePartLength()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN))
            {
                return 4;
            }
            return 3;
        }

        private bool IsNameFirst()
        {
            if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_LAST))
            {
                return false;
            }
            return true;
        }

        private List<string> GetAllUsernames(DirectoryEntry entry)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person))");

            using DirectorySearcher search = new DirectorySearcher(entry);
            search.Filter = filter;
            search.PropertiesToLoad.Add("sAMAccountName");
            SearchResultCollection users = search.FindAll();

            if (users != null)
            {
                List<string> usernames = new List<string>();
                foreach (SearchResult user in users)
                {
                    usernames.Add(user.Properties["sAMAccountName"][0].ToString());
                }
                return usernames;
            }

            return null;
        }


        private DirectoryEntry GetSecurityGroupOUInEntry(DirectoryEntry entry)
        {
            DirectoryEntry securityGroupOU = null;
            if (!DirectoryEntry.Exists("LDAP://OU=" + securityGroupOUName + "," + entry.Properties["distinguishedName"].Value.ToString()))
            {
                securityGroupOU = entry.Children.Add("OU=" + securityGroupOUName, "OrganizationalUnit");
                securityGroupOU.CommitChanges();
            }
            else
            {
                securityGroupOU = new DirectoryEntry("LDAP://OU=" + securityGroupOUName + "," + entry.Properties["distinguishedName"].Value.ToString());
            }

            return securityGroupOU;
        }

        private string GetClassSecurityGroupName(OS2skoledata.Model.Group currentClass, Institution institution)
        {
            string name = classSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{CLASS_NAME}", currentClass.GroupName.Unidecode())
                        .Replace("{CLASS_ID}", currentClass.GroupId)
                        .Replace("{CLASS_LEVEL}", currentClass.GroupLevel);


            name = EscapeCharactersForAD(name);

            if (name.Length > 64)
            {
                name = name.Substring(0,64);
            }

            return name;
        }

        private string GetInstitutionGroupName(string type, Institution institution)
        {
            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                default:
                    throw new Exception("Unknown institution security group name standard type: " + type);
            }

            name = EscapeCharactersForAD(name);

            if (name.Length > 64)
            {
                name = name.Substring(0,64);
            }

            return name;
        }


        private DirectoryEntry UpdateGroup(DirectoryEntry securityGroupOU, string id, string name)
        {
            logger.LogInformation($"Checking if security group with name {name} and id {id} should be updated or created");
            DirectoryEntry group = GetGroupFromId(id);

            bool created = false;
            bool updated = false;
            if (group == null)
            {
                group = securityGroupOU.Children.Add("CN=" + name, "group");
                group.Properties[securityGroupIdField].Value = id;
                created = true;
            }
            else
            {
                if (!group.Name.Equals("CN=" + name))
                {
                    group.Rename("CN=" + name);
                    updated = true;
                }
            }

            group.CommitChanges();

            if (created)
            {
                logger.LogInformation($"Created security group with name {name} and id {id}");
            } else if (updated)
            {
                logger.LogInformation($"Updated security group with name {name} and id {id}");
            }

            return group;
        }

    }
}
