using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Services.ActiveDirectory.Model;
using os2skoledata_ad_sync.Services.OS2skoledata;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using os2skoledata_ad_sync.Services.PowerShellRunner;
using System;
using System.Collections.Generic;
using System.DirectoryServices;
using System.DirectoryServices.AccountManagement;
using System.Linq;
using System.Reflection.Emit;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Channels;
using Unidecode.NET;
using static Microsoft.ApplicationInsights.MetricDimensionNames.TelemetryContext;
using Group = os2skoledata_ad_sync.Services.OS2skoledata.Model.Group;
using User = os2skoledata_ad_sync.Services.OS2skoledata.Model.User;

namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    class ActiveDirectoryService : ServiceBase<ActiveDirectoryService>
    {
        private readonly string rootOU;
        private readonly string keepAliveOU;
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
        private readonly int randomStandardLetterCount;
        private readonly int randomStandardNumberCount;
        private readonly string cprField;
        private readonly string classOUNameStandard;
        private readonly string classOUNameStandardNoClassYear;
        private readonly string institutionOUNameStandard;
        private readonly string securityGroupIdField;
        private readonly string securityGroupInstitutionNumberField;
        private readonly string globalStudentSecurityGroupName;
        private readonly string globalEmployeeSecurityGroupName;
        private readonly string globalSecurityGroupForEmployeeTypeSchoolNameStandard;
        private readonly string globalSecurityGroupForEmployeeTypeDaycareNameStandard;
        private readonly string globalSecurityGroupForEmployeeTypeFUNameStandard;
        private readonly string allInInstitutionSecurityGroupNameStandard;
        private readonly string allStudentsInInstitutionSecurityGroupNameStandard;
        private readonly string allEmployeesInInstitutionSecurityGroupNameStandard;
        private readonly string classSecurityGroupNameStandard;
        private readonly string classSecurityGroupNameStandardNoClassYear;
        private readonly string classStudentsSecurityGroupNameStandard;
        private readonly string classStudentsSecurityGroupNameStandardNoClassYear;
        private readonly string classEmployeesSecurityGroupNameStandard;
        private readonly string classEmployeesSecurityGroupNameStandardNoClassYear;
        private readonly string otherGroupsÅrgangSecurityGroupNameStandard;
        private readonly string otherGroupsRetningSecurityGroupNameStandard;
        private readonly string otherGroupsHoldSecurityGroupNameStandard;
        private readonly string otherGroupsSFOSecurityGroupNameStandard;
        private readonly string otherGroupsTeamSecurityGroupNameStandard;
        private readonly string otherGroupsAndetSecurityGroupNameStandard;
        private readonly string securityGroupForEmployeeTypeNameStandard;
        private readonly string securityGroupForYearNameStandard;
        private readonly string securityGroupForLevelNameStandard;
        private readonly string[] globallyExcludedRoles;
        private readonly Dictionary<string, string[]> exludedRolesInInstitution;
        private readonly string institutionNumberField;
        private readonly string institutionNameField;
        private readonly string institutionAbbreviationField;
        private readonly string uniIdField;
        private readonly string mailField;
        private readonly string schoolOUName;
        private readonly string daycareOUName;
        private readonly string fuOUName;
        private readonly bool moveUsersEnabled;
        private readonly bool dryRun;
        private readonly bool useUsernameAsKey;
        private readonly string usernameKeyField;
        private readonly UsernameKeyType usernameKeyType;
        private readonly bool useDanishCharacters;
        private readonly bool createOUHierarchy;
        private readonly Dictionary<string, string> institutionUserOUPath;
        private readonly string allSecurityGroupsOU;
        private readonly string adFieldForOS2skoledataMark;
        private readonly List<string> institutionWhitelist;
        private readonly bool setSamAccountName;
        private readonly string samAccountPrefix;
        private readonly bool samAccountUseInstitutionType;
        private readonly List<string> usersToIgnore;
        private readonly List<string> usersToInclude;
        private readonly string os2skoledataMark;
        private readonly string stilRolesField;
        private readonly string ignoreField;
        private readonly string studentStartYearField;
        private readonly string currentInstitutionField;
        private readonly string securityGroupMailField;
        private readonly string securityGroupMailDomain;
        private readonly bool securityGroupOverwriteExistingMail;
        private readonly string securityGroupNormalMailNameStandard;
        private readonly string securityGroupNoLineMailNameStandard;
        private readonly string securityGroupNoLineNoYearMailNameStandard;
        private readonly string securityGroupForEmployeeTypeMailStandard;
        private readonly string securityGroupForEmployeesMailStandard;
        private readonly string otherGroupsÅrgangSecurityGroupMailStandard;
        private readonly string otherGroupsRetningSecurityGroupMailStandard;
        private readonly string otherGroupsHoldSecurityGroupMailStandard;
        private readonly string otherGroupsSFOSecurityGroupMailStandard;
        private readonly string otherGroupsTeamSecurityGroupMailStandard;
        private readonly string otherGroupsAndetSecurityGroupMailStandard;
        private readonly string multipleCprExcludedGroupDn;
        private readonly string globalSecurityGroupForLevelNameStandard;
        private readonly bool deleteDisabledUsersFully;
        private readonly int daysBeforeDeletionStudent;
        private readonly int daysBeforeDeletionEmployee;
        private readonly int daysBeforeDeletionExternal;
        private readonly string globalRoleField;
        private readonly string disabledDateField;
        private readonly bool StudentAndClassGroupsSchoolsOnly;

        private char[] first;
        private char[] second;
        private char[] third;
        private Dictionary<long, string> uniqueIds;
        private Random random;

        private readonly PowerShellRunnerService powerShellRunner;
        private readonly OS2skoledataService os2SkoledataService;

        public ActiveDirectoryService(IServiceProvider sp) : base(sp)
        {
            rootOU = settings.ActiveDirectorySettings.RootOU;
            keepAliveOU = settings.ActiveDirectorySettings.KeepAliveOU;
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
            randomStandardLetterCount = settings.ActiveDirectorySettings.usernameSettings.RandomStandardLetterCount;
            randomStandardNumberCount = settings.ActiveDirectorySettings.usernameSettings.RandomStandardNumberCount;
            cprField = settings.ActiveDirectorySettings.requiredUserFields.CprField;
            classOUNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassOUNameStandard;
            classOUNameStandardNoClassYear = settings.ActiveDirectorySettings.namingSettings.ClassOUNameStandardNoClassYear;
            institutionOUNameStandard = settings.ActiveDirectorySettings.namingSettings.InstitutionOUNameStandard;
            securityGroupIdField = settings.ActiveDirectorySettings.requiredSecurityGroupFields.SecurityGroupIdField;
            securityGroupInstitutionNumberField = settings.ActiveDirectorySettings.requiredSecurityGroupFields.InstitutionNumberField;
            globalStudentSecurityGroupName = settings.ActiveDirectorySettings.namingSettings.GlobalStudentSecurityGroupName;
            globalEmployeeSecurityGroupName = settings.ActiveDirectorySettings.namingSettings.GlobalEmployeeSecurityGroupName;
            globalSecurityGroupForEmployeeTypeSchoolNameStandard = settings.ActiveDirectorySettings.namingSettings.GlobalSecurityGroupForEmployeeTypeSchoolNameStandard;
            globalSecurityGroupForEmployeeTypeDaycareNameStandard = settings.ActiveDirectorySettings.namingSettings.GlobalSecurityGroupForEmployeeTypeDaycareNameStandard;
            globalSecurityGroupForEmployeeTypeDaycareNameStandard = settings.ActiveDirectorySettings.namingSettings.GlobalSecurityGroupForEmployeeTypeFUNameStandard;
            allInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllInInstitutionSecurityGroupNameStandard;
            allStudentsInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllStudentsInInstitutionSecurityGroupNameStandard;
            allEmployeesInInstitutionSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.AllEmployeesInInstitutionSecurityGroupNameStandard;
            classSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassSecurityGroupNameStandard;
            classSecurityGroupNameStandardNoClassYear = settings.ActiveDirectorySettings.namingSettings.ClassSecurityGroupNameStandardNoClassYear;
            classStudentsSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassStudentsSecurityGroupNameStandard;
            classStudentsSecurityGroupNameStandardNoClassYear = settings.ActiveDirectorySettings.namingSettings.ClassStudentsSecurityGroupNameStandardNoClassYear;
            classEmployeesSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.ClassEmployeesSecurityGroupNameStandard;
            classEmployeesSecurityGroupNameStandardNoClassYear = settings.ActiveDirectorySettings.namingSettings.ClassEmployeesSecurityGroupNameStandardNoClassYear;
            otherGroupsÅrgangSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsÅrgangSecurityGroupNameStandard;
            otherGroupsRetningSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsRetningSecurityGroupNameStandard;
            otherGroupsHoldSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsHoldSecurityGroupNameStandard;
            otherGroupsSFOSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsSFOSecurityGroupNameStandard;
            otherGroupsTeamSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsTeamSecurityGroupNameStandard;
            otherGroupsAndetSecurityGroupNameStandard = settings.ActiveDirectorySettings.namingSettings.OtherGroupsAndetSecurityGroupNameStandard;
            securityGroupForEmployeeTypeNameStandard = settings.ActiveDirectorySettings.namingSettings.SecurityGroupForEmployeeTypeNameStandard;
            securityGroupForYearNameStandard = settings.ActiveDirectorySettings.namingSettings.SecurityGroupForYearNameStandard;
            securityGroupForLevelNameStandard = settings.ActiveDirectorySettings.namingSettings.SecurityGroupForLevelNameStandard;
            globallyExcludedRoles = settings.ActiveDirectorySettings.filteringSettings.GloballyExcludedRoles;
            exludedRolesInInstitution = settings.ActiveDirectorySettings.filteringSettings.ExludedRolesInInstitution;
            institutionNumberField = settings.ActiveDirectorySettings.requiredUserFields.InstitutionNumberField;
            institutionNameField = settings.ActiveDirectorySettings.optionalUserFields.InstitutionNameField;
            institutionAbbreviationField = settings.ActiveDirectorySettings.optionalUserFields.InstitutionAbbreviationField;
            uniIdField = settings.ActiveDirectorySettings.optionalUserFields.UNIIdField;
            mailField = settings.ActiveDirectorySettings.optionalUserFields.MailField;
            schoolOUName = settings.ActiveDirectorySettings.namingSettings.SchoolOUName;
            daycareOUName = settings.ActiveDirectorySettings.namingSettings.DaycareOUName;
            fuOUName = settings.ActiveDirectorySettings.namingSettings.FUOUName;
            moveUsersEnabled = settings.ActiveDirectorySettings.MoveUsersEnabled;
            dryRun = settings.ActiveDirectorySettings.DryRun;
            useUsernameAsKey = settings.ActiveDirectorySettings.UseUsernameAsKey;
            usernameKeyField = settings.ActiveDirectorySettings.UsernameKeyField;
            usernameKeyType = settings.ActiveDirectorySettings.UsernameKeyType;
            useDanishCharacters = settings.ActiveDirectorySettings.UseDanishCharacters;
            createOUHierarchy = settings.ActiveDirectorySettings.CreateOUHierarchy;
            institutionUserOUPath = settings.ActiveDirectorySettings.InstitutionUserOUPath;
            allSecurityGroupsOU = settings.ActiveDirectorySettings.AllSecurityGroupsOU;
            adFieldForOS2skoledataMark = settings.ActiveDirectorySettings.ADFieldForOS2skoledataMark;
            institutionWhitelist = settings.OS2skoledataSettings.InstitutionWhitelist;
            setSamAccountName = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? false : settings.ActiveDirectorySettings.optionalSecurityGroupFields.SetSamAccountName;
            samAccountPrefix = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.SamAccountPrefix;
            samAccountUseInstitutionType = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? false : settings.ActiveDirectorySettings.optionalSecurityGroupFields.SamAccountUseInstitutionType;
            usersToIgnore = settings.ActiveDirectorySettings.UsersToIgnore == null ? new List<string>() : settings.ActiveDirectorySettings.UsersToIgnore;
            usersToInclude = settings.ActiveDirectorySettings.UsersToInclude == null ? new List<string>() : settings.ActiveDirectorySettings.UsersToInclude;
            os2skoledataMark = "OS2skoledata";
            stilRolesField = settings.ActiveDirectorySettings.optionalUserFields.STILRolesField;
            ignoreField = settings.ActiveDirectorySettings.optionalUserFields.IgnoreField;
            studentStartYearField = settings.ActiveDirectorySettings.optionalUserFields.StudentStartYearField;
            currentInstitutionField = settings.ActiveDirectorySettings.optionalUserFields.CurrentInstitutionField;
            securityGroupMailField = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.MailField;
            securityGroupMailDomain = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.MailDomain;
            securityGroupOverwriteExistingMail = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? false : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OverwriteExistingMail;
            securityGroupNormalMailNameStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.NormalMailNameStandard;
            securityGroupNoLineMailNameStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.NoLineNameStandard;
            securityGroupNoLineNoYearMailNameStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.NoLineNoYearNameStandard;
            securityGroupForEmployeeTypeMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.SecurityGroupForEmployeeTypeMailStandard;
            securityGroupForEmployeesMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.SecurityGroupForEmployeesMailStandard;
            otherGroupsÅrgangSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsÅrgangSecurityGroupMailStandard;
            otherGroupsRetningSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsRetningSecurityGroupMailStandard;
            otherGroupsHoldSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsHoldSecurityGroupMailStandard;
            otherGroupsSFOSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsSFOSecurityGroupMailStandard;
            otherGroupsTeamSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsTeamSecurityGroupMailStandard;
            otherGroupsAndetSecurityGroupMailStandard = settings.ActiveDirectorySettings.optionalSecurityGroupFields == null ? null : settings.ActiveDirectorySettings.optionalSecurityGroupFields.OtherGroupsAndetSecurityGroupMailStandard;
            multipleCprExcludedGroupDn = settings.ActiveDirectorySettings.MultipleCprExcludedGroupDn;
            globalSecurityGroupForLevelNameStandard = settings.ActiveDirectorySettings.namingSettings.GlobalSecurityGroupForLevelNameStandard;
            deleteDisabledUsersFully = settings.ActiveDirectorySettings.DeleteDisabledUsersFully;
            daysBeforeDeletionStudent = settings.ActiveDirectorySettings.DaysBeforeDeletionStudent;
            daysBeforeDeletionEmployee = settings.ActiveDirectorySettings.DaysBeforeDeletionEmployee;
            daysBeforeDeletionExternal = settings.ActiveDirectorySettings.DaysBeforeDeletionExternal;
            globalRoleField = settings.ActiveDirectorySettings.optionalUserFields.GlobalRoleField;
            disabledDateField = settings.ActiveDirectorySettings.optionalUserFields.DisabledDateField;
            StudentAndClassGroupsSchoolsOnly = settings.ActiveDirectorySettings.StudentAndClassGroupsSchoolsOnly;

            first = "23456789abcdefghjkmnpqrstuvxyz".ToCharArray();
            second = "abcdefghjkmnpqrstuvxyz".ToCharArray();
            third = "23456789".ToCharArray();
            uniqueIds = new Dictionary<long, string>();
            random = new Random();

            PopulateTable();
            if (!String.IsNullOrEmpty(ignoreField))
            {
                List<string> additionalUsersToIgnore = GetAllUsernamesWithIgnoreFieldFilled();
                usersToIgnore.AddRange(additionalUsersToIgnore);
            }

            powerShellRunner = sp.GetService<PowerShellRunnerService>();
            os2SkoledataService = sp.GetService<OS2skoledataService>();
        }

        public void deleteUsers()
        {
            if (deleteDisabledUsersFully && moveUsersEnabled && !dryRun)
            {
                DateTime studentDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionStudent);
                DateTime employeeDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionEmployee);
                DateTime externalDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionExternal);

                List<DirectoryEntry> disabledUsers = GetDisabledUsers(disabledUsersOU);
                foreach (var user in disabledUsers)
                {
                    string role = user.Properties[globalRoleField]?.Value?.ToString();
                    string date = user.Properties[disabledDateField]?.Value?.ToString();

                    if (string.IsNullOrEmpty(role) || string.IsNullOrEmpty(date))
                    {
                        continue; // skip if no role or date
                    }

                    bool shouldDelete = false;
                    if (DateTime.TryParse(date, out DateTime parsedDateTime))
                    {
                        if ((Role.STUDENT.ToString().Equals(role) && parsedDateTime < studentDaysAgo) ||
                            (Role.EMPLOYEE.ToString().Equals(role) && parsedDateTime < employeeDaysAgo) ||
                            (Role.EXTERNAL.ToString().Equals(role) && parsedDateTime < externalDaysAgo))
                        {
                            shouldDelete = true;
                        }
                    }

                    if (shouldDelete)
                    {
                        string username = user.Properties["sAMAccountName"]?.Value.ToString();
                        user.DeleteTree();
                        user.CommitChanges();
                        logger.LogInformation($"User with username {username} was fully deleted. Role: {role} - disabled date: {date}");
                        
                    }
                }
            }
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
                string username;
                if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM))
                {
                    username = namePart + GetRandomNumber(randomStandardNumberCount);
                }
                else
                {
                    username = prefix + ((nameFirst) ? namePart : uniqueIds[i]) + ((nameFirst) ? uniqueIds[i] : namePart);
                }

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

        public void DisableInactiveUsersFromRoot(List<User> users, List<string> lockedUsernames, List<string> keepAliveUsernames)
        {
            using DirectoryEntry rootOUEntry = new DirectoryEntry(@"LDAP://" + rootOU);
            using DirectoryEntry deletedOusEntry = new DirectoryEntry(@"LDAP://" + rootDeletedOusOu);
            using DirectoryEntry disabledUsersEntry = new DirectoryEntry(@"LDAP://" + disabledUsersOU);

            // it only works with usernames because we delete before we create/update
            List<string> usernamesInAD = new List<string>();
            usernamesInAD.AddRange(GetAllUsernames(rootOUEntry, false));
            usernamesInAD.AddRange(GetAllUsernames(deletedOusEntry, false));
            usernamesInAD.AddRange(GetAllUsernames(disabledUsersEntry, false));

            if (!string.IsNullOrEmpty(keepAliveOU))
            {
                using DirectoryEntry keepAliveOUEntry = new DirectoryEntry(@"LDAP://" + keepAliveOU);
                usernamesInAD.AddRange(GetAllUsernames(keepAliveOUEntry, false));
            }

            foreach (string username in usernamesInAD)
            {
                // if user is in locked institution, skip
                if (lockedUsernames != null && lockedUsernames.Contains(username))
                {
                    continue;
                }

                // if user is a keep alive user, skip
                if (keepAliveUsernames != null && keepAliveUsernames.Contains(username))
                {
                    continue;
                }

                if (usersToIgnore.Contains(username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(username)))
                {
                    continue;
                }

                User user = users.Where(u => username.Equals(u.Username)).FirstOrDefault();
                if (user == null)
                {
                    DisableAccount(username);
                }
            }
        }

        public void DisableInactiveUsersNoHierarchy(List<User> users, List<string> lockedUsernames, List<string> keepAliveUsernames)
        {
            // it only works with usernames because we delete before we create/update
            // only return usernames that previously has been marked by OS2skoledata because no OU hierarchy - but from the entire AD
            List<string> usernamesInAD = GetAllUsernames(new DirectoryEntry(), true);

            foreach (string username in usernamesInAD)
            {
                // if user is in locked institution, skip
                if (lockedUsernames != null && lockedUsernames.Contains(username))
                {
                    continue;
                }

                // if user is a keep alive user, skip
                if (keepAliveUsernames != null && keepAliveUsernames.Contains(username))
                {
                    continue;
                }

                if (usersToIgnore.Contains(username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(username)))
                {
                    continue;
                }

                User user = users.Where(u => username.Equals(u.Username)).FirstOrDefault();
                if (user == null)
                {
                    DisableAccount(username);
                }
            }
        }

        public bool shouldBeExcluded(User user, List<string> excludedRoles)
        {
            if (user.GlobalRole.Equals(Role.STUDENT))
            {
                if (excludedRoles.Contains(user.GlobalStudentRole.ToString()))
                {
                    return true;
                }
            }
            else if (user.GlobalRole.Equals(Role.EMPLOYEE))
            {
                foreach (EmployeeRole role in user.GlobalEmployeeRoles ?? new List<EmployeeRole>())
                {
                    if (!excludedRoles.Contains(role.ToString()))
                    {
                        return false;
                    }
                }
                return true;
            }
            else if (user.GlobalRole.Equals(Role.EXTERNAL))
            {
                if (excludedRoles.Contains(user.GlobalExternalRole.ToString()))
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
                result.AddRange(globallyExcludedRoles.Select(g => g.ToUpper()).ToList());
            }

            if (exludedRolesInInstitution != null)
            {
                if (exludedRolesInInstitution.ContainsKey(institutionNumber))
                {
                    result.AddRange(exludedRolesInInstitution[institutionNumber].Select(g => g.ToUpper()).ToList());
                }
            }
            return result;
        }

        public string UpdateAndMoveUser(string username, User user, DirectoryEntry entry, List<string> keepAliveUsernames)
        {
            if (usersToIgnore.Contains(username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(username)))
            {
                logger.LogInformation("Ignoring " + username);
                return null;
            }

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
                var firstname = user.Firstname.Replace(",", "");
                var familyname = user.FamilyName.Replace(",", "");
                if (!object.Equals(userPrincipal.GivenName, firstname))
                {
                    userPrincipal.GivenName = firstname;
                    logger.LogInformation($"Updated given name on user with username {username} and db id {user.DatabaseId} to {firstname}");
                    changes = true;
                    nameChanges = true;
                }
                if (!object.Equals(userPrincipal.Surname, familyname))
                {
                    userPrincipal.Surname = familyname;
                    logger.LogInformation($"Updated surname on user with username {username} and db id {user.DatabaseId} to {familyname}");
                    changes = true;
                    nameChanges = true;
                }
                var fullname = firstname + " " + familyname;
                if (!object.Equals(userPrincipal.DisplayName, fullname))
                {
                    userPrincipal.DisplayName = fullname;
                    logger.LogInformation($"Updated displayname on user with username {username} and db id {user.DatabaseId} to {fullname}");
                    changes = true;
                    nameChanges = true;
                }
                if (userPrincipal.Enabled.Equals(false))
                {
                    userPrincipal.Enabled = true;
                    logger.LogInformation($"Reactivated user with username {username} and db id {user.DatabaseId}");
                    changes = true;
                    reactivated = true;
                }

                using DirectoryEntry principalEntry = userPrincipal.GetUnderlyingObject() as DirectoryEntry;

                // institution numbers
                string institutionNumbers = String.Join(",", user.Institutions.Select(i => i.InstitutionNumber).ToArray());
                if (institutionNumberField != null && !institutionNumberField.Equals("") && !object.Equals(principalEntry.Properties[institutionNumberField].Value, institutionNumbers))
                {
                    principalEntry.Properties[institutionNumberField].Value = institutionNumbers;
                    logger.LogInformation($"Updated {institutionNumberField} (institutionNumberField) on user with username {username} and db id {user.DatabaseId} to {institutionNumbers}");
                    changes = true;
                }

                // optional fields
                if (cprField != null && !cprField.Equals("") && !object.Equals(principalEntry.Properties[cprField].Value, user.Cpr))
                {
                    principalEntry.Properties[cprField].Value = user.Cpr;
                    logger.LogInformation($"Updated {cprField} (cprField) on user with username {username} and db id {user.DatabaseId}");
                    changes = true;
                }

                string institutionNames = String.Join(", ", user.Institutions.Select(i => i.InstitutionName).ToArray());
                if (institutionNameField != null && !institutionNameField.Equals("") && !object.Equals(principalEntry.Properties[institutionNameField].Value, institutionNames))
                {
                    principalEntry.Properties[institutionNameField].Value = institutionNames;
                    logger.LogInformation($"Updated {institutionNameField} (institutionNameField) on user with username {username} and db id {user.DatabaseId} to {institutionNames}");
                    changes = true;
                }

                string institutionAbbreviations = String.Join(", ", user.Institutions.Where(i => i.Abbreviation != null).Select(i => i.Abbreviation).ToArray());
                if (institutionAbbreviationField != null && !institutionAbbreviationField.Equals("") && !object.Equals(principalEntry.Properties[institutionAbbreviationField].Value, institutionAbbreviations))
                {
                    principalEntry.Properties[institutionAbbreviationField].Value = institutionAbbreviations;
                    logger.LogInformation($"Updated {institutionAbbreviationField} (institutionAbbreviationField) on user with username {username} and db id {user.DatabaseId} to {institutionAbbreviations}");
                    changes = true;
                }

                if (uniIdField != null && !uniIdField.Equals("") && !object.Equals(principalEntry.Properties[uniIdField].Value, user.UniId))
                {
                    principalEntry.Properties[uniIdField].Value = user.UniId;
                    logger.LogInformation($"Updated {uniIdField} (uniIdField) on user with username {username} and db id {user.DatabaseId} to {user.UniId}");
                    changes = true;
                }

                string mail = username + emailDomain;
                if (mailField != null && !mailField.Equals("") && !object.Equals(principalEntry.Properties[mailField].Value, mail))
                {
                    principalEntry.Properties[mailField].Value = mail;
                    logger.LogInformation($"Updated {mailField} (mailField) on user with username {username} and db id {user.DatabaseId} to {mail}");
                    changes = true;
                }

                string totalRoles = String.Join(',', user.TotalRoles);
                if (stilRolesField != null && !stilRolesField.Equals("") && !object.Equals(principalEntry.Properties[stilRolesField].Value, totalRoles))
                {
                    principalEntry.Properties[stilRolesField].Value = totalRoles;
                    logger.LogInformation($"Updated {stilRolesField} (stilRolesField) on user with username {username} and db id {user.DatabaseId} to {totalRoles}");
                    changes = true;
                }

                if (!string.IsNullOrEmpty(disabledDateField) && !string.IsNullOrEmpty(principalEntry.Properties[disabledDateField].Value?.ToString()))
                {
                    principalEntry.Properties[disabledDateField].Value = null;
                    logger.LogInformation($"Updated {disabledDateField} (disabledDateField) on user with username {username} to be empty as user is active");
                    changes = true;
                }

                if (!string.IsNullOrEmpty(globalRoleField) && !object.Equals(principalEntry.Properties[globalRoleField].Value, user.GlobalRole.ToString()))
                {
                    principalEntry.Properties[globalRoleField].Value = user.GlobalRole.ToString();
                    logger.LogInformation($"Updated {globalRoleField} (globalRoleField) on user with username {username} and db id {user.DatabaseId} to {user.GlobalRole}");
                    changes = true;
                }

                // mark user
                if (!createOUHierarchy && adFieldForOS2skoledataMark != null && !adFieldForOS2skoledataMark.Equals("") && !object.Equals(principalEntry.Properties[adFieldForOS2skoledataMark].Value, os2skoledataMark))
                {
                    principalEntry.Properties[adFieldForOS2skoledataMark].Value = os2skoledataMark;
                    logger.LogInformation($"Updated {adFieldForOS2skoledataMark} (adFieldForOS2skoledataMark) on user with username {username} and db id {user.DatabaseId} to {os2skoledataMark}");
                    changes = true;
                }


                if (changes)
                {
                    if (dryRun)
                    {
                        logger.LogInformation($"DryRun: would have updated AD account with username {username}. Changes(all kinds of changes) true, nameChanges={nameChanges}, reactivated={reactivated}");
                    }
                    else
                    {
                        userPrincipal.Save();

                        if (nameChanges)
                        {
                            var toRenameTo = "CN=" + firstname + " " + familyname + " (" + username + ")";
                            string unidecodedName = toRenameTo.Unidecode();
                            if (unidecodedName.Length > 64)
                            {
                                int lengthDiff = unidecodedName.Length - 64;
                                string namePart = firstname + " " + familyname;
                                if (namePart.Length > lengthDiff)
                                {
                                    namePart = namePart.Substring(0, namePart.Length - lengthDiff);
                                    string fullName = namePart + " (" + username + ")";
                                    logger.LogInformation($"Renaming user with username {username} and db id {user.DatabaseId} to {fullName.Unidecode()}");
                                    principalEntry.Rename(fullName.Unidecode());
                                }
                                else
                                {
                                    throw new Exception($"Way to long name for user {username}. Name {unidecodedName}. Not updating user. Stopping sync.");
                                }
                            }
                            else
                            {
                                logger.LogInformation($"Renaming user with username {username} and db id {user.DatabaseId} to {unidecodedName}");
                                principalEntry.Rename(unidecodedName);
                            }
                            
                        }

                        principalEntry.CommitChanges();
                    }
                }
            }

            // make sure we have the right dn
            if (nameChanges || changes)
            {
                if (useUsernameAsKey)
                {
                    if (UsernameKeyType.UNI_ID.Equals(usernameKeyType))
                    {
                        entry = GetUserFromUsername(user.UniId);
                    } else
                    {
                        entry = GetUserFromUsername(username);
                    }
                    
                } else
                {
                    entry = GetUserFromCpr(user.Cpr);
                }
            }

            // check if user should be moved
            bool moved = false;
            string cn = entry.Properties["cn"][0].ToString();
            string dn = entry.Properties["distinguishedName"].Value.ToString();
            string ouDN = dn.Replace("CN=" + cn + ",", "");
            using DirectoryEntry currentOU = new DirectoryEntry(@"LDAP://" + ouDN);
            int classYearForOU = 0;
            string institutionNameForOU = null;
            if (createOUHierarchy)
            {
                if (keepAliveUsernames != null && keepAliveUsernames.Contains(username) && !string.IsNullOrEmpty(keepAliveOU) && keepAliveOU.Equals(ouDN))
                {
                    // do not move
                }
                else if (user.Role.Equals(Role.STUDENT))
                {
                    using DirectoryEntry institutionEntry = GetOUFromId("inst" + user.CurrentInstitutionNumber);
                    using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);

                    if (!dryRun || (dryRun && institutionEntry != null && emptyGroupsOU != null))
                    {
                        if (user.StudentMainGroups == null || user.StudentMainGroups.Count() == 0)
                        {
                            if (!currentOU.Properties["distinguishedName"].Value.ToString().Equals(emptyGroupsOU.Properties["distinguishedName"].Value.ToString()))
                            {
                                MoveUser(username, dn, emptyGroupsOU.Properties["distinguishedName"].Value.ToString());
                                moved = true;
                            }

                        }
                        else
                        {
                            List<string> possibleDns = new List<string>();
                            Dictionary<string, InfoDTO> possibleDnsAndInfo = new Dictionary<string, InfoDTO>();
                            foreach (var mainGroup in user.StudentMainGroupsAsObjects)
                            {
                                using DirectoryEntry possible = GetOUFromId(mainGroup.DatabaseId.ToString());
                                if (possible != null)
                                {
                                    string currentDn = possible.Properties["distinguishedName"].Value.ToString();
                                    possibleDns.Add(currentDn);
                                    possibleDnsAndInfo[currentDn] = new InfoDTO(mainGroup);

                                }
                            }

                            // if user is already in one of the possible ous - don't move, else move to first of list
                            if (possibleDns.Count() == 0 && !ouDN.Equals(emptyGroupsOU.Properties["distinguishedName"].Value.ToString()))
                            {
                                //There is no options, move to the ungrouped ou
                                MoveUser(username, dn, emptyGroupsOU.Properties["distinguishedName"].Value.ToString());
                                moved = true;
                            }
                            else if (possibleDns.Count() != 0)
                            {
                                string selectedDn = possibleDns[0];

                                if (user.PrimaryInstitution != null && possibleDnsAndInfo.Values.Any(g => g.Primary))
                                {
                                    // if user has a primary institution move to there 
                                    selectedDn = possibleDnsAndInfo
                                    .First(g => g.Value.Primary)
                                    .Key;
                                }
                                else if (possibleDnsAndInfo.Values.Any(dn => dn.InstitutionType.Equals(InstitutionType.FU))
                                    && possibleDnsAndInfo.Values.Any(dn => dn.InstitutionType.Equals(InstitutionType.SCHOOL)
                                ))
                                {
                                    //If there is both a school and a FU, choose the first school
                                    selectedDn = possibleDnsAndInfo
                                    .First(dn => dn.Value.InstitutionType.Equals(InstitutionType.SCHOOL))
                                    .Key;
                                }

                                if (!selectedDn.Equals(ouDN))
                                {
                                    MoveUser(username, dn, selectedDn);
                                    moved = true;
                                }

                                classYearForOU = possibleDnsAndInfo[selectedDn].StartYear;
                                institutionNameForOU = possibleDnsAndInfo[selectedDn].InstitutionName;
                                
                            }
                        }
                    } else
                    {
                        logger.LogInformation($"DryRun: would have checked if student user with username {username} should be moved, but some ous are missing (maybe because of DryRun)");
                    }
                }
                else if (user.Role.Equals(Role.EMPLOYEE) || user.Role.Equals(Role.EXTERNAL))
                {
                    Dictionary<string, Institution> possibleDnsAndInstitution = new();
                    foreach (var institution in user.Institutions)
                    {
                        if (IsWhitelisted(institution))
                        {
                            using var institutionOU = GetOUFromId("inst" + institution.InstitutionNumber);
                            if (institutionOU != null)
                            {
                                using var employeeOU = GetEmployeeOU(institutionOU);
                                if (employeeOU != null)
                                {
                                    string currentDn = employeeOU.Properties["distinguishedName"].Value.ToString();
                                    possibleDnsAndInstitution[currentDn] = institution;

                                }
                            }
                        }
                    }

                    // decide if and where user should be moved to
                    if (possibleDnsAndInstitution.Count > 0)
                    {
                        string selectedDn = possibleDnsAndInstitution.First().Key;

                        if (user.PrimaryInstitution != null && possibleDnsAndInstitution.Values.Any(i => i.DatabaseId == user.PrimaryInstitution.DatabaseId)) {
                            // if user has a primary institution move to there 
                            selectedDn = possibleDnsAndInstitution
                            .First(i => i.Value.DatabaseId == user.PrimaryInstitution.DatabaseId)
                            .Key;
                        }
                        else if (possibleDnsAndInstitution.Values.Any(dn => dn.Type.Equals(InstitutionType.FU))
                                 && possibleDnsAndInstitution.Values.Any(dn => dn.Type.Equals(InstitutionType.SCHOOL)
                             ))
                        {
                            // if there is both a school and a FU, choose the first school
                            selectedDn = possibleDnsAndInstitution
                            .First(dn => dn.Value.Type.Equals(InstitutionType.SCHOOL))
                            .Key;
                        }

                        if (!selectedDn.Equals(ouDN))
                        {
                            MoveUser(username, dn, selectedDn);
                            moved = true;
                        }

                        institutionNameForOU = possibleDnsAndInstitution[selectedDn].InstitutionName;
                    }

                    if (possibleDnsAndInstitution.Count == 0 && dryRun)
                    {
                        logger.LogInformation($"DryRun: would have checked if employee / external user with username {username} should be moved, but some ous are missing (maybe because of DryRun)");
                    }
                }
            } else
            {
                // if no hierarchy - don't move user unless reactivated
                if (reactivated)
                {
                    List<string> possibleDns = new List<string>();
                    foreach (var institution in user.Institutions)
                    {
                        if (IsWhitelisted(institution))
                        {
                            using var institutionOU = new DirectoryEntry(@"LDAP://" + institutionUserOUPath[institution.InstitutionNumber]);
                            if (institutionOU != null)
                            {
                                possibleDns.Add(institutionOU.Properties["distinguishedName"].Value.ToString());
                            }
                        }
                    }


                    // if user is already in one of the possible ous - don't move, else move to first of list
                    if (possibleDns.Count > 0 && !possibleDns.Contains(ouDN))
                    {
                        MoveUser(username, dn, possibleDns[0]);
                        moved = true;
                    }
                }
            }

            // make sure we have the right dn
            if (moved)
            {
                if (useUsernameAsKey)
                {
                    if (UsernameKeyType.UNI_ID.Equals(usernameKeyType))
                    {
                        entry = GetUserFromUsername(user.UniId);
                    }
                    else
                    {
                        entry = GetUserFromUsername(username);
                    }
                }
                else
                {
                    entry = GetUserFromCpr(user.Cpr);
                }
            }

            // update start year if changed
            if (studentStartYearField != null && !studentStartYearField.Equals(""))
            {
                if (classYearForOU != 0 && !object.Equals(entry.Properties[studentStartYearField].Value, classYearForOU.ToString()))
                {
                    if (dryRun)
                    {
                        logger.LogInformation($"DryRun: would have updated {studentStartYearField} (studentStartYearField) on user with username {username} and db id {user.DatabaseId} to {classYearForOU}");
                    }
                    else
                    {
                        entry.Properties[studentStartYearField].Value = classYearForOU.ToString();
                        logger.LogInformation($"Updated {studentStartYearField} (studentStartYearField) on user with username {username} and db id {user.DatabaseId} to {classYearForOU}");
                        entry.CommitChanges();
                    }
                    
                    changes = true;
                } else if (classYearForOU == 0 && !object.Equals(entry.Properties[studentStartYearField].Value, null))
                {
                    if (dryRun)
                    {
                        logger.LogInformation($"DryRun: would have updated {studentStartYearField} (studentStartYearField) on user with username {username} and db id {user.DatabaseId} to be empty (no start year found)");
                    }
                    else
                    {
                        entry.Properties[studentStartYearField].Value = null;
                        logger.LogInformation($"Updated {studentStartYearField} (studentStartYearField) on user with username {username} and db id {user.DatabaseId} to be empty (no start year found)");
                        entry.CommitChanges();
                    }

                    changes = true;
                }
            }

            // update current institution if changed
            if (currentInstitutionField != null && !currentInstitutionField.Equals(""))
            {
                if (!String.IsNullOrEmpty(institutionNameForOU) && !object.Equals(entry.Properties[currentInstitutionField].Value, institutionNameForOU))
                {
                    if (dryRun)
                    {
                        logger.LogInformation($"DryRun: would have updated {currentInstitutionField} (currentInstitutionField) on user with username {username} and db id {user.DatabaseId} to {institutionNameForOU}");
                    }
                    else
                    {
                        entry.Properties[currentInstitutionField].Value = institutionNameForOU;
                        logger.LogInformation($"Updated {currentInstitutionField} (currentInstitutionField) on user with username {username} and db id {user.DatabaseId} to {institutionNameForOU}");
                        entry.CommitChanges();
                    }
                    
                    changes = true;
                }
                else if (String.IsNullOrEmpty(institutionNameForOU) && !object.Equals(entry.Properties[currentInstitutionField].Value, null))
                {
                    if (dryRun)
                    {
                        logger.LogInformation($"DryRun: would have updated {currentInstitutionField} (currentInstitutionField) on user with username {username} and db id {user.DatabaseId} to be empty (no institutionName found)");
                    }
                    else
                    {
                        entry.Properties[currentInstitutionField].Value = null;
                        logger.LogInformation($"Updated {currentInstitutionField} (currentInstitutionField) on user with username {username} and db id {user.DatabaseId} to be empty (no institutionName found)");
                        entry.CommitChanges();
                    }
                    
                    changes = true;
                }
            }

            if (reactivated)
            {
                // run powerShell
                powerShellRunner.RunCreateScript(username, user.Firstname + " " + user.FamilyName, user.Role.ToString(), ctx.ConnectedServer, user);

                // register as reactivated in OS2skoledata
                os2SkoledataService.SetActionOnUser(username, ActionType.REACTIVATE);
            }

            if (changes || nameChanges || moved || reactivated)
            {
                logger.LogInformation($"Updated user with username {username}. AD path = {entry.Properties["distinguishedName"].Value}");
            }

            return entry.Properties["distinguishedName"].Value.ToString();
        }

        private bool IsWhitelisted(Institution institution)
        {
            bool whitelisted = true;
            if (institutionWhitelist != null && institutionWhitelist.Count != 0)
            {
                if (!institutionWhitelist.Contains(institution.InstitutionNumber))
                {
                    whitelisted = false;
                }
            }
            return whitelisted;
        }

        public void UpdateSecurityGroups(Institution institution, List<User> users, List<Group> classes, List<string> allSecurityGroupIds, List<string> allRenamedGroupIds, Dictionary<string,string> usernameADPathMap, HashSet<string> allClassLevels, List<Group> otherGroups)
        {
            logger.LogInformation($"Handling security groups for institution {institution.InstitutionName}");
            using var institutionEntry = GetOUFromId("inst" + institution.InstitutionNumber);
            using var securityGroupOU = GetSecurityGroupOUInEntry(institutionEntry);

            // keeping a list of ids for when we need to delete later
            List<string> securityGroupIds = new List<string>();
            List<string> renamedOrNewSecurityGroupIds = new List<string>();

            // create student groups if not StudentAndClassGroupsSchoolsOnly or if StudentAndClassGroupsSchoolsOnly and institutionType is school
            if (!StudentAndClassGroupsSchoolsOnly || (StudentAndClassGroupsSchoolsOnly && institution.Type.Equals(InstitutionType.SCHOOL)))
            {
                // if ADPath is null it means that the user has been excluded
                // sort classes based on level (highest first) to make sure potential renaming is smooth
                classes = SortGroupsBasedOnLevel(classes);
                foreach (Group currentClass in classes)
                {
                    List<User> usersInClass = users.Where(u => u.ADPath != null && (u.GroupIds.Contains(currentClass.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + currentClass.DatabaseId)))).ToList();
                    List<User> studentsInClass = usersInClass.Where(u => u.Role.Equals(Role.STUDENT)).ToList();
                    List<User> employeesInClass = usersInClass.Where(u => u.Role.Equals(Role.EMPLOYEE) || u.Role.Equals(Role.EXTERNAL)).ToList();

                    string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_klasse_" + currentClass.DatabaseId;
                    string idStudents = "os2skoledata_institution_" + institution.InstitutionNumber + "_elever_klasse_" + currentClass.DatabaseId;
                    string idEmployees = "os2skoledata_institution_" + institution.InstitutionNumber + "_medarbejdere_klasse_" + currentClass.DatabaseId;

                    using DirectoryEntry classGroupEntry = UpdateGroup(securityGroupOU, id, GetClassSecurityGroupName(currentClass, institution, classSecurityGroupNameStandard, classSecurityGroupNameStandardNoClassYear, "classSecurityGroupNameStandard", "classSecurityGroupNameStandardNoClassYear"), institution.InstitutionNumber, GenerateSamAccountName(currentClass, institution, GroupType.CLASS_ALL, null, classes), GetSecurityGroupMail(currentClass, institution, classes, GroupType.CLASS_ALL), renamedOrNewSecurityGroupIds);
                    using DirectoryEntry classStudentsGroupEntry = UpdateGroup(securityGroupOU, idStudents, GetClassSecurityGroupName(currentClass, institution, classStudentsSecurityGroupNameStandard, classStudentsSecurityGroupNameStandardNoClassYear, "classStudentsSecurityGroupNameStandard", "classStudentsSecurityGroupNameStandardNoClassYear"), institution.InstitutionNumber, GenerateSamAccountName(currentClass, institution, GroupType.CLASS_STUDENTS, null, classes), null, renamedOrNewSecurityGroupIds);
                    using DirectoryEntry classEmployeesGroupEntry = UpdateGroup(securityGroupOU, idEmployees, GetClassSecurityGroupName(currentClass, institution, classEmployeesSecurityGroupNameStandard, classEmployeesSecurityGroupNameStandardNoClassYear, "classEmployeesSecurityGroupNameStandard", "classEmployeesSecurityGroupNameStandardNoClassYear"), institution.InstitutionNumber, GenerateSamAccountName(currentClass, institution, GroupType.CLASS_EMPLOYEES, null, classes), null, renamedOrNewSecurityGroupIds);

                    if (classGroupEntry != null)
                    {
                        securityGroupIds.Add(id);
                        HandleGroupMembers(classGroupEntry, usersInClass, null, usernameADPathMap);
                    }
                    if (classStudentsGroupEntry != null)
                    {
                        securityGroupIds.Add(idStudents);
                        HandleGroupMembers(classStudentsGroupEntry, studentsInClass, null, usernameADPathMap);
                    }
                    if (classEmployeesGroupEntry != null)
                    {
                        securityGroupIds.Add(idEmployees);
                        HandleGroupMembers(classEmployeesGroupEntry, employeesInClass, null, usernameADPathMap);
                    }
                }

                // handle security groups for other types of groups
                foreach (Group otherGroup in otherGroups)
                {
                    List<User> usersInGroup = users.Where(u => u.ADPath != null && (u.GroupIds.Contains(otherGroup.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + otherGroup.DatabaseId)))).ToList();
                    
                    string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_gruppe_" + otherGroup.DatabaseId;
                    
                    using DirectoryEntry otherGroupEntry = UpdateGroup(securityGroupOU, id, GetOtherGroupSecurityGroupName(otherGroup, institution), institution.InstitutionNumber, GenerateSamAccountName(otherGroup, institution, GroupType.OTHER_GROUPS_ALL, null, otherGroups), GetSecurityGroupMail(otherGroup, institution, otherGroups, GroupType.OTHER_GROUPS_ALL), renamedOrNewSecurityGroupIds);
                    
                    if (otherGroupEntry != null)
                    {
                        securityGroupIds.Add(id);
                        HandleGroupMembers(otherGroupEntry, usersInGroup, null, usernameADPathMap);
                    }
                }

                using DirectoryEntry institutionStudentGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_elever", GetInstitutionGroupName("STUDENTS", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_STUDENTS, null, null));
                securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_elever");
                // all students in institution security group
                if (institutionStudentGroup != null)
                {
                    List<User> usersInStudent = users.Where(u => u.ADPath != null && u.Role.Equals(Role.STUDENT)).ToList();
                    HandleGroupMembers(institutionStudentGroup, usersInStudent, null, usernameADPathMap);
                }

                // securityGroups for class levels - the groups never change but the students do
                List<string> classLevels = GetClassLevels(classes);
                allClassLevels.UnionWith(classLevels);
                foreach (string level in classLevels)
                {
                    List<User> usersInLevel = users.Where(u => u.Role.Equals(Role.STUDENT) && u.ADPath != null && u.StudentMainGroupLevelForInstitution != null && u.StudentMainGroupLevelForInstitution.Equals(level)).ToList();
                    string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_level_" + level;
                    using DirectoryEntry levelGroupEntry = UpdateGroup(securityGroupOU, id, GetLevelSecurityGroupName(level, institution, securityGroupForLevelNameStandard), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_LEVELS, level, null));
                    if (levelGroupEntry != null)
                    {
                        securityGroupIds.Add(id);
                        HandleGroupMembers(levelGroupEntry, usersInLevel, null, usernameADPathMap);
                    }
                }

                // securityGroups for students in same year
                List<int> classStartYears = GetClassStartYears(classes);
                foreach (int year in classStartYears)
                {
                    List<User> usersInYear = users.Where(u => u.Role.Equals(Role.STUDENT) && u.ADPath != null && u.StudentMainGroupStartYearForInstitution != 0 && u.StudentMainGroupStartYearForInstitution == year).ToList();
                    string id = "os2skoledata_institution_" + institution.InstitutionNumber + "_år_" + year;
                    using DirectoryEntry yearGroupEntry = UpdateGroup(securityGroupOU, id, GetYearSecurityGroupName(year, institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_STUDENT_YEARS, year.ToString(), null));
                    if (yearGroupEntry != null)
                    {
                        securityGroupIds.Add(id);
                        HandleGroupMembers(yearGroupEntry, usersInYear, null, usernameADPathMap);
                    }
                }
            }

            using DirectoryEntry institutionEmployeeGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte", GetInstitutionGroupName("EMPLOYEES", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEES, null, null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEES));
            using DirectoryEntry institutionAllGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_alle", GetInstitutionGroupName("ALL", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_ALL, null, null));

            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte");
            securityGroupIds.Add("os2skoledata_institution_" + institution.InstitutionNumber + "_alle");

            // all users in institution security group
            if (institutionAllGroup != null)
            {
                List<User> usersInAll = users.Where(u => u.ADPath != null).ToList();
                HandleGroupMembers(institutionAllGroup, usersInAll, null, usernameADPathMap);
            }

            // all employees in institution security group
            if (institutionEmployeeGroup != null)
            {
                List<User> usersInEmployee = users.Where(u => u.ADPath != null && (u.Role.Equals(Role.EMPLOYEE) || u.Role.Equals(Role.EXTERNAL))).ToList();
                HandleGroupMembers(institutionEmployeeGroup, usersInEmployee, null, usernameADPathMap);
            }

            // securityGroups for adults based on role
            HandleInstitutionEmployeeTypeSecurityGroups(securityGroupIds, securityGroupOU, institution, users, usernameADPathMap);

            // delete other securityGroups
            if (createOUHierarchy)
            {
                DeleteSecurityGroups(securityGroupIds, securityGroupOU, null, renamedOrNewSecurityGroupIds);
            } else
            {
                allSecurityGroupIds.AddRange(securityGroupIds);
                allRenamedGroupIds.AddRange(renamedOrNewSecurityGroupIds);
            }

            logger.LogInformation($"Finished handling security groups for institution {institution.InstitutionName}");
        }

        private string GetOtherGroupSecurityGroupName(Group otherGroup, Institution institution)
        {
            string institutionName = institution.InstitutionName;
            string groupName = otherGroup.GroupName;
            if (!useDanishCharacters)
            {
                institutionName = institutionName.Unidecode();
                groupName = groupName.Unidecode();
            }

            string name;
            switch (otherGroup.GroupType)
            {
                case DBGroupType.ÅRGANG:
                    name = otherGroupsÅrgangSecurityGroupNameStandard;
                    break; 
                case DBGroupType.RETNING:
                    name = otherGroupsRetningSecurityGroupNameStandard;
                    break;
                case DBGroupType.HOLD:
                    name = otherGroupsHoldSecurityGroupNameStandard;
                    break;
                case DBGroupType.SFO:
                    name = otherGroupsSFOSecurityGroupNameStandard;
                    break;
                case DBGroupType.TEAM:
                    name = otherGroupsTeamSecurityGroupNameStandard;
                    break;
                case DBGroupType.ANDET:
                    name = otherGroupsAndetSecurityGroupNameStandard;
                    break;
                default:
                    return null;
            }

            if (String.IsNullOrEmpty(name))
            {
                return null;
            }

            name = name.Replace("{INSTITUTION_NAME}", institution.InstitutionName)
                    .Replace("{INSTITUTION_ABBREVIATION}", institution.Abbreviation)
                    .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                    .Replace("{CLASS_NAME}", otherGroup.GroupName)
                    .Replace("{CLASS_ID}", otherGroup.GroupId)
                    .Replace("{DATABASE_ID}", otherGroup.DatabaseId.ToString());

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private List<Group> SortGroupsBasedOnLevel(List<Group> classes)
        {
            return classes.OrderByDescending(c => ConvertToInt(c.GroupLevel)).ToList();
        }

        private string GetLevelSecurityGroupName(string level, Institution institution, string nameStandard)
        {
            if (nameStandard == null)
            {
                return null;
            }

            string name = nameStandard;
            if (institution != null)
            {
                (string name, string abbreviation) institutionNames = getInstitutionNamesTuple(institution);

                name = nameStandard
                            .Replace("{INSTITUTION_NAME}", institutionNames.name)
                            .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                            .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
            }

            name = name.Replace("{LEVEL}", level);

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetYearSecurityGroupName(int year, Institution institution)
        {
            (string name, string abbreviation) institutionNames = getInstitutionNamesTuple(institution);

            string name = securityGroupForYearNameStandard
                        .Replace("{INSTITUTION_NAME}", institutionNames.name)
                        .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{YEAR}", year + "");

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private List<int> GetClassStartYears(List<Group> classes)
        {
            List<int> years = new List<int>();
            foreach (Group group in classes)
            {
                if (group.StartYear != 0 && !years.Contains(group.StartYear))
                {
                    years.Add(group.StartYear);
                }
            }
            return years;
        }

        private List<string> GetClassLevels(List<Group> classes)
        {
            List<string> levels = new List<string>();
            foreach (Group group in classes)
            {
                if (group.GroupLevel != null && !levels.Contains(group.GroupLevel))
                {
                    levels.Add(group.GroupLevel);
                }
            }
            return levels;
        }

        private void HandleInstitutionEmployeeTypeSecurityGroups(List<string> securityGroupIds, DirectoryEntry securityGroupOU, Institution institution, List<User> users, Dictionary<string, string> usernameADPathMap)
        {
            // check if name is empty. If one name is empty every name will be empty. Do not create groups if name is empty
            if (String.IsNullOrEmpty(GetInstitutionEmployeeTypeGroupName("LÆRER", institution)))
            {
                return;
            }

            using DirectoryEntry institutionEmployeeLærerGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LÆRER", GetInstitutionEmployeeTypeGroupName("LÆRER", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LÆRER", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LÆRER"));
            using DirectoryEntry institutionEmployeePædagogGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PÆDAGOG", GetInstitutionEmployeeTypeGroupName("PÆDAGOG", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "PÆDAGOG", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "PÆDAGOG"));
            using DirectoryEntry institutionEmployeeVikarGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_VIKAR", GetInstitutionEmployeeTypeGroupName("VIKAR", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "VIKAR", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "VIKAR"));
            using DirectoryEntry institutionEmployeeLederGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDER", GetInstitutionEmployeeTypeGroupName("LEDER", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LEDER", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LEDER"));
            using DirectoryEntry institutionEmployeeLedelseGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_LEDELSE", GetInstitutionEmployeeTypeGroupName("LEDELSE", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LEDELSE", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "LEDELSE"));
            using DirectoryEntry institutionEmployeeTapGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_TAP", GetInstitutionEmployeeTypeGroupName("TAP", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "TAP", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "TAP"));
            using DirectoryEntry institutionEmployeeKonsulentGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_KONSULENT", GetInstitutionEmployeeTypeGroupName("KONSULENT", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "KONSULENT", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "KONSULENT"));
            using DirectoryEntry institutionEmployeeUnknownGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_UNKNOWN", GetInstitutionEmployeeTypeGroupName("UNKNOWN", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "UNKNOWN", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "UNKNOWN"));
            using DirectoryEntry institutionEmployeePraktikantGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_PRAKTIKANT", GetInstitutionEmployeeTypeGroupName("PRAKTIKANT", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "PRAKTIKANT", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "PRAKTIKANT"));
            using DirectoryEntry institutionEmployeeEksternGroup = UpdateGroup(securityGroupOU, "os2skoledata_institution_" + institution.InstitutionNumber + "_ansatte_EKSTERN", GetInstitutionEmployeeTypeGroupName("EKSTERN", institution), institution.InstitutionNumber, GenerateSamAccountName(null, institution, GroupType.INSTITUTION_EMPLOYEE_TYPES, "EKSTERN", null), GetSecurityGroupMail(null, institution, null, GroupType.INSTITUTION_EMPLOYEE_TYPES, "EKSTERN"));

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
            HandleGroupMembers(institutionEmployeeLærerGroup, usersInLærer, null, usernameADPathMap);

            List<User> usersInPædagog = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.PÆDAGOG)).ToList();
            HandleGroupMembers(institutionEmployeePædagogGroup, usersInPædagog, null, usernameADPathMap);

            List<User> usersInVikar = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.VIKAR)).ToList();
            HandleGroupMembers(institutionEmployeeVikarGroup, usersInVikar, null, usernameADPathMap);

            List<User> usersInLeder = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDER)).ToList();
            HandleGroupMembers(institutionEmployeeLederGroup, usersInLeder, null, usernameADPathMap);

            List<User> usersInLedelse = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDELSE)).ToList();
            HandleGroupMembers(institutionEmployeeLedelseGroup, usersInLedelse, null, usernameADPathMap);

            List<User> usersInTAP = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.TAP)).ToList();
            HandleGroupMembers(institutionEmployeeTapGroup, usersInTAP, null, usernameADPathMap);

            List<User> usersInKonsulent = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.KONSULENT)).ToList();
            HandleGroupMembers(institutionEmployeeKonsulentGroup, usersInKonsulent, null, usernameADPathMap);

            List<User> usersInPraktikant = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.PRAKTIKANT)).ToList();
            HandleGroupMembers(institutionEmployeePraktikantGroup, usersInPraktikant, null, usernameADPathMap);

            List<User> usersInEkstern = users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.EKSTERN)).ToList();
            HandleGroupMembers(institutionEmployeeEksternGroup, usersInEkstern, null, usernameADPathMap);

            List<User> usersInUnknown = users.Where(u => u.ADPath != null && ((u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.UNKNOWN)) || (u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.UNKNOWN)))).ToList();
            HandleGroupMembers(institutionEmployeeUnknownGroup, usersInUnknown, null, usernameADPathMap);
        }

        /// <summary>
        /// Returns the name and abbreviation for the institution, taking into account the setting for Danish characters
        /// </summary>
        /// <param name="institution"></param>
        /// <returns></returns>
        private (string name, string abbreviation) getInstitutionNamesTuple(Institution institution)
        {
            string institutionName = "";
            string institutionAbbreviation = "";
            if (institution != null)
            {
                if (useDanishCharacters)
                {
                    institutionName = institution.InstitutionName;
                    institutionAbbreviation = institution.Abbreviation;
                }
                else
                {
                    institutionName = institution.InstitutionName.Unidecode();
                    institutionAbbreviation = institution.Abbreviation.Unidecode();
                }
                return (institutionName, institutionAbbreviation);

            }
            return ("", "");
        }

        private string GetInstitutionEmployeeTypeGroupName(string type, Institution institution)
        {
            (string name, string abbreviation) institutionNames = getInstitutionNamesTuple(institution);

            string name = securityGroupForEmployeeTypeNameStandard
                        .Replace("{INSTITUTION_NAME}", institutionNames.name)
                        .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{TYPE}", type);

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetGlobalEmployeeTypeGroupName(string type, string nameStandard)
        {
            string name = nameStandard
                        .Replace("{TYPE}", type);

            if (!useDanishCharacters)
            {
                name = name.Unidecode();
            }

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        public void DeleteSecurityGroups(List<string> securityGroupIds, DirectoryEntry securityGroupOU, List<string> lockedInstitutionNumbers, List<string> allRenamedGroupIds)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have checked if any classes should be deleted");
            }
            else
            {
                if (securityGroupOU == null)
                {
                    securityGroupOU = GetSecurityGroupOUInEntry(null);
                }

                logger.LogInformation("Checking if any groups under " + securityGroupOU.Name + " should be deleted");
                PrincipalContext context = new PrincipalContext(ContextType.Domain, null, securityGroupOU.Properties["distinguishedName"].Value.ToString());
                GroupPrincipal findAllGroups = new GroupPrincipal(context, "*");
                PrincipalSearcher ps = new PrincipalSearcher(findAllGroups);
                ((DirectorySearcher)ps.GetUnderlyingSearcher()).PageSize = 1000;
                using PrincipalSearchResult<Principal> allGroups = ps.FindAll();
                foreach (var group in allGroups)
                {
                    logger.LogInformation("Checking if group " + group.Name + " should be deleted");
                    using DirectoryEntry entry = group.GetUnderlyingObject() as DirectoryEntry;
                    if (entry.Properties[securityGroupIdField].Value != null)
                    {
                        string id = entry.Properties[securityGroupIdField].Value.ToString();
                        string name = entry.Name;
                        if (id.StartsWith("os2skoledata_institution_") && !securityGroupIds.Contains(id))
                        {
                            // skip if in locked institution
                            var skip = false;
                            if (lockedInstitutionNumbers != null)
                            {
                                var instituionNumber = entry.Properties[securityGroupInstitutionNumberField].Value.ToString();
                                if (lockedInstitutionNumbers.Contains(instituionNumber))
                                {
                                    skip = true;
                                }
                            }

                            if (skip)
                            {
                                continue;
                            }

                            securityGroupOU.Children.Remove(entry);
                            logger.LogInformation($"Deleted class security group with name {name}");
                        }
                    }
                }

                // after deletion rename prefixed groups to their real name and samAccountName
                foreach (var groupId in allRenamedGroupIds)
                {
                    DirectoryEntry group = GetGroupFromId(groupId);
                    if (group != null)
                    {
                        string name = RemovePrefix(group.Name, "CN=");
                        name = RemovePrefix(name, "c_");
                        logger.LogInformation("Removing prefix on group with name " + name);
                        group.Rename("CN=" + name);

                        if (setSamAccountName)
                        {
                            string samAccountName = group.Properties["sAMAccountName"].Value.ToString();
                            logger.LogInformation("Removing prefix on group sAMAccountName " + samAccountName);
                            group.Properties["sAMAccountName"].Value = RemovePrefix(samAccountName, "c_");
                        }

                        group.CommitChanges();
                    }
                }
            }
        }

        private string RemovePrefix(string name, string prefix)
        {
            if (name.StartsWith(prefix))
            {
                return name.Substring(prefix.Length);
            }
            else
            {
                return name; // Return the original name if the prefix is not present
            }
        }

        private void HandleGroupMembers(DirectoryEntry group, List<User> users, List<string> lockedInstitutionNumbers, Dictionary<string, string> usernameADPathMap)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have checked if any members should be added or removed from group");
            }
            else
            {
                if (moveUsersEnabled)
                {
                    logger.LogInformation($"Handling members for group {group.Name}");
                    PrincipalContext context = new PrincipalContext(ContextType.Domain, Environment.UserDomainName);
                    GroupPrincipal groupPrincipal = GroupPrincipal.FindByIdentity(context, IdentityType.Guid, group.Guid.ToString());
                    List<Principal> members = new List<Principal>();
                    try
                    {
                        members = groupPrincipal.GetMembers().ToList();
                    } catch(Exception e)
                    {
                        logger.LogInformation($"Failed to find current members for group {group.Name}. Maybe there are none. Therefore not checking if members should be removed");
                    }

                    List<string> currentUsernames = members.Select(m => m.SamAccountName).ToList();
                    List<string> actualUsernames = users.Select(u => u.Username).ToList();

                    foreach (var member in members)
                    {
                        var usernameForMember = member.SamAccountName;
                        var dn = member.DistinguishedName;

                        if (usersToIgnore.Contains(usernameForMember) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(usernameForMember)))
                        {
                            logger.LogInformation("Ignoring " + usernameForMember);
                            continue;
                        }

                        if (lockedInstitutionNumbers != null)
                        {
                            // skip if member is in locked institution
                            using DirectoryEntry memberAsDirectoryEntry = member.GetUnderlyingObject() as DirectoryEntry;

                            // the member can be a group and not have the institutionNumberField 
                            string memberInstitutionNumbers = "";
                            List<string> memberInstitutionNumbersList = new List<string>();
                            try
                            {
                                memberInstitutionNumbers = memberAsDirectoryEntry.Properties[institutionNumberField].Value.ToString();
                                memberInstitutionNumbersList = memberInstitutionNumbers.Split(",").ToList();
                            } catch(Exception e)
                            {
                                // do nothing
                            }

                            var skip = false;
                            if (memberInstitutionNumbers != null)
                            {
                                foreach (string number in memberInstitutionNumbersList)
                                {
                                    if (lockedInstitutionNumbers.Contains(number))
                                    {
                                        skip = true;
                                        break;
                                    }
                                }
                            }

                            if (skip)
                            {
                                continue;
                            }
                        }

                        if (!actualUsernames.Contains(usernameForMember))
                        {
                            group.Properties["member"].Remove(dn);
                            logger.LogInformation($"Removed member with dn {dn} from group {group.Name}");
                        }
                    }

                    foreach (User user in users)
                    {
                        if (usersToIgnore.Contains(user.Username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(user.Username)))
                        {
                            logger.LogInformation("Ignoring " + user.Username);
                            continue;
                        }

                        if (!currentUsernames.Contains(user.Username))
                        {
                            string pathForUser = GetPathFromUsername(user, usernameADPathMap);
                            group.Properties["member"].Add(pathForUser);
                            logger.LogInformation($"Added member with dn {pathForUser} to group {group.Name}");

                            currentUsernames.Add(user.Username);
                        }
                    }
                    group.CommitChanges();
                }
            }
        }

        private string GetPathFromUsername(User user, Dictionary<string, string> usernameADPathMap)
        {
            if (usernameADPathMap.ContainsKey(user.Username)) {
                return usernameADPathMap[user.Username];
            } else
            {
                // should never happen
                return user.ADPath;
            }
        }

        public void UpdateGlobalSecurityGroups(Dictionary<string, List<User>> institutionUserMap, List<string> lockedInstitutionNumbers, Dictionary<string, string> usernameADPathMap, List<Institution> institutions, HashSet<string> allClassLevels)
        {
            logger.LogInformation("Handling global security groups");
            using var rootEntry = new DirectoryEntry("LDAP://" + rootOU);
            using var securityGroupOU = GetSecurityGroupOUInEntry(rootEntry);

            using DirectoryEntry globalStudentGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_elever", globalStudentSecurityGroupName, "allInstitutions", GenerateSamAccountName(null, null, GroupType.GLOBAL_STUDENTS, null, null));
            using DirectoryEntry globalEmployeeGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_ansatte", globalEmployeeSecurityGroupName, "allInstitutions", GenerateSamAccountName(null, null, GroupType.GLOBAL_EMPLOYEES, null, null));

            if (moveUsersEnabled)
            {
                List<User> students = new List<User>();
                List<User> employees = new List<User>();
                if (!dryRun)
                {
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
                            // so fetch the correct one from the map
                            user.ADPath = GetPathFromUsername(user, usernameADPathMap);
                            

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
                }

                if (globalStudentGroup != null)
                {
                    HandleGroupMembers(globalStudentGroup, students, lockedInstitutionNumbers, usernameADPathMap);
                }
                if (globalEmployeeGroup != null)
                {
                    HandleGroupMembers(globalEmployeeGroup, employees, lockedInstitutionNumbers, usernameADPathMap);
                }
            }

            if (!String.IsNullOrEmpty(globalSecurityGroupForEmployeeTypeSchoolNameStandard))
            {
                HandleGlobalEmployeeTypeSecurityGroups(securityGroupOU, institutions.Where(i => i.Type.Equals(InstitutionType.SCHOOL)).ToList(), institutionUserMap, usernameADPathMap, globalSecurityGroupForEmployeeTypeSchoolNameStandard, "skoler", GroupType.GLOBAL_EMPLOYEE_TYPES_SCHOOL, lockedInstitutionNumbers);
            }
            if (!String.IsNullOrEmpty(globalSecurityGroupForEmployeeTypeDaycareNameStandard))
            {
                HandleGlobalEmployeeTypeSecurityGroups(securityGroupOU, institutions.Where(i => i.Type.Equals(InstitutionType.DAYCARE)).ToList(), institutionUserMap, usernameADPathMap, globalSecurityGroupForEmployeeTypeDaycareNameStandard, "daginstitutioner", GroupType.GLOBAL_EMPLOYEE_TYPES_DAYCARE, lockedInstitutionNumbers);
            }
            if (!String.IsNullOrEmpty(globalSecurityGroupForEmployeeTypeFUNameStandard))
            {
                HandleGlobalEmployeeTypeSecurityGroups(securityGroupOU, institutions.Where(i => i.Type.Equals(InstitutionType.FU)).ToList(), institutionUserMap, usernameADPathMap, globalSecurityGroupForEmployeeTypeFUNameStandard, "FU", GroupType.GLOBAL_EMPLOYEE_TYPES_FU, lockedInstitutionNumbers);
            }

            if (!String.IsNullOrEmpty(globalSecurityGroupForLevelNameStandard))
            {
                List<User> allUsers = new List<User>();
                if (moveUsersEnabled)
                {
                    foreach (List<User> userList in institutionUserMap.Values)
                    {
                        allUsers.AddRange(userList);
                    }
                }
                foreach (string level in allClassLevels)
                {
                    List<User> usersInLevel = allUsers.Where(u => u.Role.Equals(Role.STUDENT) && u.ADPath != null && u.StudentMainGroupLevelForInstitution != null && u.StudentMainGroupLevelForInstitution.Equals(level)).ToList();
                    string id = "os2skoledata_global_level_" + level;
                    using DirectoryEntry levelGroupEntry = UpdateGroup(securityGroupOU, id, GetLevelSecurityGroupName(level, null, globalSecurityGroupForLevelNameStandard), "allInstitutions", GenerateSamAccountName(null, null, GroupType.GLOBAL_LEVEL, level, null));
                    if (levelGroupEntry != null)
                    {
                        HandleGroupMembers(levelGroupEntry, usersInLevel, null, usernameADPathMap);
                    }
                }
            }

            logger.LogInformation("Finished handling global security groups");
        }

        private void HandleGlobalEmployeeTypeSecurityGroups(DirectoryEntry securityGroupOU, List<Institution> allInstitutions, Dictionary<string, List<User>> institutionUserMap, Dictionary<string, string> usernameADPathMap, string nameStandard, String typeStringForId, GroupType groupType, List<string> lockedInstitutionNumbers)
        {
            using DirectoryEntry globalEmployeeLærerGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_LÆRER", GetGlobalEmployeeTypeGroupName("lærere", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "lærere", null));
            using DirectoryEntry globalEmployeePædagogGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_PÆDAGOG", GetGlobalEmployeeTypeGroupName("pædagoger", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "pædagoger", null));
            using DirectoryEntry globalEmployeeVikarGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_VIKAR", GetGlobalEmployeeTypeGroupName("vikarer", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "vikarer", null));
            using DirectoryEntry globalEmployeeLederGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_LEDER", GetGlobalEmployeeTypeGroupName("ledere", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "ledere", null));
            using DirectoryEntry globalEmployeeLedelseGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_LEDELSE", GetGlobalEmployeeTypeGroupName("ledelse", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "ledelse", null));
            using DirectoryEntry globalEmployeeTapGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_TAP", GetGlobalEmployeeTypeGroupName("TAPer", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "TAPer", null));
            using DirectoryEntry globalEmployeeKonsulentGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_KONSULENT", GetGlobalEmployeeTypeGroupName("konsulenter", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "konsulenter", null));
            using DirectoryEntry globalEmployeeUnknownGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_UNKNOWN", GetGlobalEmployeeTypeGroupName("unknown", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "unknown", null));
            using DirectoryEntry globalEmployeePraktikantGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_PRAKTIKANT", GetGlobalEmployeeTypeGroupName("praktikanter", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "praktikanter", null));
            using DirectoryEntry globalEmployeeEksternGroup = UpdateGroup(securityGroupOU, "os2skoledata_global_" + typeStringForId + "_EKSTERN", GetGlobalEmployeeTypeGroupName("eksterne", nameStandard), "allInstitutions", GenerateSamAccountName(null, null, groupType, "eksterne", null));

            if (moveUsersEnabled)
            {
                List<User> usersInLærer = new List<User>();
                List<User> usersInPædagog = new List<User>();
                List<User> usersInVikar = new List<User>();
                List<User> usersInLeder = new List<User>();
                List<User> usersInLedelse = new List<User>();
                List<User> usersInTAP = new List<User>();
                List<User> usersInKonsulent = new List<User>();
                List<User> usersInPraktikant = new List<User>();
                List<User> usersInEkstern = new List<User>();
                List<User> usersInUnknown = new List<User>();

                foreach (Institution institution in allInstitutions)
                {
                    if (institutionUserMap.ContainsKey(institution.InstitutionNumber))
                    {
                        List<User> users = institutionUserMap[institution.InstitutionNumber];
                        usersInLærer.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LÆRER)).ToList());
                        usersInPædagog.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.PÆDAGOG)).ToList());
                        usersInVikar.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.VIKAR)).ToList());
                        usersInLeder.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDER)).ToList());
                        usersInLedelse.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDELSE)).ToList());
                        usersInTAP.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.TAP)).ToList());
                        usersInKonsulent.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.KONSULENT)).ToList());
                        usersInPraktikant.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.PRAKTIKANT)).ToList());
                        usersInEkstern.AddRange(users.Where(u => u.ADPath != null && u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.EKSTERN)).ToList());
                        usersInUnknown.AddRange(users.Where(u => u.ADPath != null && ((u.Role.Equals(Role.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.UNKNOWN)) || (u.Role.Equals(Role.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.UNKNOWN)))).ToList());

                    }
                }

                HandleGroupMembers(globalEmployeeLærerGroup, usersInLærer, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeePædagogGroup, usersInPædagog, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeVikarGroup, usersInVikar, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeLederGroup, usersInLeder, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeLedelseGroup, usersInLedelse, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeTapGroup, usersInTAP, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeKonsulentGroup, usersInKonsulent, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeePraktikantGroup, usersInPraktikant, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeEksternGroup, usersInEkstern, lockedInstitutionNumbers, usernameADPathMap);
                HandleGroupMembers(globalEmployeeUnknownGroup, usersInUnknown, lockedInstitutionNumbers, usernameADPathMap);
            }
        }

        public void PopulateTable()
        {
           
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN))
            {
                char[] possibleNumbers = "0123456789".ToCharArray();
                int idx = 0;

                for (int i = 0; i < possibleNumbers.Length; i++)
                {
                    for (int j = 0; j < possibleNumbers.Length; j++)
                    {
                        for (int k = 0; k < possibleNumbers.Length; k++)
                        {
                            for (int l = 0; l < possibleNumbers.Length; l++)
                            {
                                uniqueIds.Add(idx++, ("" + possibleNumbers[i] + possibleNumbers[j] + possibleNumbers[k] + possibleNumbers[l]));
                            }
                        }
                    }
                }
            }
            else if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM))
            {
                // do nothing. Random number will be generated later 
            }
            else if (usernameStandard.Equals(UsernameStandardType.THREE_NUMBERS_THREE_CHARS_FROM_NAME))
            {
                char[] possibleNumbers = "23456789".ToCharArray();
                int idx = 0;

                for (int i = 0; i < possibleNumbers.Length; i++)
                {
                    for (int j = 0; j < possibleNumbers.Length; j++)
                    {
                        for (int k = 0; k < possibleNumbers.Length; k++)
                        {
                            uniqueIds.Add(idx++, ("" + possibleNumbers[i] + possibleNumbers[j] + possibleNumbers[k]));
                        }
                    }
                }
            }
            else
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

        public string GetRandomNumber(int length)
        {
            string s = string.Empty;
            for (int i = 0; i < length; i++)
                s = String.Concat(s, random.Next(10).ToString());
            return s;
        }

        public List<string> GetAllUsernames()
        {
            using var entry = new DirectoryEntry();
            return GetAllUsernames(entry, false);
        }

        public Dictionary<string, List<string>> GenerateUsernameMap(List<string> allADUsernames, List<string> allOS2skoledataUsernames)
        {
            Dictionary<string, List<string>> map = new Dictionary<string, List<string>>();
            List<string> allUsernames = new List<string>();
            allUsernames.AddRange(allADUsernames);
            allUsernames.AddRange(allOS2skoledataUsernames);
            foreach (string username in allUsernames)
            {
                string key = "";
                if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM))
                {
                    try
                    {
                        int wantedTotalLength = (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM)) ? randomStandardLetterCount + randomStandardNumberCount : 8;
                        if (username.Length != wantedTotalLength)
                        {
                            continue;
                        }

                        int wantedLetterLength = (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM)) ? randomStandardLetterCount : 4;
                        if (username.Length >= wantedLetterLength)
                        {
                            key = username.Substring(0, wantedLetterLength);
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
                } else if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_LAST) || usernameStandard.Equals(UsernameStandardType.THREE_NUMBERS_THREE_CHARS_FROM_NAME))
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
            bool handleFU = institutions.Any(i => i.Type.Equals(InstitutionType.FU));
            List<OUDTO> dtos = institutions.Select(i => new OUDTO(i)).ToList();
            using DirectoryEntry schoolsOU = GetSchoolsOU(rootOUEntry);
            using DirectoryEntry daycareOU = GetDaycareOU(rootOUEntry);
            DirectoryEntry fuOU = null;

            if (handleFU)
            {
                fuOU = GetFUOU(rootOUEntry);
            }
            
            syncOUs(dtos, rootOUEntry, true, null, schoolsOU, daycareOU, fuOU);
        }

        public void UpdateClassesForInstitution(List<Group> classes, Institution institution)
        {
            logger.LogInformation($"Handling classes for institution {institution.InstitutionName}");
            using DirectoryEntry rootOUEntry = new DirectoryEntry(@"LDAP://" + rootOU);

            if (dryRun)
            {
                List<OUDTO> dtos = classes.Select(c => new OUDTO(c)).ToList();
                syncOUs(dtos, null, false, institution, null, null, null);
            } else
            {
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

                // sort dtos based on level (highest first) to make sure potential renaming is smooth
                List<OUDTO> dtos = classes.Select(c => new OUDTO(c)).ToList();
                dtos = SortDtosBasedOnLevel(dtos);
                syncOUs(dtos, studentEntry, false, institution, null, null, null);
            }
        }

        private List<OUDTO> SortDtosBasedOnLevel(List<OUDTO> dtos)
        {
            return dtos.OrderByDescending(dto => ConvertToInt(dto.Level)).ToList();
        }

        private int ConvertToInt(string level)
        {
            int result;
            return int.TryParse(level, out result) ? result : int.MinValue;
        }

        public DirectoryEntry GetUserFromCpr(string cpr)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person)({0}={1}))", cprField, cpr);

            if (!String.IsNullOrEmpty(multipleCprExcludedGroupDn))
            {
                filter = string.Format("(&(objectClass=user)(objectClass=person)({0}={1})(!(memberOf={2})))", cprField, cpr, multipleCprExcludedGroupDn);
            };

            return SearchForDirectoryEntry(filter);
        }

        public DirectoryEntry GetUserFromUsername(string username)
        {
            if (username != null)
            {
                var filter = string.Format($"(&(objectClass=user)(objectClass=person)({usernameKeyField}={username}))");
                return SearchForDirectoryEntry(filter);
            } else
            {
                return null;
            }
        }

        public DirectoryEntry GetUserFromSAMAccountName(string username)
        {
            if (username != null)
            {
                var filter = string.Format($"(&(objectClass=user)(objectClass=person)(sAMAccountName={username}))");
                return SearchForDirectoryEntry(filter);
            }
            else
            {
                return null;
            }
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
            if (usersToIgnore.Contains(username) || usersToInclude.Count() != 0)
            {
                logger.LogInformation("Ignoring " + username);
                return null;
            }

            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have created Active directory user with username {username} and name {user.Firstname} {user.FamilyName}");
                return "";
            } else
            {
                // ou
                int classYearForOU = 0;
                string ouDN = null;
                if (createOUHierarchy)
                {
                    if (user.Role.Equals(Role.STUDENT))
                    {
                        if (user.StudentMainGroups == null || user.StudentMainGroups.Count() == 0)
                        {
                            using DirectoryEntry institutionEntry = GetOUFromId("inst" + institutionNumber);
                            using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);
                            ouDN = emptyGroupsOU.Properties["distinguishedName"].Value.ToString();
                        }
                        else
                        {
                            MiniGroup firstGroup = user.StudentMainGroupsAsObjects.First();
                            using DirectoryEntry entry = GetOUFromId(firstGroup.DatabaseId.ToString());
                            if (entry != null)
                            {
                                ouDN = entry.Properties["distinguishedName"].Value.ToString();
                                classYearForOU = firstGroup.StartYear;
                            }
                            else
                            {
                                using DirectoryEntry institutionEntry = GetOUFromId("inst" + institutionNumber);
                                using DirectoryEntry emptyGroupsOU = GetStudentWithoutGroupsOU(institutionEntry);
                                ouDN = emptyGroupsOU.Properties["distinguishedName"].Value.ToString();
                            }
                        }
                    }
                    else if (user.Role.Equals(Role.EMPLOYEE) || user.Role.Equals(Role.EXTERNAL))
                    {
                        using DirectoryEntry institutionEntry = GetOUFromId("inst" + institutionNumber);
                        using DirectoryEntry employeeOU = GetEmployeeOU(institutionEntry);
                        if (employeeOU != null)
                        {
                            ouDN = employeeOU.Properties["distinguishedName"].Value.ToString();
                        }
                    }
                }
                else
                {
                    using DirectoryEntry institutionUsersEntry = new DirectoryEntry(@"LDAP://" + institutionUserOUPath[institutionNumber]);
                    ouDN = institutionUsersEntry.Properties["distinguishedName"].Value.ToString();
                }

                if (ouDN == null)
                {
                    throw new Exception("Failed to find ou to create user in.");
                }


                // the user should be under root ou if createOUHierarchy
                if (createOUHierarchy && !ouDN.ToLower().EndsWith(rootOU.ToLower()))
                {
                    throw new Exception("The user " + username + " was not created. It has to be placed under the root ou. Tried to place user in " + ouDN);
                }

                using PrincipalContext ctx = GetPrincipalContext(ouDN);
                using UserPrincipal newUser = new UserPrincipal(ctx);
                var firstname = user.Firstname.Replace(",", "");
                var familyname = user.FamilyName.Replace(",", "");
                string name = firstname + " " + familyname + " (" + username + ")";
                string unidecodedName = name.Unidecode();
                if (unidecodedName.Length > 64)
                {
                    int lengthDiff = unidecodedName.Length - 64;
                    string namePart = firstname + " " + familyname;
                    if (namePart.Length > lengthDiff)
                    {
                        namePart = namePart.Substring(0, namePart.Length - lengthDiff);
                        string fullName = namePart + " (" + username + ")";
                        newUser.Name = fullName.Unidecode();
                    } else
                    {
                        throw new Exception($"Way to long name for user {username}. Name {unidecodedName}. Not creating user. Stopping sync.");
                    }

                } else
                {
                    newUser.Name = unidecodedName;
                }
                
                newUser.GivenName = firstname;
                newUser.Surname = familyname;
                newUser.SamAccountName = username;
                newUser.DisplayName = firstname + " " + familyname;
                newUser.AccountExpirationDate = null;
                newUser.UserPrincipalName = username + emailDomain;
                newUser.Enabled = true;
                newUser.PasswordNotRequired = false;

                if (user.SetPasswordOnCreate)
                {
                    newUser.SetPassword(user.Password);
                } else
                {
                    // the Aa!1 is to ensure we have a character from all four complexity categories (upper case, lower case, digit, special char)
                    // so that user creation doesn't fail due to complexity requirements not met
                    // password should still be safe enough due to the leading random uuid
                    newUser.SetPassword(Guid.NewGuid().ToString() + "Aa1$");
                    newUser.ExpirePasswordNow();
                }
                
                newUser.Save();

                // set cpr
                using DirectoryEntry directoryEntry = newUser.GetUnderlyingObject() as DirectoryEntry;

                if (cprField != null && !cprField.Equals(""))
                {
                    directoryEntry.Properties[cprField].Value = user.Cpr;
                }

                // set institutionNumbers
                if (institutionNumberField != null && !institutionNumberField.Equals(""))
                {
                    directoryEntry.Properties[institutionNumberField].Value = String.Join(",", user.Institutions.Select(i => i.InstitutionNumber).ToArray());
                }

                // optional fields
                if (institutionNameField != null && !institutionNameField.Equals(""))
                {
                    directoryEntry.Properties[institutionNameField].Value = String.Join(", ", user.Institutions.Select(i => i.InstitutionName).ToArray());
                }

                if (institutionAbbreviationField != null && !institutionAbbreviationField.Equals(""))
                {
                    directoryEntry.Properties[institutionAbbreviationField].Value = String.Join(", ", user.Institutions.Where(i => i.Abbreviation != null).Select(i => i.Abbreviation).ToArray());
                }

                if (uniIdField != null && !uniIdField.Equals(""))
                {
                    directoryEntry.Properties[uniIdField].Value = user.UniId;
                }

                string mail = username + emailDomain;
                if (mailField != null && !mailField.Equals(""))
                {
                    directoryEntry.Properties[mailField].Value = mail;
                }

                string totalRoles = String.Join(',', user.TotalRoles);
                if (stilRolesField != null && !stilRolesField.Equals(""))
                {
                    directoryEntry.Properties[stilRolesField].Value = totalRoles;
                }

                if (studentStartYearField != null && !studentStartYearField.Equals("") && classYearForOU != 0)
                {
                    directoryEntry.Properties[studentStartYearField].Value = classYearForOU;
                }

                if (!string.IsNullOrEmpty(globalRoleField))
                {
                    directoryEntry.Properties[globalRoleField].Value = user.GlobalRole.ToString();
                }

                // mark user
                if (!createOUHierarchy && adFieldForOS2skoledataMark != null && !adFieldForOS2skoledataMark.Equals(""))
                {
                    directoryEntry.Properties[adFieldForOS2skoledataMark].Value = os2skoledataMark;
                }

                directoryEntry.CommitChanges();

                // run powerShell
                powerShellRunner.RunCreateScript(username, user.Firstname + " " + user.FamilyName, user.Role.ToString(), ctx.ConnectedServer, user);
                
                // register as created in OS2skoledata
                os2SkoledataService.SetActionOnUser(username, ActionType.CREATE);

                return directoryEntry.Properties["distinguishedName"].Value.ToString();
            }
        }

        public void DeltaSyncCreateGroup(Group group)
        {
            using DirectoryEntry institutionEntry = GetOUFromId("inst" + group.InstitutionNumber);
            using DirectoryEntry studentEntry = GetStudentsOU(institutionEntry);

            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.InstitutionAbbreviation, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear, group.Line);
            CreateOU(studentEntry, false, group.GroupId, name);
        }

        public void DeltaSyncCreateInstitution(Institution institution)
        {
            using DirectoryEntry rootEntry = new DirectoryEntry(@"LDAP://" + rootOU);

            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, institution.Abbreviation, null, null, null, 0, null);
            CreateOU(rootEntry, true, institution.InstitutionNumber, name);
        }

        public void DeltaSyncUpdateGroup(Group group, DirectoryEntry entry)
        {
            using DirectoryEntry institutionEntry = GetOUFromId("inst" + group.InstitutionNumber);
            using DirectoryEntry studentEntry = GetStudentsOU(institutionEntry);
            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.InstitutionAbbreviation, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear, group.Line);

            // if it has been deleted, move it back to the right place
            bool moved = false;
            if (!rootDeletedOusOu.Equals(rootOU) && entry.Properties["distinguishedName"][0].ToString().EndsWith(rootDeletedOusOu))
            {
                logger.LogInformation("Attempting to move OU with DN " + entry.Properties["distinguishedName"][0].ToString() + " to " + studentEntry.Properties["distinguishedName"][0].ToString());
                entry.MoveTo(studentEntry);
                logger.LogInformation($"Moved OU with name {entry.Name} to OU with path {studentEntry.Properties["distinguishedName"][0]}");
                moved = true;
            }

            // check if ou should be updated
            if (moved)
            {
                using DirectoryEntry movedEntry = GetOUFromId("" + group.DatabaseId);
                UpdateOU(movedEntry, name);
            }
            else
            {
                UpdateOU(entry, name);
            }
        }

        public void DeltaSyncUpdateInstitution(Institution institution, DirectoryEntry entry)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, institution.Abbreviation, null, null, null, 0, null);
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
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have created or fetched school ou in Active Directory");
                return null;
            } else
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
        }

        private DirectoryEntry GetDaycareOU(DirectoryEntry rootOU)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have created or fetched daycare ou in Active Directory");
                return null;
            }
            else
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
        }

        private DirectoryEntry GetFUOU(DirectoryEntry rootOU)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have created or fetched FU ou in Active Directory");
                return null;
            }
            else
            {
                foreach (DirectoryEntry ou in rootOU.Children)
                {
                    if (ou.Name.Equals("OU=" + fuOUName))
                    {
                        return ou;
                    }
                    ou.Close();
                }

                DirectoryEntry fuOU = rootOU.Children.Add("OU=" + fuOUName, "OrganizationalUnit");
                fuOU.CommitChanges();

                return fuOU;
            }
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

        private void syncOUs(List<OUDTO> dtos, DirectoryEntry ouToCreateIn, bool institutionLevel, Institution institution, DirectoryEntry schoolsOU, DirectoryEntry daycareOU, DirectoryEntry fuOU)
        {
            // delete
            if (dryRun)
            { 
                string institutionString = institution == null ? "" : "Institution: " + institution.InstitutionName;
                logger.LogInformation($"DryRun: Would have checked if any ous should be deleted. InstitutionLevel: {institutionLevel}. {institutionString}");
            } else
            {
                foreach (DirectoryEntry ou in ouToCreateIn.Children)
                {

                    if (ou.Name.Equals("OU=" + schoolOUName)
                        || ou.Name.Equals("OU=" + daycareOUName)
                        || ou.Name.Equals("OU=" + fuOUName)
                        || ou.Properties["distinguishedName"].Equals(rootDeletedOusOu)
                        || ou.Properties["distinguishedName"].Equals(disabledUsersOU))
                    {
                        continue;
                    }

                    if (ou.Properties[oUIdField].Value != null)
                    {
                        string id = ou.Properties[oUIdField].Value.ToString();
                        if (dtos.Where(i => i.Id.Equals(id)).ToList().Count == 0)
                        {
                            if (institution == null)
                            {
                                MoveToDeletedOUs(ou, "Ukendt");
                            }
                            else
                            {
                                MoveToDeletedOUs(ou, GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, institution.Abbreviation, null, null, null, 0, null));
                            }
                        }
                    }
                    ou.Close();
                }
            }
            

            if (institutionLevel)
            {
                if (dryRun)
                {
                    logger.LogInformation($"DryRun: Would have checked if any ous in the schoolOU or daycareOU or FuOU should be deleted.");
                } else
                {
                    DeleteInstitutionLevelOusFor(dtos, schoolsOU);
                    DeleteInstitutionLevelOusFor(dtos, daycareOU);
                    if (fuOU != null)
                    {
                        DeleteInstitutionLevelOusFor(dtos, fuOU);
                    }
                }
            }

            List<string> idsWithPrefix = new List<string>();

            // create or update
            foreach (OUDTO dto in dtos)
            {
                if (dto.InstitutionLocked)
                {
                    logger.LogInformation($"NOT handling OU with database Id {dto.Id} and name  {dto.Name}. Institution is locked.");
                    continue;
                }

                logger.LogInformation($"Handling OU with database Id {dto.Id} and name  {dto.Name}");
                using DirectoryEntry match = GetOUFromId(dto.Id);
                string name = "";
                if (institutionLevel)
                {
                    name = GetNameForOU(institutionLevel, dto.Name, dto.StilId, dto.Abbreviation, null, null, null, 0, null);
                }
                else
                {
                    name = GetNameForOU(institutionLevel, institution.InstitutionName, institution.InstitutionNumber, dto.Abbreviation, dto.Name, dto.StilId, dto.Level, dto.StartYear, dto.Line);
                }

                if (match == null)
                {
                    // Add prefix for new classes to avoid naming issues during creation
                    string nameToCreate = name;
                    if (!institutionLevel && nameToCreate.Length <= 62)
                    {
                        nameToCreate = "c_" + nameToCreate;
                        idsWithPrefix.Add(dto.Id);
                    }

                    if (institutionLevel)
                    {
                        if (dto.Type.Equals(InstitutionType.SCHOOL))
                        {
                            CreateOU(schoolsOU, institutionLevel, dto.Id, nameToCreate);
                        }
                        else if (dto.Type.Equals(InstitutionType.DAYCARE))
                        {
                            CreateOU(daycareOU, institutionLevel, dto.Id, nameToCreate);
                        }
                        else if (dto.Type.Equals(InstitutionType.FU))
                        {
                            CreateOU(fuOU, institutionLevel, dto.Id, nameToCreate);
                        }
                        else if (dto.Type.Equals(InstitutionType.MUNICIPALITY))
                        {
                            CreateOU(ouToCreateIn, institutionLevel, dto.Id, nameToCreate);
                        }
                        else
                        {
                            throw new Exception($"Unknown institution type: {institution.Type.ToString()}. Institution with database id: {institution.DatabaseId} and institution number: {institution.InstitutionNumber}");
                        }
                    }
                    else
                    {
                        CreateOU(ouToCreateIn, institutionLevel, dto.Id, nameToCreate);
                    }
                }
                else
                {
                    // Check if current name is correct
                    string currentName = RemovePrefix(match.Name, "OU=");
                    bool nameNeedsUpdate = !currentName.Equals(name, StringComparison.OrdinalIgnoreCase);

                    // if it has been deleted, move it back to the right place
                    bool moved = false;

                    // null if dryRun
                    if (ouToCreateIn != null)
                    {
                        if (institutionLevel)
                        {
                            string matchDN = match.Properties["distinguishedName"].Value.ToString();
                            DirectoryEntry toMoveTo = null;

                            if (dto.Type.Equals(InstitutionType.SCHOOL))
                            {
                                string createInDN = schoolsOU.Properties["distinguishedName"].Value.ToString();
                                if (!matchDN.EndsWith(createInDN))
                                {
                                    toMoveTo = schoolsOU;
                                }
                            }
                            else if (dto.Type.Equals(InstitutionType.DAYCARE))
                            {
                                string createInDN = daycareOU.Properties["distinguishedName"].Value.ToString();
                                if (!matchDN.EndsWith(createInDN))
                                {
                                    toMoveTo = daycareOU;
                                }
                            }
                            else if (dto.Type.Equals(InstitutionType.FU))
                            {
                                string createInDN = fuOU.Properties["distinguishedName"].Value.ToString();
                                if (!matchDN.EndsWith(createInDN))
                                {
                                    toMoveTo = fuOU;
                                }
                            }
                            else if (dto.Type.Equals(InstitutionType.MUNICIPALITY))
                            {
                                string createInDN = ouToCreateIn.Properties["distinguishedName"].Value.ToString();
                                if (!matchDN.EndsWith(createInDN))
                                {
                                    toMoveTo = ouToCreateIn;
                                }
                            }
                            else
                            {
                                throw new Exception($"Unknown institution type: {institution.Type.ToString()}. Institution with database id: {institution.DatabaseId} and institution number: {institution.InstitutionNumber}");
                            }

                            if (toMoveTo != null)
                            {
                                string createInDN = toMoveTo.Properties["distinguishedName"].Value.ToString();
                                logger.LogInformation("Attempting to move OU with DN " + matchDN + " to " + createInDN);
                                match.MoveTo(toMoveTo);
                                logger.LogInformation($"Moved OU with name {match.Name} to OU with path {createInDN}");
                                moved = true;
                            }
                        }
                        else
                        {
                            string matchDN = match.Properties["distinguishedName"].Value.ToString();
                            string createInDN = ouToCreateIn.Properties["distinguishedName"].Value.ToString();
                            if (!matchDN.EndsWith(createInDN))
                            {
                                logger.LogInformation("Attempting to move OU with DN " + matchDN + " to " + createInDN);
                                match.MoveTo(ouToCreateIn);
                                logger.LogInformation($"Moved OU with name {match.Name} to OU with path {createInDN}");
                                moved = true;
                            }
                        }
                    }

                    // Update only if name needs changing
                    if (nameNeedsUpdate)
                    {
                        // Add prefix when renaming classes to avoid naming conflicts
                        string nameToUpdate = name;
                        if (!institutionLevel && nameToUpdate.Length <= 62)
                        {
                            nameToUpdate = "c_" + nameToUpdate;
                            idsWithPrefix.Add(dto.Id);
                        }

                        if (moved)
                        {
                            using DirectoryEntry movedEntry = GetOUFromId(dto.Id);
                            UpdateOU(movedEntry, nameToUpdate);
                        }
                        else
                        {
                            UpdateOU(match, nameToUpdate);
                        }
                    }
                    else
                    {
                        logger.LogInformation($"OU with id {dto.Id} already has correct name: {name}");
                    }
                }
            }

            // remove prefixes if any
            if (idsWithPrefix.Count > 0)
            {
                foreach (var id in idsWithPrefix)
                {
                    using DirectoryEntry entry = GetOUFromId(id);
                    string name = RemovePrefix(entry.Name, "OU=");
                    name = RemovePrefix(name, "c_");
                    UpdateOU(entry, name);
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
                        MoveToDeletedOUs(ou, ou.Name);
                    }
                }
                ou.Close();
            }
        }

        private void UpdateOU(DirectoryEntry match, string name)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have checked for updates on OU with name {name}");
            }
            else
            {
                var shouldHaveName = "OU=" + name;
                if (!match.Name.Equals(shouldHaveName))
                {
                    logger.LogInformation($"Attempting to rename OU with dn {match.Properties["distinguishedName"].Value} from {match.Name} to {shouldHaveName}");
                    match.Rename(shouldHaveName);
                    match.CommitChanges();
                    logger.LogInformation($"Updated OU with DN {match.Properties["distinguishedName"].Value} and id {match.Properties[oUIdField].Value}");
                }
            }
        }

        private void CreateOU(DirectoryEntry ouToCreateIn, bool institutionLevel, string id, string name)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have created OU with name {name} and id {id}");
            } else
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
        }

        public string GetNameForOU(bool institutionLevel, string institutionName, string institutionNumber, string institutionAbbreviation, string name, string id, string level, int startYear, string line)
        {
            if (!useDanishCharacters)
            {
                institutionName = institutionName.Unidecode();
                institutionAbbreviation = institutionAbbreviation.Unidecode();
                name = name.Unidecode();
            }

            string calculatedName = "";
            if (institutionLevel)
            {
                calculatedName = institutionOUNameStandard
                    .Replace("{INSTITUTION_NAME}", institutionName)
                    .Replace("{INSTITUTION_ABBREVIATION}", institutionAbbreviation)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber);
            }
            else
            {
                if (startYear != 0 && (!classOUNameStandard.Contains("CLASS_LINE") || (classOUNameStandard.Contains("CLASS_LINE") && !String.IsNullOrEmpty(line))))
                {
                    calculatedName = classOUNameStandard
                    .Replace("{INSTITUTION_NAME}", institutionName)
                    .Replace("{INSTITUTION_ABBREVIATION}", institutionAbbreviation)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber)
                    .Replace("{CLASS_NAME}", name)
                    .Replace("{CLASS_ID}", id)
                    .Replace("{CLASS_LEVEL}", level)
                    .Replace("{CLASS_YEAR}", startYear + "")
                    .Replace("{CLASS_LINE}", line);
                }
                else
                {
                    String nameStandard = null;
                    if (String.IsNullOrEmpty(classOUNameStandardNoClassYear))
                    {
                        nameStandard = classOUNameStandard;
                    } else
                    {
                        nameStandard = classOUNameStandardNoClassYear;
                    }

                    calculatedName = nameStandard
                    .Replace("{INSTITUTION_NAME}", institutionName)
                    .Replace("{INSTITUTION_ABBREVIATION}", institutionAbbreviation)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber)
                    .Replace("{CLASS_NAME}", name)
                    .Replace("{CLASS_ID}", id)
                    .Replace("{CLASS_LEVEL}", level);
                }
            }

            calculatedName = EscapeCharactersForAD(calculatedName, false);

            if (calculatedName.Length > 64)
            {
                calculatedName = calculatedName.Substring(0, 64);
            }

            return calculatedName;
        }

        private string EscapeCharactersForAD(string name, bool forGroup)
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

            if (forGroup)
            {
                name = name.Replace(".", "");
            }

            return name;
        }
        
        private string EscapeCharactersForADSamAccountNameOrMailHard(string name, bool mail = false)
        {
            name = name.Replace("+", "");
            name = name.Replace(",", "");
            name = name.Replace("\"", "");
            name = name.Replace("<", "");
            name = name.Replace(">", "");
            name = name.Replace(";", "");
            name = name.Replace("#", "");
            name = name.Replace("&", "");
            name = name.Replace("/", "");
            name = name.Replace(":", "");
            name = name.Replace("(", "");
            name = name.Replace(")", "");

            if (mail)
            {
                name = name.Replace(" ", "-");
                name = name.Replace("*", "-");
            } else
            {
                name = name.Replace(" ", "_");
                name = name.Replace("*", "_");
                name = name.Replace(".", "");
            }

            if (!useDanishCharacters)
            {
                name = name.Replace("ø", "oe");
                name = name.Replace("å", "aa");
                name = name.Replace("æ", "ae");
                name = name.Replace("Ø", "Oe");
                name = name.Replace("Å", "Aa");
                name = name.Replace("Æ", "Ae");
            }

            return name;
        }

        public void MoveToDeletedOUs(DirectoryEntry toMove, string underInstitution)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have moved OU with dn {toMove.Properties["distinguishedName"][0]} to deleted ous");
            } else
            {
                var newOUName = "OU=slettede_ous_" + DateTime.Now.ToString("yyyy_MM_dd");
                DirectoryEntry rootDeletedOusOuEntry = new DirectoryEntry(@"LDAP://" + rootDeletedOusOu);
                DirectoryEntry dayMatch = null;
                foreach (DirectoryEntry ou in rootDeletedOusOuEntry.Children)
                {
                    if (ou.Name.Equals(newOUName))
                    {
                        dayMatch = ou;
                    }
                }

                if (dayMatch == null)
                {
                    dayMatch = rootDeletedOusOuEntry.Children.Add(newOUName, "OrganizationalUnit");
                    dayMatch.CommitChanges();
                    logger.LogInformation("Created deleted ous ou for today: " + dayMatch.Properties["distinguishedName"][0].ToString());
                }

                var underInstitutionOUName = "OU=" + underInstitution;
                DirectoryEntry institutionMatch = null;
                foreach (DirectoryEntry ou in dayMatch.Children)
                {
                    if (ou.Name.Equals(underInstitutionOUName))
                    {
                        institutionMatch = ou;
                    }
                }

                if (institutionMatch == null)
                {
                    institutionMatch = dayMatch.Children.Add(underInstitutionOUName, "OrganizationalUnit");
                    institutionMatch.CommitChanges();
                    logger.LogInformation("Created institutionOU under deleted ous ou for today: " + institutionMatch.Properties["distinguishedName"][0].ToString());
                }

                logger.LogInformation("Attempting to move OU with DN " + toMove.Properties["distinguishedName"][0].ToString() + " to institutionOU under deleted ous ou for today with dn " + institutionMatch.Properties["distinguishedName"][0].ToString());
                toMove.MoveTo(institutionMatch);
                logger.LogInformation($"Moved OU with name {toMove.Name} to institutionOU with name {institutionMatch.Name} under deleted ous ou for today");

                institutionMatch.Close();
                dayMatch.Close();
            }
        }

        public bool DisableAccount(string username)
        {
            if (usersToIgnore.Contains(username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(username)))
            {
                logger.LogInformation("Ignoring " + username);
                return true;
            }

            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have disabled AD account with username {username} in AD");
                return true;
            }
            else
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
                    if ((bool) user.Enabled)
                    {
                        // disable user
                        user.Enabled = false;
                        user.Save();

                        // set deleted date on user
                        if (!string.IsNullOrEmpty(disabledDateField))
                        {
                            using DirectoryEntry entry = (DirectoryEntry)user.GetUnderlyingObject();
                            entry.Properties[disabledDateField].Value = DateTime.Now.ToString();
                            logger.LogInformation($"Updated {disabledDateField} (disabledDateField) on user with username {username} after disabling user");
                            entry.CommitChanges();
                        }

                        // move user to date specific disabled user group
                        string ouDn = CreateOrGetDisabledUserOUDN();
                        MoveUser(username, user.DistinguishedName, ouDn);

                        logger.LogInformation($"Disabled user with username {username}");

                        powerShellRunner.DisableScript(username);

                        // register as deactivated in OS2skoledata
                        os2SkoledataService.SetActionOnUser(username, ActionType.DEACTIVATE);

                        success = true;
                    }
                }

                return success;
            }
        }

        private string CreateOrGetDisabledUserOUDN()
        {
            if (dryRun)
            {
                logger.LogInformation("DryRun: would have created disabled ou for today");
                return "";
            } else
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
        }

        private void MoveUser(string username, string from, string to)
        {
            if (usersToIgnore.Contains(username) || (usersToInclude.Count() != 0 && !usersToInclude.Contains(username)))
            {
                logger.LogInformation("Ignoring " + username);
                return;
            }

            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have moved AD account with username {username} from {from} to {to}");
            }
            else
            {
                logger.LogInformation("Attempting to move user " + username + " from " + from + " to " + to);

                using DirectoryEntry eLocation = new DirectoryEntry("LDAP://" + from);
                using DirectoryEntry nLocation = new DirectoryEntry("LDAP://" + to);

                eLocation.MoveTo(nLocation);

                logger.LogInformation("Moved user " + username + " from " + from + " to " + to);
            }
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
            if (usernameStandard.Equals(UsernameStandardType.RANDOM))
            {
                char[] possibleChars = "abcdefghjkmnpqrstuvwxyz".ToCharArray();
                StringBuilder randomCharsBuilder = new StringBuilder(randomStandardLetterCount);
                for (int i = 0; i < randomStandardLetterCount; i++)
                {
                    int randomIndex = random.Next(possibleChars.Length);
                    randomCharsBuilder.Append(possibleChars[randomIndex]);
                }

                return randomCharsBuilder.ToString();
            } else
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
        }

        private string GetPrefix()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM))
            {
                return "";
            }
            return usernamePrefix == null ? "" : usernamePrefix;
        }

        private int getNamePartLength()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN))
            {
                return 4;
            } else if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || usernameStandard.Equals(UsernameStandardType.RANDOM))
            {
                return randomStandardLetterCount;
            }

            return 3;
        }

        private bool IsNameFirst()
        {
            if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_LAST) || usernameStandard.Equals(UsernameStandardType.THREE_NUMBERS_THREE_CHARS_FROM_NAME))
            {
                return false;
            }
            return true;
        }

        private List<DirectoryEntry> GetDisabledUsers(string ouPath)
        {
            logger.LogInformation("ouPath " + ouPath);
            List<DirectoryEntry> disabledUsers = new List<DirectoryEntry>();
            using DirectoryEntry entry = new DirectoryEntry(@"LDAP://" + disabledUsersOU);
            logger.LogInformation(entry.Name);
            using DirectorySearcher search = new DirectorySearcher(entry);
            search.Filter = "(&(objectCategory=person)(objectClass=user))";

            // to ensure loading all - set pageSize
            search.PageSize = 1000;
            using SearchResultCollection users = search.FindAll();

            if (users != null)
            {
                List<string> usernames = new List<string>();
                foreach (SearchResult result in users)
                {
                    DirectoryEntry userEntry = result.GetDirectoryEntry();
                    if (IsUserDisabled(userEntry))
                    {
                        disabledUsers.Add(userEntry);
                    }
                }
                return disabledUsers;
            }

            return null;
        }

        public static bool IsUserDisabled(DirectoryEntry user)
        {
            if (user.Properties["userAccountControl"].Value != null)
            {
                int userAccountControl = (int)user.Properties["userAccountControl"].Value;
                return (userAccountControl & 2) != 0;
            }
            return false;
        }

        private List<string> GetAllUsernames(DirectoryEntry entry, bool filterByMark)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person))");

            using DirectorySearcher search = new DirectorySearcher(entry);
            search.Filter = filter;
            search.PropertiesToLoad.Add("sAMAccountName");
            if (!createOUHierarchy && filterByMark)
            {
                search.PropertiesToLoad.Add(adFieldForOS2skoledataMark);
            }

            // to ensure loading all - set pageSize
            search.PageSize = 1000;
            using SearchResultCollection users = search.FindAll();

            if (users != null)
            {
                List<string> usernames = new List<string>();
                foreach (SearchResult user in users)
                {
                    if (createOUHierarchy && !filterByMark)
                    {
                        usernames.Add(user.Properties["sAMAccountName"][0].ToString());
                    } else
                    {
                        if (user.Properties[adFieldForOS2skoledataMark] != null && user.Properties[adFieldForOS2skoledataMark].Count > 0 && Object.Equals(user.Properties[adFieldForOS2skoledataMark][0].ToString(), os2skoledataMark))
                        {
                            usernames.Add(user.Properties["sAMAccountName"][0].ToString());
                        }
                    }
                }
                return usernames;
            }

            return null;
        }

        private List<UsernameInstitutionDTO> GetAllUsernamesAndInstitutionNumbers(DirectoryEntry entry, bool filterByMark)
        {
            var filter = string.Format("(&(objectClass=user)(objectClass=person))");

            using DirectorySearcher search = new DirectorySearcher(entry);
            search.Filter = filter;
            search.PropertiesToLoad.Add("sAMAccountName");
            search.PropertiesToLoad.Add(institutionNumberField);
            if (!createOUHierarchy && filterByMark)
            {
                search.PropertiesToLoad.Add(adFieldForOS2skoledataMark);
            }

            // to ensure loading all - set pageSize
            search.PageSize = 1000;
            using SearchResultCollection users = search.FindAll();

            if (users != null)
            {
                List<UsernameInstitutionDTO> dtos = new List<UsernameInstitutionDTO>();
                foreach (SearchResult user in users)
                {
                    if (createOUHierarchy && !filterByMark)
                    {
                        AddDTO(dtos, user);
                    }
                    else
                    {
                        if (user.Properties[adFieldForOS2skoledataMark] != null && user.Properties[adFieldForOS2skoledataMark].Count > 0 && Object.Equals(user.Properties[adFieldForOS2skoledataMark][0].ToString(), os2skoledataMark))
                        {
                            AddDTO(dtos, user);
                        }
                    }
                }
                return dtos;
            }

            return null;
        }

        private List<string> GetAllUsernamesWithIgnoreFieldFilled()
        {
            // search for users where ignoreField has a value (no matter what the value is - * is wildcard)
            var filter = string.Format("(&(objectClass=user)(objectClass=person)({0}=*))", ignoreField);

            using DirectorySearcher search = new DirectorySearcher(new DirectoryEntry());
            search.Filter = filter;
            search.PropertiesToLoad.Add("sAMAccountName");
            search.PropertiesToLoad.Add(ignoreField);

            // to ensure loading all - set pageSize
            search.PageSize = 1000;
            using SearchResultCollection users = search.FindAll();

            if (users != null)
            {
                List<string> usernamesToIgnore = new List<string>();
                foreach (SearchResult user in users)
                {
                    usernamesToIgnore.Add(user.Properties["sAMAccountName"][0].ToString());
                }
                return usernamesToIgnore;
            }

            return null;
        }

        private void AddDTO(List<UsernameInstitutionDTO> dtos, SearchResult user)
        {
            UsernameInstitutionDTO dto = new UsernameInstitutionDTO();
            dto.Username = user.Properties["sAMAccountName"][0].ToString();
            dto.institutionNumbers = new List<string>();

            string numberString = null;
            if (user.Properties.Contains(institutionNumberField) && user.Properties[institutionNumberField] != null && user.Properties[institutionNumberField].Count > 0 && !string.IsNullOrEmpty(user.Properties[institutionNumberField][0].ToString()))
            {
                numberString = user.Properties[institutionNumberField][0].ToString();
            }
            
            if (numberString != null && numberString.Count() > 0)
            {
                dto.institutionNumbers = numberString.Split(",").ToList();
            }

            // only add if we can fetch institutionNumbers
            if (dto.institutionNumbers.Count > 0)
            {
                dtos.Add(dto);
            }
        }

        private DirectoryEntry GetSecurityGroupOUInEntry(DirectoryEntry entry)
        {
            if (createOUHierarchy)
            {
                DirectoryEntry securityGroupOU = null;

                if (dryRun)
                {
                    logger.LogInformation($"DryRun: would have created or fetched security group Ou");
                } else
                {
                    if (!DirectoryEntry.Exists("LDAP://OU=" + securityGroupOUName + "," + entry.Properties["distinguishedName"].Value.ToString()))
                    {

                        securityGroupOU = entry.Children.Add("OU=" + securityGroupOUName, "OrganizationalUnit");
                        securityGroupOU.CommitChanges();
                    }
                    else
                    {
                        securityGroupOU = new DirectoryEntry("LDAP://OU=" + securityGroupOUName + "," + entry.Properties["distinguishedName"].Value.ToString());
                    }
                }

                return securityGroupOU;
            } else
            {
                return new DirectoryEntry(@"LDAP://" + allSecurityGroupsOU);
            }
            
        }

        private string GetClassSecurityGroupName(Group currentClass, Institution institution, string standard, string standardNoYear, string standardName, string standardNameNoYear)
        {
            if (String.IsNullOrEmpty(standard))
            {
                return null;
            }   
                
            string institutionName = institution.InstitutionName;
            string groupName = currentClass.GroupName;
            if (!useDanishCharacters)
            {
                institutionName = institutionName.Unidecode();
                groupName = groupName.Unidecode();
            }

            String name;
            if (currentClass.StartYear != 0 && (!standard.Contains("CLASS_LINE") || (!String.IsNullOrEmpty(currentClass.Line) && standard.Contains("CLASS_LINE"))))
            {
                name = standard;
                name = FindPlaceholdersAndClassSecurityGroupNameStandard(standardName, currentClass, institution, institutionName, groupName, name);

            }
            else
            {
                if (String.IsNullOrEmpty(standardNoYear)) {
                    name = standard;
                    name = FindPlaceholdersAndClassSecurityGroupNameStandard(standardName, currentClass, institution, institutionName, groupName, name);
                } else
                {
                    name = standardNoYear;
                    name = FindPlaceholdersAndClassSecurityGroupNameStandard(standardNoYear, currentClass, institution, institutionName, groupName, name);
                }
            }

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0,64);
            }

            return name;
        }

        private string FindPlaceholdersAndClassSecurityGroupNameStandard(string standard, Group currentClass, Institution institution, string institutionName, string groupName, string name)
        {
            // find the placeholders and for each replace
            string pattern = @"\{.*?\}";
            Regex regex = new Regex(pattern);
            MatchCollection placeholders = regex.Matches(name);

            foreach (Match match in placeholders)
            {
                string placeholder = match.Value;
                name = ReplaceGroupNameStandard(standard, name, placeholder, institutionName, institution, groupName, currentClass);
            }

            return name;
        }

        private string ReplaceGroupNameStandard(string standardName, string name, String placeholder, string institutionName, Institution institution, string groupName, Group currentClass)
        {
            string lettersPattern = @"([a-zA-Z_]+)";
            string numberPattern = @"(\d{1,2})";

            Match lettersMatch = Regex.Match(placeholder, lettersPattern);
            Match numberMatch = Regex.Match(placeholder, numberPattern);

            string letters = lettersMatch.Success ? lettersMatch.Groups[1].Value : null;
            string numberString = numberMatch.Success ? numberMatch.Groups[1].Value : null;

            int number = 0;
            if (numberString != null)
            {
                bool isNumberValid = int.TryParse(numberString, out number);
                if (!isNumberValid)
                {
                    throw new Exception($"Invalid number in class group name Standard: {standardName}. Invalid number: {numberString}");
                }
            }

            bool numberFirst = false;
            bool hasNumber = false;

            if (!string.IsNullOrEmpty(letters) && !string.IsNullOrEmpty(numberString))
            {
                hasNumber = true;
                numberFirst = placeholder.IndexOf(letters) > placeholder.IndexOf(numberString);
            }

            if (placeholder.Contains("INSTITUTION_NAME"))
            {
                name = ReplaceBasedOnNumber(name, placeholder, institutionName, number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("INSTITUTION_NUMBER"))
            {
                name = ReplaceBasedOnNumber(name, placeholder, institution.InstitutionNumber, number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("CLASS_NAME"))
            {
                name = ReplaceBasedOnNumber(name, placeholder, groupName, number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("CLASS_ID"))
            {
                name = ReplaceBasedOnNumber(name, placeholder, currentClass.GroupId, number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("CLASS_LEVEL"))
            {
                name = ReplaceBasedOnNumber(name, placeholder, currentClass.GroupLevel, number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("CLASS_YEAR") && currentClass.StartYear != 0)
            {
                name = ReplaceBasedOnNumber(name, placeholder, currentClass.StartYear.ToString(), number, numberFirst, hasNumber);
            }
            else if (placeholder.Contains("CLASS_LINE") && !String.IsNullOrEmpty(currentClass.Line))
            {
                name = ReplaceBasedOnNumber(name, placeholder, currentClass.Line, number, numberFirst, hasNumber);
            }

            return name;
        }

        private string ReplaceBasedOnNumber(string name, string placeholder, string value, int number, bool numberFirst, bool hasNumber)
        {
            if (hasNumber && value.Length > number)
            {
                // if numberFirst - then cut from end else from beginning
                string finalValue = value;
                if (numberFirst)
                {
                    finalValue = finalValue.Substring(0, number);
                }
                else
                {
                    finalValue = finalValue.Substring(finalValue.Length - number);
                }
                name = name.Replace(placeholder, finalValue);

            }
            else
            {
                name = name.Replace(placeholder, value);
            }

            return name;
        }

        private string GetInstitutionGroupName(string type, Institution institution)
        {
            (string name, string abbreviation) institutionNames = getInstitutionNamesTuple(institution);

            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institutionNames.name)
                        .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institutionNames.name)
                        .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionSecurityGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institutionNames.name)
                        .Replace("{INSTITUTION_ABBREVIATION}", institutionNames.abbreviation)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                default:
                    throw new Exception("Unknown institution security group name standard type: " + type);
            }

            name = EscapeCharactersForAD(name, true);

            if (name.Length > 64)
            {
                name = name.Substring(0,64);
            }

            return name;
        }


        private DirectoryEntry UpdateGroup(DirectoryEntry securityGroupOU, string id, string name, string institutionNumber, string samAccountName, string mail = null, List<string> renamedOrNewSecurityGroupIds = null)
        {
            if (String.IsNullOrEmpty(name))
            {
                return null;
            }

            logger.LogInformation($"Checking if security group with name {name} and id {id} should be updated or created");
            DirectoryEntry group = GetGroupFromId(id);

            if (dryRun)
            {
                if (group == null)
                {
                    logger.LogInformation($"DryRun: would have created Security Group with name {name} and id {id}.");
                }
                else
                {
                    logger.LogInformation($"DryRun: would have updated Security Group with name {name} and id {id}.");
                }
            }
            else
            {
                bool created = false;
                bool updated = false;
                bool hasPrefix = false;
                bool wasRenamed = false;

                if (group == null)
                {
                    logger.LogInformation($"Did not find group with name {name} and id {id} in AD. Attempting to create");
                    if (renamedOrNewSecurityGroupIds != null && name.Length <= 62)
                    {
                        group = securityGroupOU.Children.Add("CN=c_" + RemovePrefix(name, "c_"), "group");
                        hasPrefix = true;
                    }
                    else
                    {
                        group = securityGroupOU.Children.Add("CN=" + name, "group");
                    }
                    group.Properties[securityGroupIdField].Value = id;
                    logger.LogInformation("Setting value " + id + " in field (securityGroupIdField) " + securityGroupIdField + " when creating group " + name);
                    group.Properties[securityGroupInstitutionNumberField].Value = institutionNumber;
                    logger.LogInformation("Setting value " + institutionNumber + " in field (securityGroupInstitutionNumberField) " + securityGroupInstitutionNumberField + " when creating group " + name);

                    if (setSamAccountName)
                    {
                        group.Properties["sAMAccountName"].Value = samAccountName;
                        logger.LogInformation("Setting value " + samAccountName + " in field samAccountName when creating group " + name);
                    }

                    if (!String.IsNullOrEmpty(securityGroupMailField) && !String.IsNullOrEmpty(mail))
                    {
                        group.Properties[securityGroupMailField].Value = mail;
                        logger.LogInformation("Setting value " + mail + " in field " + securityGroupMailField + " when creating group " + name);
                    }

                    created = true;
                }
                else
                {
                    // First handle rename if needed
                    if (!group.Name.Equals("CN=" + name))
                    {
                        logger.LogInformation($"Updating name on group {group.Name} from {group.Name} to {"CN=" + name}");
                        if (renamedOrNewSecurityGroupIds != null && name.Length <= 62)
                        {
                            group.Rename("CN=c_" + RemovePrefix(name, "c_"));
                            hasPrefix = true;
                        }
                        else
                        {
                            group.Rename("CN=" + name);
                        }
                        wasRenamed = true;

                        // Refresh the group object after rename to ensure we have the latest state
                        group.RefreshCache();
                    }

                    // Now handle all other property updates
                    if (!object.Equals(group.Properties[securityGroupInstitutionNumberField].Value, institutionNumber))
                    {
                        logger.LogInformation($"Updating securityGroupInstitutionNumberField on group {group.Name} from {group.Properties[securityGroupInstitutionNumberField].Value} to {institutionNumber}");
                        group.Properties[securityGroupInstitutionNumberField].Value = institutionNumber;
                        updated = true;
                    }

                    if (setSamAccountName)
                    {
                        var currentSamAccountName = group.Properties["sAMAccountName"].Value;
                        if (!Object.Equals(currentSamAccountName, samAccountName))
                        {
                            logger.LogInformation($"Updating samAccountName on group {group.Name} from {currentSamAccountName} to {samAccountName}");
                            group.Properties["sAMAccountName"].Value = samAccountName;
                            updated = true;
                        }
                    }

                    bool updateGeneratedMail = false;
                    if (securityGroupOverwriteExistingMail && !String.IsNullOrEmpty(securityGroupMailField) && !String.IsNullOrEmpty(mail))
                    {
                        updateGeneratedMail = true;
                    }
                    else if (!String.IsNullOrEmpty(securityGroupMailField) && !String.IsNullOrEmpty(mail))
                    {
                        string currentMail = group.Properties[securityGroupMailField].Value?.ToString();
                        if (String.IsNullOrEmpty(currentMail))
                        {
                            updateGeneratedMail = true;
                        }
                    }

                    if (updateGeneratedMail)
                    {
                        var currentMail = group.Properties[securityGroupMailField].Value;
                        if (!Object.Equals(currentMail, mail))
                        {
                            logger.LogInformation($"Updating mail on group {group.Name} from {currentMail} to {mail}");
                            group.Properties[securityGroupMailField].Value = mail;
                            updated = true;
                        }
                    }
                }

                // Handle prefix logic
                if (hasPrefix)
                {
                    if (setSamAccountName && samAccountName != null)
                    {
                        // If name has prefix, also set prefix on samAccountName
                        group.Properties["sAMAccountName"].Value = "c_" + RemovePrefix(samAccountName, "c_");
                        updated = true;
                    }

                    renamedOrNewSecurityGroupIds.Add(id);
                }

                // Commit changes (this will handle both new properties and any remaining updates after rename)
                if (updated || created)
                {
                    group.CommitChanges();
                }

                if (created)
                {
                    logger.LogInformation($"Created security group with name {name} and id {id}");
                }
                else if (updated || wasRenamed)
                {
                    logger.LogInformation($"Updated security group with name {name} and id {id}");
                }
            }

            return group;
        }

        private string GenerateSamAccountName(Group group, Institution institution, GroupType groupType, string additionalInfo, List<Group> classes)
        {
            if (!setSamAccountName)
            {
                return null;
            }

            string institutionName = institution == null ? null : institution.InstitutionName;
            string groupName = group == null ? null : group.GroupName;
            string dbGroupType = group == null ? null : group.GroupType.ToString().ToLower();
            if (!useDanishCharacters)
            {
                institutionName = institution == null ? null : institutionName.Unidecode();
                additionalInfo = additionalInfo == null ? null : additionalInfo.Unidecode();
                groupName = group == null ? null : group.GroupName.Unidecode();
                dbGroupType = dbGroupType == null ? null : dbGroupType.Unidecode();
            }

            string samAccountName = "";
            string prefix = string.IsNullOrEmpty(samAccountPrefix) ? "" : samAccountPrefix + "_";

            if (samAccountUseInstitutionType && institution != null)
            {
                if (institution.Type.Equals(InstitutionType.SCHOOL))
                {
                    prefix = prefix + "S_";
                } else if (institution.Type.Equals(InstitutionType.DAYCARE))
                {
                    prefix = prefix + "D_";
                }
                else if (institution.Type.Equals(InstitutionType.FU))
                {
                    prefix = prefix + "F_";
                }
                else if (institution.Type.Equals(InstitutionType.MUNICIPALITY)) 
                {
                    prefix = prefix + "K_";
                }
            }

            switch (groupType)
            {
                case GroupType.INSTITUTION_ALL:
                    samAccountName = $"{prefix}{institutionName}_alle";
                    break;
                case GroupType.INSTITUTION_STUDENTS:
                    samAccountName = $"{prefix}{institutionName}_elever";
                    break;
                case GroupType.INSTITUTION_EMPLOYEES:
                    samAccountName = $"{prefix}{institutionName}_ansatte";
                    break;
                case GroupType.INSTITUTION_EMPLOYEE_TYPES:
                    samAccountName = $"{prefix}{institutionName}_{additionalInfo}_alle";
                    break;
                case GroupType.INSTITUTION_STUDENT_YEARS:
                    samAccountName = $"{prefix}{institutionName}_elever_{additionalInfo}";
                    break;
                case GroupType.INSTITUTION_LEVELS:
                    samAccountName = $"{prefix}{institutionName}_{additionalInfo}.klasse";
                    break;
                case GroupType.CLASS_ALL:
                    bool multipleWithSameLineAndLevel = classes.Where(c => c.Line != null && group.Line != null && Object.Equals(c.Line.ToLower(), group.Line.ToLower()) && Object.Equals(c.StartYear, group.StartYear)).Count() > 1;
                    if (group.Line == null || multipleWithSameLineAndLevel)
                    {
                        samAccountName = $"{prefix}{institutionName}_{group.StartYear}_{groupName}";
                    } else
                    {
                        samAccountName = $"{prefix}{institutionName}_{group.StartYear}_{group.Line}";
                    }
                    break;
                case GroupType.CLASS_STUDENTS:
                    bool multipleWithSameLineAndLevelStudents = classes.Where(c => c.Line != null && group.Line != null && Object.Equals(c.Line.ToLower(), group.Line.ToLower()) && Object.Equals(c.StartYear, group.StartYear)).Count() > 1;
                    if (group.Line == null || multipleWithSameLineAndLevelStudents)
                    {
                        samAccountName = $"{prefix}{institutionName}_students_{group.StartYear}_{groupName}";
                    }
                    else
                    {
                        samAccountName = $"{prefix}{institutionName}_students_{group.StartYear}_{group.Line}";
                    }
                    break;
                case GroupType.CLASS_EMPLOYEES:
                    bool multipleWithSameLineAndLevelEmployees = classes.Where(c => c.Line != null && group.Line != null && Object.Equals(c.Line.ToLower(), group.Line.ToLower()) && Object.Equals(c.StartYear, group.StartYear)).Count() > 1;
                    if (group.Line == null || multipleWithSameLineAndLevelEmployees)
                    {
                        samAccountName = $"{prefix}{institutionName}_employees_{group.StartYear}_{groupName}";
                    }
                    else
                    {
                        samAccountName = $"{prefix}{institutionName}_employees_{group.StartYear}_{group.Line}";
                    }
                    break;
                case GroupType.OTHER_GROUPS_ALL:
                    samAccountName = $"{prefix}{institutionName}_{group.DatabaseId}_{groupName}_{dbGroupType}";
                    break;
                case GroupType.GLOBAL_STUDENTS:
                    samAccountName = $"{prefix}{globalStudentSecurityGroupName}";
                    break;
                case GroupType.GLOBAL_EMPLOYEES:
                    samAccountName = $"{prefix}{globalEmployeeSecurityGroupName}";
                    break;
                case GroupType.GLOBAL_EMPLOYEE_TYPES_SCHOOL:
                    samAccountName = $"{prefix}global_{additionalInfo}_skoler";
                    break;
                case GroupType.GLOBAL_EMPLOYEE_TYPES_DAYCARE:
                    samAccountName = $"{prefix}global_{additionalInfo}_daginstitutioner";
                    break;
                case GroupType.GLOBAL_EMPLOYEE_TYPES_FU:
                    samAccountName = $"{prefix}global_{additionalInfo}_fu";
                    break;
                case GroupType.GLOBAL_LEVEL:
                    samAccountName = $"{prefix}global_{additionalInfo}_klasse";
                    break;
            }

            samAccountName = EscapeCharactersForADSamAccountNameOrMailHard(samAccountName);

            return samAccountName;
        }

        private string GetSecurityGroupMail(Group group, Institution institution, List<Group> classes, GroupType securityGroupType, string additionalInfo = null)
        {
            if (String.IsNullOrEmpty(securityGroupMailField))
            {
                return null;
            }

            string institutionName = institution.InstitutionName;
            string groupName = group == null ? "" : group.GroupName;

            string mail = "";

            switch (securityGroupType)
            {
                case GroupType.CLASS_ALL:
                    bool multipleWithSameLineAndLevel = classes.Where(c => c.Line != null && group.Line != null && Object.Equals(c.Line.ToLower(), group.Line.ToLower()) && Object.Equals(c.StartYear, group.StartYear)).Count() > 1;
                    if (group.Line == null && group.StartYear == 0)
                    {
                        mail = securityGroupNoLineNoYearMailNameStandard;
                        mail = FindPlaceholdersAndClassSecurityGroupNameStandard("securityGroupNoLineNoYearMailNameStandard", group, institution, institutionName, groupName, mail);
                    }
                    else if (group.Line == null || multipleWithSameLineAndLevel)
                    {
                        mail = securityGroupNoLineMailNameStandard;
                        mail = FindPlaceholdersAndClassSecurityGroupNameStandard("securityGroupNoLineMailNameStandard", group, institution, institutionName, groupName, mail);
                    }
                    else
                    {
                        mail = securityGroupNormalMailNameStandard;
                        mail = FindPlaceholdersAndClassSecurityGroupNameStandard("securityGroupNormalMailNameStandard", group, institution, institutionName, groupName, mail);
                    }
                    break;
                case GroupType.OTHER_GROUPS_ALL:
                    switch (group.GroupType)
                    {
                        case DBGroupType.ÅRGANG:
                            mail = otherGroupsÅrgangSecurityGroupMailStandard;
                            break;
                        case DBGroupType.RETNING:
                            mail = otherGroupsRetningSecurityGroupMailStandard;
                            break;
                        case DBGroupType.HOLD:
                            mail = otherGroupsHoldSecurityGroupMailStandard;
                            break;
                        case DBGroupType.SFO:
                            mail = otherGroupsSFOSecurityGroupMailStandard;
                            break;
                        case DBGroupType.TEAM:
                            mail = otherGroupsTeamSecurityGroupMailStandard;
                            break;
                        case DBGroupType.ANDET:
                            mail = otherGroupsAndetSecurityGroupMailStandard;
                            break;
                        default:
                            return null;
                    }

                    if (String.IsNullOrEmpty(mail) || !mail.Contains("{DATABASE_ID}"))
                    {
                        return null;
                    }

                    mail = mail.Replace("{INSTITUTION_NAME}", institutionName)
                            .Replace("{INSTITUTION_ABBREVIATION}", institution.Abbreviation)
                            .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                            .Replace("{CLASS_NAME}", groupName)
                            .Replace("{CLASS_ID}", group.GroupId)
                            .Replace("{DATABASE_ID}", group.DatabaseId.ToString());
                    break;
                case GroupType.INSTITUTION_EMPLOYEE_TYPES:
                    mail = securityGroupForEmployeeTypeMailStandard;
                    if (String.IsNullOrEmpty(mail))
                    {
                        return null;
                    }

                    mail = mail.Replace("{INSTITUTION_NAME}", institutionName)
                            .Replace("{INSTITUTION_ABBREVIATION}", institution.Abbreviation)
                            .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                            .Replace("{TYPE}", additionalInfo);
                    break;
                case GroupType.INSTITUTION_EMPLOYEES:
                    mail = securityGroupForEmployeesMailStandard;
                    if (String.IsNullOrEmpty(mail))
                    {
                        return null;
                    }

                    mail = mail.Replace("{INSTITUTION_NAME}", institutionName)
                            .Replace("{INSTITUTION_ABBREVIATION}", institution.Abbreviation)
                            .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                default:
                    return null;
            }

            mail = mail.Replace("{DOMAIN}", securityGroupMailDomain);
            mail = EscapeCharactersForADSamAccountNameOrMailHard(mail, true);

            return mail.ToLower();
        }

        public void MoveKeepAliveUsers(List<string> keepAliveUsernames)
        {
            if (string.IsNullOrEmpty(keepAliveOU))
            {
                return;
            }

            foreach (string username in keepAliveUsernames)
            {
                using DirectoryEntry user = GetUserFromSAMAccountName(username);
                if (user != null)
                {
                    string cn = user.Properties["cn"][0].ToString();
                    string dn = user.Properties["distinguishedName"].Value.ToString();
                    string ouDN = dn.Replace("CN=" + cn + ",", "");

                    if (!keepAliveOU.Equals(ouDN))
                    {
                        MoveUser(username, dn, keepAliveOU);
                    }
                }
            }
        }
    }

    public enum GroupType
    {
        INSTITUTION_ALL,
        INSTITUTION_STUDENTS,
        INSTITUTION_EMPLOYEES,
        INSTITUTION_EMPLOYEE_TYPES,
        INSTITUTION_STUDENT_YEARS,
        INSTITUTION_LEVELS,
        CLASS_ALL,
        CLASS_STUDENTS,
        CLASS_EMPLOYEES,
        OTHER_GROUPS_ALL,
        GLOBAL_STUDENTS,
        GLOBAL_EMPLOYEES,
        GLOBAL_EMPLOYEE_TYPES_SCHOOL,
        GLOBAL_EMPLOYEE_TYPES_DAYCARE,
        GLOBAL_EMPLOYEE_TYPES_FU,
        GLOBAL_LEVEL
    }

}
