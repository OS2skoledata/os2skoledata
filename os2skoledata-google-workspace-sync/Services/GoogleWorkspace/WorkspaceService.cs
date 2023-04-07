using Google.Apis.Admin.Directory.directory_v1;
using Google.Apis.Admin.Directory.directory_v1.Data;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Drive.v3;
using Google.Apis.Drive.v3.Data;
using Google.Apis.Licensing.v1;
using Google.Apis.Licensing.v1.Data;
using Google.Apis.Services;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace.Model;
using os2skoledata_google_workspace_sync.Services.OS2skoledata;
using os2skoledata_google_workspace_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Unidecode.NET;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace
{
    internal class WorkspaceService : ServiceBase<WorkspaceService>
    {
        private readonly OS2skoledataService oS2skoledataService;
        private readonly string emailAccountToImpersonate;
        private readonly string domain;
        private readonly string rootOrgUnitPath;
        private readonly DirectoryService directoryService;
        private readonly DriveService driveService;
        private readonly LicensingService licenseService;
        private readonly string[] oUsToAlwaysCreate;
        private readonly string employeeOUName;
        private readonly string studentOUName;
        private readonly string studentsWithoutGroupsOUName;
        private readonly string classOUNameStandard;
        private readonly string institutionOUNameStandard;
        private readonly string globalEmployeeDriveName;
        private readonly string allInInstitutionDriveNameStandard;
        private readonly string allStudentsInInstitutionDriveNameStandard;
        private readonly string allEmployeesInInstitutionDriveNameStandard;
        private readonly string classDriveNameStandard;
        private readonly string[] globallyExcludedRoles;
        private readonly Dictionary<string, string[]> exludedRolesInInstitution;
        private readonly string suspendedUsersOU;
        private readonly string deletedOusOu;
        private readonly string deletedDrivePrefix;
        public readonly string allInInstitutionGroupNameStandard;
        public readonly string allStudentsInInstitutionGroupNameStandard;
        public readonly string allEmployeesInInstitutionGroupNameStandard;
        public readonly string groupForEmployeeTypeNameStandard;
        public readonly string groupForYearNameStandard;
        public readonly string classGroupNameStandard;
        public readonly string globalEmployeeGroupName;
        private readonly string schoolOUName;
        private readonly string daycareOUName;
        private readonly string staffLicenseProductId;
        private readonly string staffLicenseSkuId;
        private readonly string studentLicenseProductId;
        private readonly string studentLicenseSkuId;

        // caching because Google Workspace is slooooow
        private Dictionary<string, OrgUnit> institutionStudentsOUMapping;
        private Dictionary<string, OrgUnit> institutionStudentsWithoutGroupOUMapping;
        private Dictionary<string, OrgUnit> institutionEmployeesOUMapping;
        private Dictionary<string, OrgUnit> workspaceIdsOUMapping;


        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };

        public WorkspaceService(IServiceProvider sp) : base(sp)
        {
            oS2skoledataService = sp.GetService<OS2skoledataService>();

            var serviceAccountDataFilePath = settings.WorkspaceSettings.ServiceAccountDataFilePath;
            emailAccountToImpersonate = settings.WorkspaceSettings.EmailAccountToImpersonate;
            domain = settings.WorkspaceSettings.Domain;
            rootOrgUnitPath = settings.WorkspaceSettings.RootOrgUnitPath;
            oUsToAlwaysCreate = settings.WorkspaceSettings.OUsToAlwaysCreate;

            employeeOUName = settings.WorkspaceSettings.NamingSettings.EmployeeOUName;
            studentOUName = settings.WorkspaceSettings.NamingSettings.StudentOUName;
            studentsWithoutGroupsOUName = settings.WorkspaceSettings.NamingSettings.StudentsWithoutGroupsOUName;
            classOUNameStandard = settings.WorkspaceSettings.NamingSettings.ClassOUNameStandard;
            institutionOUNameStandard = settings.WorkspaceSettings.NamingSettings.InstitutionOUNameStandard;
            globalEmployeeDriveName = settings.WorkspaceSettings.NamingSettings.GlobalEmployeeDriveName;
            allInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllInInstitutionDriveNameStandard;
            allStudentsInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllStudentsInInstitutionDriveNameStandard;
            allEmployeesInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllEmployeesInInstitutionDriveNameStandard;
            classDriveNameStandard = settings.WorkspaceSettings.NamingSettings.ClassDriveNameStandard;
            globallyExcludedRoles = settings.WorkspaceSettings.FilteringSettings.GloballyExcludedRoles;
            exludedRolesInInstitution = settings.WorkspaceSettings.FilteringSettings.ExludedRolesInInstitution;
            suspendedUsersOU = settings.WorkspaceSettings.SuspendedUsersOU;
            deletedOusOu = settings.WorkspaceSettings.DeletedOusOu;
            deletedDrivePrefix = settings.WorkspaceSettings.NamingSettings.DeletedDrivePrefix;
            allInInstitutionGroupNameStandard = settings.WorkspaceSettings.NamingSettings.AllInInstitutionGroupNameStandard;
            allStudentsInInstitutionGroupNameStandard = settings.WorkspaceSettings.NamingSettings.AllStudentsInInstitutionGroupNameStandard;
            allEmployeesInInstitutionGroupNameStandard = settings.WorkspaceSettings.NamingSettings.AllEmployeesInInstitutionGroupNameStandard;
            groupForEmployeeTypeNameStandard = settings.WorkspaceSettings.NamingSettings.GroupForEmployeeTypeNameStandard;
            groupForYearNameStandard = settings.WorkspaceSettings.NamingSettings.GroupForYearNameStandard;
            classGroupNameStandard = settings.WorkspaceSettings.NamingSettings.ClassGroupNameStandard;
            globalEmployeeGroupName = settings.WorkspaceSettings.NamingSettings.GlobalEmployeeGroupName;
            schoolOUName = settings.WorkspaceSettings.NamingSettings.SchoolOUName;
            daycareOUName = settings.WorkspaceSettings.NamingSettings.DaycareOUName;
            staffLicenseProductId = settings.WorkspaceSettings.LicensingSettings.StaffLicenseProductId;
            staffLicenseSkuId = settings.WorkspaceSettings.LicensingSettings.StaffLicenseSkuId;
            studentLicenseProductId = settings.WorkspaceSettings.LicensingSettings.StudentLicenseProductId;
            studentLicenseSkuId = settings.WorkspaceSettings.LicensingSettings.StudentLicenseSkuId;

            using FileStream fileStream = System.IO.File.OpenRead(serviceAccountDataFilePath);
            string fileContents;
            using (StreamReader reader = new StreamReader(fileStream))
            {
                fileContents = reader.ReadToEnd();
            }
            ServiceAccountCredentialDTO serviceAccountCredentialDTO = JsonConvert.DeserializeObject<ServiceAccountCredentialDTO>(fileContents, jsonSerializerSettings);

            ServiceAccountCredential credentialWithScopes = new ServiceAccountCredential(
               new ServiceAccountCredential.Initializer(serviceAccountCredentialDTO.ClientEmail)
               {
                   Scopes = new[] {
                        DirectoryService.Scope.AdminDirectoryUser,
                        DirectoryService.Scope.AdminDirectoryOrgunit,
                        DirectoryService.Scope.AdminDirectoryGroup,
                        DriveService.Scope.Drive,
                        LicensingService.Scope.AppsLicensing
                    },
                   KeyId = serviceAccountCredentialDTO.KeyId,
                   User = emailAccountToImpersonate
               }.FromPrivateKey(serviceAccountCredentialDTO.PrivateKey));

            directoryService = new DirectoryService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Directory API Sample",
            });

            driveService = new DriveService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Drive API Sample",
            });

            licenseService = new LicensingService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Licensing API Sample",
            });
        }

        public void InitializeDictionaries()
        {
            institutionStudentsOUMapping = new Dictionary<string, OrgUnit>();
            institutionStudentsWithoutGroupOUMapping = new Dictionary<string, OrgUnit>();
            institutionEmployeesOUMapping = new Dictionary<string, OrgUnit>();
            workspaceIdsOUMapping = new Dictionary<string, OrgUnit>();
        }

        public void UpdateClassesForInstitution(List<DBGroup> classes, Institution institution, OrgUnit institutionOrgUnit)
        {
            logger.LogInformation($"Handling classes for institution {institution.InstitutionName}");
            OrgUnit studentOrgUnit = GetStudentOrgUnitForInstitution(institutionOrgUnit);

            // get children of root
            List<OrgUnit> children = ListOrgUnits(studentOrgUnit.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);

            // delete 
            foreach (OrgUnit ou in children)
            {
                if (classes.Where(c => c.GoogleWorkspaceId.Equals(ou.OrgUnitId)).ToList().Count == 0)
                {
                    MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath);
                }
            }

            foreach (DBGroup group in classes)
            {
                string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
                OrgUnit match = GetOrgUnit(group.GoogleWorkspaceId);

                if (match == null)
                {
                    // create
                    OrgUnit ou = CreateOU(studentOrgUnit.OrgUnitPath, false, name);
                    group.GoogleWorkspaceId = ou.OrgUnitId;
                    oS2skoledataService.SetFields(group.DatabaseId, EntityType.GROUP, SetFieldType.GROUP_WORKSPACE_ID, ou.OrgUnitId);
                }
                else
                {
                    // check if ou should be updated
                    UpdateOU(name, match);
                }
            }
        }

        public void UpdateInstitutions(List<Institution> institutions)
        {
            logger.LogInformation("Handling institutions");

            OrgUnit schoolsOU = GetSchoolsOrgUnit(rootOrgUnitPath);
            OrgUnit daycaresOU = GetDaycaresOrgUnit(rootOrgUnitPath);

            // get children of root
            List<OrgUnit> children = ListOrgUnits(rootOrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);
            children.AddRange(ListOrgUnits(schoolsOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children));
            children.AddRange(ListOrgUnits(daycaresOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children));

            // delete 
            foreach (OrgUnit ou in children)
            {
                if (ou.OrgUnitPath.Equals(deletedOusOu) || ou.OrgUnitPath.Equals(suspendedUsersOU) || ou.OrgUnitPath.Equals(schoolsOU.OrgUnitPath) || ou.OrgUnitPath.Equals(daycaresOU.OrgUnitPath))
                {
                    continue;
                }

                if (institutions.Where(i => i.GoogleWorkspaceId != null && i.GoogleWorkspaceId.Equals(ou.OrgUnitId)).ToList().Count == 0)
                {
                    MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath);
                }
            }

            // create or update
            foreach (Institution institution in institutions)
            {
                logger.LogInformation($"Handling institution with number {institution.InstitutionNumber} and name  {institution.InstitutionName}");
                string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
                OrgUnit match = GetOrgUnit(institution.GoogleWorkspaceId);

                if (match == null)
                {
                    // create
                    OrgUnit ou;
                    if (institution.Type.Equals(InstitutionType.SCHOOL))
                    {
                        ou = CreateOU(schoolsOU.OrgUnitPath, true, name);
                    } else if (institution.Type.Equals(InstitutionType.DAYCARE))
                    {
                        ou = CreateOU(daycaresOU.OrgUnitPath, true, name);
                    } else if (institution.Type.Equals(InstitutionType.MUNICIPALITY))
                    {
                        ou = CreateOU(rootOrgUnitPath, true, name);
                    } else
                    {
                        throw new Exception($"Unknown institution type: {institution.Type.ToString()}. Institution with database id: {institution.DatabaseId} and institution number: {institution.InstitutionNumber}");
                    }

                    institution.GoogleWorkspaceId = ou.OrgUnitId;
                    oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_WORKSPACE_ID, ou.OrgUnitId);
                } else
                {
                    // check if ou should be updated
                    UpdateOU(name, match);
                }
            }
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

        public bool shouldBeExcluded(DBUser user, List<string> excludedRoles)
        {
            if (user.Role.Equals(DBRole.STUDENT))
            {
                if (excludedRoles.Contains(user.StudentRole.ToString()))
                {
                    return true;
                }
            }
            else if (user.Role.Equals(DBRole.EMPLOYEE))
            {
                foreach (EmployeeRole role in user.EmployeeRoles)
                {
                    if (!excludedRoles.Contains(role.ToString()))
                    {
                        return false;
                    }
                }
                return true;
            }
            else if (user.Role.Equals(DBRole.EXTERNAL))
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

        public void DisableInactiveUsers(List<DBUser> usersShouldBeInInstitution, Institution institution, List<string> excludedRoles)
        {
            OrgUnit institutionOrgUnit = GetOrgUnit(institution.GoogleWorkspaceId);
            if (institutionOrgUnit == null)
            {
                throw new Exception("Could not find institution orgunit for intitution with number " + institution.InstitutionNumber);
            }

            // users from institution
            List<Google.Apis.Admin.Directory.directory_v1.Data.User> users = ListUsers(institutionOrgUnit.OrgUnitPath);
            if (users == null)
            {
                throw new Exception("Could not fetch users for intitution with number " + institution.InstitutionNumber);
            }

            // it only works with usernames because we delete before we create/update
            List<string> usernamesInInstitution = users.Select(u => u.PrimaryEmail.Replace("@" + domain, "")).ToList();
            foreach (string username in usernamesInInstitution)
            {
                DBUser user = usersShouldBeInInstitution.Where(u => username.Equals(u.Username)).FirstOrDefault();
                if (user != null)
                {
                    if (shouldBeExcluded(user, excludedRoles))
                    {
                        user.IsExcluded = true;
                        HandleSuspendUser(username);
                    }
                }
                else
                {
                    HandleSuspendUser(username);
                }
            }
        }

        public void CreateUser(DBUser user, Institution institution, List<DBGroup> classes, OrgUnit institutionOrgUnit)
        {
            string ouPath = null;
            if (user.Role.Equals(DBRole.STUDENT))
            {
                OrgUnit emptyGroupsOU = GetStudentsWithoutGroupOrgUnitForInstitution(institutionOrgUnit);
                if (user.StudentMainGroupsGoogleWorkspaceIds == null || user.StudentMainGroupsGoogleWorkspaceIds.Count() == 0)
                {
                    ouPath = emptyGroupsOU.OrgUnitPath;
                }
                else
                {
                    ouPath = emptyGroupsOU.OrgUnitPath;
                    foreach (var current in user.StudentMainGroupsGoogleWorkspaceIds)
                    {
                        var currentMainGroup = GetOrgUnit(current);
                        if (currentMainGroup != null)
                        {
                            ouPath = currentMainGroup.OrgUnitPath;
                            break;
                        }
                    }
                }
            }
            else if (user.Role.Equals(DBRole.EMPLOYEE) || user.Role.Equals(DBRole.EXTERNAL))
            {
                OrgUnit employeeOU = GetEmployeeOrgUnitForInstitution(institutionOrgUnit);
                if (employeeOU != null)
                {
                    ouPath = employeeOU.OrgUnitPath;
                }
            }

            if (ouPath == null)
            {
                throw new Exception("Failed to find ou to create user in.");
            }

            // the user should be under root ou
            if (!ouPath.StartsWith(rootOrgUnitPath))
            {
                throw new Exception("The user " + user.Username + " was not created. It has to be placed under the root ou. Tried to place user in " + ouPath);
            }

            CreateUserInWorkspace(user, ouPath);

            // handle license
            if (user.Role.Equals(DBRole.STUDENT))
            {
                AddStudentLicense(user.Username + "@" + domain);
            } else if (user.Role.Equals(DBRole.EXTERNAL) || user.Role.Equals(DBRole.EMPLOYEE))
            {
                AddStaffLicense(user.Username + "@" + domain);
            } else
            {
                throw new Exception($"Failed to assign license to user with database id: {user.DatabaseId}. Neither student, external or employee");
            }
        }

        public void UpdateAndMoveUser(DBUser user, Google.Apis.Admin.Directory.directory_v1.Data.User match, Institution institution, List<DBGroup> classes, OrgUnit institutionOrgUnit)
        {
            bool changes = false;

            // check for user changes
            if (!match.Name.FullName.Equals(user.Firstname + " " + user.FamilyName))
            {
                changes = true;
            }
            if (match.Suspended.Equals(true))
            {
                changes = true;
            }

            // check for move
            string path = match.OrgUnitPath;
            if (user.Role.Equals(DBRole.STUDENT))
            {
                OrgUnit emptyGroupsOU = GetStudentsWithoutGroupOrgUnitForInstitution(institutionOrgUnit);
                if (user.StudentMainGroupsGoogleWorkspaceIds == null || user.StudentMainGroupsGoogleWorkspaceIds.Count() == 0)
                {
                    path = emptyGroupsOU.OrgUnitPath;
                }
                else
                {
                    List<string> possiblePaths = new List<string>();
                    foreach (var current in user.StudentMainGroupsGoogleWorkspaceIds)
                    {
                        var currentMainGroup = GetOrgUnit(current);
                        if (currentMainGroup == null)
                        {
                            continue;
                        }

                        possiblePaths.Add(currentMainGroup.OrgUnitPath);
                    }

                    // if user is already in one of the possible ous - don't move, else move to first of list
                    if (possiblePaths.Count() == 0 && !match.OrgUnitPath.Equals(emptyGroupsOU.OrgUnitPath))
                    {
                        path = emptyGroupsOU.OrgUnitPath;
                    }
                    else if(possiblePaths.Count() != 0 && !possiblePaths.Contains(match.OrgUnitPath))
                    {
                        path = possiblePaths[0];
                    }
                }
            }
            else if (user.Role.Equals(DBRole.EMPLOYEE) || user.Role.Equals(DBRole.EXTERNAL))
            {
                List<string> possiblePaths = new List<string>();
                foreach (var current in user.Institutions)
                {
                    var currentInstitutionOU = GetOrgUnit(current.GoogleWorkspaceId);
                    if (currentInstitutionOU == null)
                    {
                        continue;
                    }

                    var employeeOU = GetEmployeeOrgUnitForInstitution(currentInstitutionOU);
                    if (employeeOU != null)
                    {
                        possiblePaths.Add(employeeOU.OrgUnitPath);
                    }
                }

                // if user is already in one of the possible ous - don't move, else move to first of list
                if (!possiblePaths.Contains(match.OrgUnitPath))
                {
                    path = possiblePaths[0];
                }
            }

            if (changes || !match.OrgUnitPath.Equals(path))
            {
                UpdateUserInWorkspace(user, path);
                logger.LogInformation($"Updated user with username {user.Username}. Path = {path}");
            }
        }

        public void UpdateGlobalGroups(List<DBUser> users, List<string> groupIds)
        {
            logger.LogInformation($"Handling global groups");
            Group globalEmployeeGroup = UpdateGroup(null, null, SetFieldType.NONE, "alle-ansatte@" + domain, globalEmployeeGroupName, "alle-ansatte@" + domain, null);

            // all employees in institution group
            List<string> usersInEmployee = users.Where(u => (u.Role.Equals(DBRole.EMPLOYEE) || u.Role.Equals(DBRole.EXTERNAL)) && !u.IsExcluded && u.Username != null).Select(u => u.Username).ToList();
            List<Member> membersInEmployeeGroup = ListGroupMembers(globalEmployeeGroup.Email);
            HandleMembersForGroup(globalEmployeeGroup.Email, usersInEmployee, membersInEmployeeGroup);
            groupIds.Add(globalEmployeeGroup.Id);
        }

        public void UpdateGroups(Institution institution, List<DBUser> users, List<DBGroup> classes, List<string> groupIds)
        {
            logger.LogInformation($"Handling groups for institution {institution.InstitutionName}");
            Group institutionEmployeeGroup = UpdateGroup(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_GROUP_WORKSPACE_EMAIL, institution.EmployeeGroupGoogleWorkspaceEmail, GetInstitutionGroupName("EMPLOYEES", institution), GenerateEmailForGroup("staff", institution.InstitutionName), null);

            // handle group drive
            foreach (DBGroup currentClass in classes)
            {
                Group classGroup = UpdateGroup(null, currentClass, SetFieldType.GROUP_GROUP_WORKSPACE_EMAIL, currentClass.GroupGoogleWorkspaceEmail, GetClassGroupName(currentClass, institution), GenerateEmailForGroup(currentClass.GroupName, institution.InstitutionName), null);
                groupIds.Add(classGroup.Id);
            }

            // some kind of timing issue with members i guess. So create groups above and add members here - it needs a bit of time between the actions
            foreach (DBGroup currentClass in classes)
            {
                List<string> usersInClass = users.Where(u => !u.IsExcluded && u.Username != null && (u.GroupIds.Contains("" + currentClass.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + currentClass.DatabaseId)))).Select(u => u.Username).ToList();
                List<Member> membersInClassGroup = ListGroupMembers(currentClass.GroupGoogleWorkspaceEmail);
                HandleMembersForGroup(currentClass.GroupGoogleWorkspaceEmail, usersInClass, membersInClassGroup);
            }

            // all employees in institution group
            List<string> usersInEmployee = users.Where(u => (u.Role.Equals(DBRole.EMPLOYEE) || u.Role.Equals(DBRole.EXTERNAL)) && !u.IsExcluded && u.Username != null).Select(u => u.Username).ToList();
            List<Member> membersInEmployeeGroup = ListGroupMembers(institutionEmployeeGroup.Email);
            HandleMembersForGroup(institutionEmployeeGroup.Email, usersInEmployee, membersInEmployeeGroup);
            groupIds.Add(institutionEmployeeGroup.Id);

            // groups for adults based on role
            HandleInstitutionEmployeeTypeGroups(groupIds, institution, users);

            // groups for students in same year
            List<int> classStartYears = GetClassStartYears(classes);
            foreach (int year in classStartYears)
            {
                Group institutionYearGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, year + ""), GetYearSecurityGroupName(year, institution), GenerateEmailForGroup(year + "", institution.InstitutionName), year + "");
                groupIds.Add(institutionYearGroup.Id);
            }

            // some kind of timing issue with members i guess. So create groups above and add members here - it needs a bit of time between the actions
            foreach (int year in classStartYears)
            {
                List<string> usersInYear = users.Where(u => u.Role.Equals(DBRole.STUDENT) && !u.IsExcluded && u.Username != null && u.StudentMainGroupStartYearForInstitution != 0 && u.StudentMainGroupStartYearForInstitution == year).Select(u => u.Username).ToList();
                List<Member> membersInYear = ListGroupMembers(GetEmail(institution, year + ""));
                HandleMembersForGroup(GetEmail(institution, year + ""), usersInYear, membersInYear);
            }
        }

        private string GetYearSecurityGroupName(int year, Institution institution)
        {
            string name = groupForYearNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{YEAR}", year + "");

            name = name.Replace("æ", "ae");
            name = name.Replace("ø", "oe");
            name = name.Replace("å", "aa");
            name = name.Unidecode();

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private List<int> GetClassStartYears(List<DBGroup> classes)
        {
            List<int> years = new List<int>();
            foreach (DBGroup group in classes)
            {
                if (group.StartYear != 0 && !years.Contains(group.StartYear))
                {
                    years.Add(group.StartYear);
                }
            }
            return years;
        }

        private void HandleInstitutionEmployeeTypeGroups(List<string> groupIds, Institution institution, List<DBUser> users)
        {
            Group institutionEmployeeLærerGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LÆRER"), GetInstitutionEmployeeTypeGroupName("Lærere", institution), GenerateEmailForGroup("laerere", institution.InstitutionName), "LÆRER");
            Group institutionEmployeePædagogGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "PÆDAGOG"), GetInstitutionEmployeeTypeGroupName("Pædagoger", institution), GenerateEmailForGroup("paedagoger", institution.InstitutionName), "PÆDAGOG");
            Group institutionEmployeeVikarGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "VIKAR"), GetInstitutionEmployeeTypeGroupName("Vikarer", institution), GenerateEmailForGroup("vikarer", institution.InstitutionName), "VIKAR");
            Group institutionEmployeeLederGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LEDER"), GetInstitutionEmployeeTypeGroupName("Ledere", institution), GenerateEmailForGroup("ledere", institution.InstitutionName), "LEDER");
            Group institutionEmployeeLedelseGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LEDELSE"), GetInstitutionEmployeeTypeGroupName("Ledelse", institution), GenerateEmailForGroup("ledelse", institution.InstitutionName), "LEDELSE");
            Group institutionEmployeeTapGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "TAP"), GetInstitutionEmployeeTypeGroupName("TAPer", institution), GenerateEmailForGroup("taper", institution.InstitutionName), "TAP");
            Group institutionEmployeeKonsulentGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "KONSULENT"), GetInstitutionEmployeeTypeGroupName("Konsulenter", institution), GenerateEmailForGroup("konsulenter", institution.InstitutionName), "KONSULENT");
            Group institutionEmployeeUnknownGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "UNKNOWN"), GetInstitutionEmployeeTypeGroupName("Ukendte", institution), GenerateEmailForGroup("ukendte", institution.InstitutionName), "UNKNOWN");
            Group institutionEmployeePraktikantGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "PRAKTIKANT"), GetInstitutionEmployeeTypeGroupName("Praktikanter", institution), GenerateEmailForGroup("praktikanter", institution.InstitutionName), "PRAKTIKANT");
            Group institutionEmployeeEksternGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "EKSTERN"), GetInstitutionEmployeeTypeGroupName("Eksterne", institution), GenerateEmailForGroup("eksterne", institution.InstitutionName), "EKSTERN");
            
            groupIds.Add(institutionEmployeeLærerGroup.Id);
            groupIds.Add(institutionEmployeePædagogGroup.Id);
            groupIds.Add(institutionEmployeeVikarGroup.Id);
            groupIds.Add(institutionEmployeeLederGroup.Id);
            groupIds.Add(institutionEmployeeLedelseGroup.Id);
            groupIds.Add(institutionEmployeeTapGroup.Id);
            groupIds.Add(institutionEmployeeKonsulentGroup.Id);
            groupIds.Add(institutionEmployeeUnknownGroup.Id);
            groupIds.Add(institutionEmployeePraktikantGroup.Id);
            groupIds.Add(institutionEmployeeEksternGroup.Id);

            List<string> usersInLærer = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LÆRER)).Select(u => u.Username).ToList();
            List<Member> membersInLærer = ListGroupMembers(institutionEmployeeLærerGroup.Email);
            HandleMembersForGroup(institutionEmployeeLærerGroup.Email, usersInLærer, membersInLærer);

            List<string> usersInPædagog = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.PÆDAGOG)).Select(u => u.Username).ToList();
            List<Member> membersInPædagog = ListGroupMembers(institutionEmployeePædagogGroup.Email);
            HandleMembersForGroup(institutionEmployeePædagogGroup.Email, usersInPædagog, membersInPædagog);

            List<string> usersInVikar = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.VIKAR)).Select(u => u.Username).ToList();
            List<Member> membersInVikar = ListGroupMembers(institutionEmployeeVikarGroup.Email);
            HandleMembersForGroup(institutionEmployeeVikarGroup.Email, usersInVikar, membersInVikar);

            List<string> usersInLeder = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDER)).Select(u => u.Username).ToList();
            List<Member> membersInLeder = ListGroupMembers(institutionEmployeeLederGroup.Email);
            HandleMembersForGroup(institutionEmployeeLederGroup.Email, usersInLeder, membersInLeder);

            List<string> usersInLedelse = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDELSE)).Select(u => u.Username).ToList();
            List<Member> membersInLedelse = ListGroupMembers(institutionEmployeeLedelseGroup.Email);
            HandleMembersForGroup(institutionEmployeeLedelseGroup.Email, usersInLedelse, membersInLedelse);

            List<string> usersInTAP = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.TAP)).Select(u => u.Username).ToList();
            List<Member> membersInTAP = ListGroupMembers(institutionEmployeeTapGroup.Email);
            HandleMembersForGroup(institutionEmployeeTapGroup.Email, usersInTAP, membersInTAP);

            List<string> usersInKonsulent = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.KONSULENT)).Select(u => u.Username).ToList();
            List<Member> membersInKonsulent = ListGroupMembers(institutionEmployeeKonsulentGroup.Email);
            HandleMembersForGroup(institutionEmployeeKonsulentGroup.Email, usersInKonsulent, membersInKonsulent);

            List<string> usersInPraktikant = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.PRAKTIKANT)).Select(u => u.Username).ToList();
            List<Member> membersInPraktikant = ListGroupMembers(institutionEmployeePraktikantGroup.Email);
            HandleMembersForGroup(institutionEmployeePraktikantGroup.Email, usersInPraktikant, membersInPraktikant);

            List<string> usersInEkstern = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.EKSTERN)).Select(u => u.Username).ToList();
            List<Member> membersInEkstern = ListGroupMembers(institutionEmployeeEksternGroup.Email);
            HandleMembersForGroup(institutionEmployeeEksternGroup.Email, usersInEkstern, membersInEkstern);

            List<string> usersInUnknown = users.Where(u => !u.IsExcluded && u.Username != null && ((u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.UNKNOWN)) || (u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.UNKNOWN)))).Select(u => u.Username).ToList();
            List<Member> membersInUnknown = ListGroupMembers(institutionEmployeeUnknownGroup.Email);
            HandleMembersForGroup(institutionEmployeeUnknownGroup.Email, usersInUnknown, membersInUnknown);
        }

        private string GetInstitutionEmployeeTypeGroupName(string type, Institution institution)
        {
            string name = groupForEmployeeTypeNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{TYPE}", type);

            name = name.Replace("æ", "ae");
            name = name.Replace("ø", "oe");
            name = name.Replace("å", "aa");
            name = name.Unidecode();

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetEmail(Institution institution, String key)
        {
            if (institution.GoogleWorkspaceEmailMappings != null)
            {
                if (institution.GoogleWorkspaceEmailMappings.ContainsKey(key))
                {
                    return institution.GoogleWorkspaceEmailMappings[key];
                }
            }
            return null;
        }

        private string GenerateEmailForGroup(string type, string institutionName)
        {
            string namePart = institutionName;
            namePart.Replace("æ", "ae");
            namePart.Replace("ø", "oe");
            namePart.Replace("å", "aa");
            namePart = namePart.Unidecode();
            namePart = namePart.Replace(" ", "-");
            namePart = namePart.Replace("@", "");

            string typePart = type;
            typePart.Replace("æ", "ae");
            typePart.Replace("ø", "oe");
            typePart.Replace("å", "aa");
            typePart = typePart.Unidecode();
            typePart = typePart.Replace(" ", "-");
            typePart = typePart.Replace("@", "");

            return GetYearPart() + "-" + typePart + "-" + namePart + "@" + domain;
        }

        private string GetYearPart()
        {
            DateTime date = DateTime.Now;
            int currentYear = date.Year;
            if (date.Month <= 6) {
                currentYear--;
            }

            string currentYearString = currentYear + "";
            string firstYear = currentYearString.Substring(currentYearString.Length - 2);
            int nextYear = currentYear + 1;
            string nextYearString = nextYear + "";
            string secondYear = nextYearString.Substring(nextYearString.Length - 2);

            return $"{firstYear}/{secondYear}";
        }

        public void UpdateSharedDrives(Institution institution, List<DBUser> users, List<DBGroup> classes, List<string> driveIds)
        {
            logger.LogInformation($"Handling shared drives for institution {institution.InstitutionName}");
            Drive institutionEmployeeDrive = UpdateSharedDrive(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_DRIVE_WORKSPACE_ID, institution.EmployeeDriveGoogleWorkspaceId, GetInstitutionDriveName("EMPLOYEES", institution));
            
            // handle group drive
            foreach (DBGroup currentClass in classes)
            {
                Drive classDrive = UpdateSharedDrive(null, currentClass, SetFieldType.GROUP_DRIVE_WORKSPACE_ID, currentClass.DriveGoogleWorkspaceId, GetClassDriveName(currentClass, institution));
                driveIds.Add(classDrive.Id);
            }

            // some kind of timing issue with permissions i guess. So create drives above and add permissions here - it needs a bit of time between the actions
            foreach (DBGroup currentClass in classes)
            {
                List<Permission> permissionsInClassDrive = ListPermissions(currentClass.DriveGoogleWorkspaceId);
                HandlePersmissionsForDrive(currentClass.DriveGoogleWorkspaceId, permissionsInClassDrive, currentClass.GroupGoogleWorkspaceEmail);
            }

            // all employees in institution drive
            List<Permission> permissionsInEmployeeDrive = ListPermissions(institutionEmployeeDrive.Id);
            HandlePersmissionsForDrive(institutionEmployeeDrive.Id, permissionsInEmployeeDrive, institution.EmployeeGroupGoogleWorkspaceEmail);
            driveIds.Add(institutionEmployeeDrive.Id);
        }

        private void HandleMembersForGroup(string groupEmail, List<string> usernames, List<Member> members)
        {
            logger.LogInformation($"Handling members for shared drive with id {groupEmail}");
            // delete permissions
            foreach (Member member in members)
            {
                if (member.Role.Equals("reader") && member.Type.Equals("user") && member.Email != null)
                {
                    if (!usernames.Contains(member.Email.Replace("@" + domain, "")) && member.Role.Equals("MEMBER"))
                    {
                        RemoveMemberFromGroup(groupEmail, member.Id);
                        logger.LogInformation($"Removed member with username {member.Email.Replace("@" + domain, "")} from group with id {groupEmail}");
                    }
                }
            }

            // create permissions
            foreach (string username in usernames)
            {
                Member matchMember = members.Where(p => p.Email != null && p.Email.Equals(username + "@" + domain)).FirstOrDefault();
                if (matchMember == null)
                {
                    AddMemberToGroup(groupEmail, username + "@" + domain);
                    logger.LogInformation($"Added member with username {username} to group with id {groupEmail}");
                }
            }

            // make sure our impersonated user owns the group. A group can have multiple owners
            Member matchOwner = members.Where(m => m.Email != null && m.Email.Equals(emailAccountToImpersonate) && m.Role.Equals("OWNER")).FirstOrDefault();
            if (matchOwner == null)
            {
                AddOwnerToGroup(groupEmail);
            }
        }

        private void HandlePersmissionsForDrive(string driveId, List<Permission> permissions, String groupEmail)
        {
            logger.LogInformation($"Handling permissions for shared drive with id {driveId}");
            
            // TODO i don't think we want to delete manually added permissions?
            // delete permissions
            /*
            foreach (Permission permission in permissions)
            {
                if (permission.Role.Equals("reader") && permission.Type.Equals("group") && permission.EmailAddress != null)
                {
                    if (!groupEmail.Equals(permission.EmailAddress))
                    {
                        DeletePermission(driveId, permission);
                        logger.LogInformation($"Removed permission with email {permission.EmailAddress} from drive with id {driveId}");
                    }
                }
            }
            */

            // create permission
            Permission matchPermission = permissions.Where(p => p.EmailAddress != null && p.EmailAddress.Equals(groupEmail) && p.Type.Equals("group")).FirstOrDefault();
            if (matchPermission == null)
            {
                CreateDrivePermission(driveId, groupEmail);
                logger.LogInformation($"Added group with email {groupEmail} to drive with id {driveId}");
            }
        }

        private Drive UpdateSharedDrive(Institution institution, DBGroup group, SetFieldType setFieldType, string driveId, string name)
        {
            logger.LogInformation($"Checking if shared drive with name {name} should be updated or created");
            Drive match = GetDrive(driveId);

            if (match == null)
            {
                match = CreateDrive(name);
                logger.LogInformation($"Created shared drive with name {name} and id {match.Id}");

                if (institution != null)
                {
                    oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, setFieldType, match.Id);
                } else if (group != null)
                {
                    oS2skoledataService.SetFields(group.DatabaseId, EntityType.GROUP, setFieldType, match.Id);
                }
                
                if (setFieldType.Equals(SetFieldType.INSTITUTION_STUDENT_DRIVE_WORKSPACE_ID))
                {
                    institution.StudentDriveGoogleWorkspaceId = match.Id;
                } else if (setFieldType.Equals(SetFieldType.INSTITUTION_ALL_DRIVE_WORKSPACE_ID))
                {
                    institution.AllDriveGoogleWorkspaceId = match.Id;
                } else if (setFieldType.Equals(SetFieldType.INSTITUTION_EMPLOYEE_DRIVE_WORKSPACE_ID))
                {
                    institution.EmployeeDriveGoogleWorkspaceId = match.Id;
                } else if (setFieldType.Equals(SetFieldType.GROUP_DRIVE_WORKSPACE_ID))
                {
                    group.DriveGoogleWorkspaceId= match.Id;
                }
            }
            else
            {
                if (!match.Name.Equals(name))
                {
                    UpdateDrive(driveId, name);
                    logger.LogInformation($"Updated shared drive with name {name} and id {match.Id}");
                }
            }

            return match;
        }

        private Group UpdateGroup(Institution institution, DBGroup group, SetFieldType setFieldType, string groupEmail, string name, string generatedEmail, string os2skoledataKey)
        {
            logger.LogInformation($"Checking if group with name {name} should be updated or created");
            Group match = GetGroup(groupEmail);

            if (match == null)
            {
                match = CreateGroup(name, generatedEmail);
                logger.LogInformation($"Created group with name {name} and email {match.Email}");
                
                if (os2skoledataKey != null && institution != null)
                {
                    HandleSetEmailAfterUpdate(institution, os2skoledataKey, match);
                }

                if (!setFieldType.Equals(SetFieldType.NONE))
                {
                    SetFieldsAfterGroupUpdate(institution, group, setFieldType, match);
                }
            }
            else
            {
                bool changes = false;
                bool mailChanges = false;

                if (!match.Name.Equals(name))
                {
                    changes = true;
                }

                if (!match.Email.ToLower().Equals(generatedEmail.ToLower()))
                {
                    changes = true;
                    mailChanges = true;
                }

                if (changes)
                {
                    match = UpdateGroupGoogleWorkspace(name, groupEmail, generatedEmail);
                    logger.LogInformation($"Updated group with name {name} and email {match.Email}");

                    if (mailChanges)
                    {
                        if (os2skoledataKey != null && institution != null)
                        {
                            HandleSetEmailAfterUpdate(institution, os2skoledataKey, match);
                        }

                        if (!setFieldType.Equals(SetFieldType.NONE))
                        {
                            SetFieldsAfterGroupUpdate(institution, group, setFieldType, match);
                        }
                    }
                }
            }

            return match;
        }

        private void HandleSetEmailAfterUpdate(Institution institution, string os2skoledataKey, Group match)
        {
            oS2skoledataService.SetGroupEmail(institution.DatabaseId, os2skoledataKey, match.Email);

            if (institution.GoogleWorkspaceEmailMappings.ContainsKey(os2skoledataKey))
            {
                institution.GoogleWorkspaceEmailMappings[os2skoledataKey] = match.Email;
            }
            else
            {
                institution.GoogleWorkspaceEmailMappings.Add(os2skoledataKey, match.Email);
            }
        }

        private void SetFieldsAfterGroupUpdate(Institution institution, DBGroup group, SetFieldType setFieldType, Group match)
        {
            if (institution != null)
            {
                oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, setFieldType, match.Email);
            }
            else if (group != null)
            {
                oS2skoledataService.SetFields(group.DatabaseId, EntityType.GROUP, setFieldType, match.Email);
            }

            if (setFieldType.Equals(SetFieldType.INSTITUTION_EMPLOYEE_GROUP_WORKSPACE_EMAIL))
            {
                institution.EmployeeGroupGoogleWorkspaceEmail = match.Email;
            }
            else if (setFieldType.Equals(SetFieldType.GROUP_GROUP_WORKSPACE_EMAIL))
            {
                group.GroupGoogleWorkspaceEmail = match.Email;
            }
        }

        public void DeltaSyncCreateGroup(DBGroup group)
        {
            OrgUnit institutionOrgUnit = GetOrgUnit(group.InstitutionGoogleWorkspaceId);
            if (institutionOrgUnit == null)
            {
                throw new Exception("Could not find institution orgunit for intitution with number " + group.InstitutionNumber);
            }

            OrgUnit studentOrgUnit = GetStudentOrgUnitForInstitution(institutionOrgUnit);

            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
            CreateOU(studentOrgUnit.OrgUnitPath, false, name);
        }

        public void DeltaSyncUpdateGroup(DBGroup group, OrgUnit entry)
        {
            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
            UpdateOU(name, entry);
        }

        public void DeltaSyncCreateInstitution(Institution institution)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
            CreateOU(rootOrgUnitPath, true, name);
        }

        public void DeltaSyncUpdateInstitution(Institution institution, OrgUnit entry)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
            UpdateOU(name, entry);
        }


        public void DeleteGroups(List<string> groupIds)
        {
            List<Group> ownedGroups = ListGroups().Where(d => IsGroupOwner(d)).ToList();

            foreach (Group group in ownedGroups)
            {
                if (!groupIds.Contains(group.Id))
                {
                    DeleteGroup(group.Id);
                    logger.LogInformation($"Group with name {group.Name} was deleted");
                }
            }
        }

        private bool IsGroupOwner(Group group)
        {
            List<Member> members = ListGroupMembers(group.Id);
            return members.Any(m => m.Role.Equals("OWNER") && m.Email.Equals(emailAccountToImpersonate));
        }

        public void RenameDrivesToDelete(List<string> driveIds)
        {
            List<Drive> ownedDrives = ListDrives().Where(d => IsOwner(d)).ToList();

            foreach (Drive drive in ownedDrives)
            {
                if (!driveIds.Contains(drive.Id) && !drive.Name.StartsWith(deletedDrivePrefix))
                {
                    UpdateDrive(deletedDrivePrefix + drive.Name, drive.Id);
                    logger.LogInformation($"Drive with name {drive.Name} is no longer managed by OS2skoledata. It has been prefixed with {deletedDrivePrefix}");
                }
            }
        }

        private bool IsOwner(Drive drive)
        {
            List<Permission> permissions = ListPermissions(drive.Id);
            return permissions.Any(p => p.Role.Equals("owner") && p.EmailAddress.Equals(emailAccountToImpersonate));
        }

        private OrgUnit GetSchoolsOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, schoolOUName);
        }

        private OrgUnit GetDaycaresOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, daycareOUName);
        }

        private OrgUnit GetStudentOrgUnitForInstitution(OrgUnit institution)
        {
            if (institutionStudentsOUMapping.ContainsKey(institution.OrgUnitPath))
            {
                return institutionStudentsOUMapping[institution.OrgUnitPath];
            } else
            {
                OrgUnit ou = GetTypeOrgUnit(institution.OrgUnitPath, studentOUName);
                institutionStudentsOUMapping.Add(institution.OrgUnitPath, ou);
                return ou;
            }
        }

        private OrgUnit GetEmployeeOrgUnitForInstitution(OrgUnit institution)
        {
            if (institutionEmployeesOUMapping.ContainsKey(institution.OrgUnitPath))
            {
                return institutionEmployeesOUMapping[institution.OrgUnitPath];
            } else
            {
                OrgUnit ou = GetTypeOrgUnit(institution.OrgUnitPath, employeeOUName);
                institutionEmployeesOUMapping.Add(institution.OrgUnitPath, ou);
                return ou;
            }
        }

        private OrgUnit GetStudentsWithoutGroupOrgUnitForInstitution(OrgUnit institution)
        {
            if (institutionStudentsWithoutGroupOUMapping.ContainsKey(institution.OrgUnitPath))
            {
                return institutionStudentsWithoutGroupOUMapping[institution.OrgUnitPath];
            }
            else
            {
                OrgUnit ou = GetTypeOrgUnit(institution.OrgUnitPath, studentsWithoutGroupsOUName);
                institutionStudentsWithoutGroupOUMapping.Add(institution.OrgUnitPath, ou);
                return ou;
            }
        }

        private OrgUnit GetTypeOrgUnit(string fromPath, string typeNameString)
        {
            List<OrgUnit> children = ListOrgUnits(fromPath, OrgunitsResource.ListRequest.TypeEnum.Children);
            OrgUnit matchOrgUnit = children.Where(c => c.Name.Equals(typeNameString)).FirstOrDefault();
            if (matchOrgUnit == null)
            {
                OrgUnit newOrgUnit = CreateOrgUnit(fromPath, typeNameString);
                return newOrgUnit;
            }
            else
            {
                return matchOrgUnit;
            }
        }

        private OrgUnit CreateOU(string ouPathToCreateIn, bool institutionLevel, string name)
        {
            OrgUnit orgUnit = CreateOrgUnit(ouPathToCreateIn, name);
            logger.LogInformation($"Created OU with path {orgUnit.OrgUnitPath} and id {orgUnit.OrgUnitId}");

            if (institutionLevel)
            {
                OrgUnit studentOU = CreateOrgUnit(orgUnit.OrgUnitPath, studentOUName);
                logger.LogInformation($"Created student OU with path {studentOU.OrgUnitPath}");
                OrgUnit studentWithoutGroupsOU = CreateOrgUnit(orgUnit.OrgUnitPath, studentsWithoutGroupsOUName);
                logger.LogInformation($"Created student-without-groups OU with path {studentWithoutGroupsOU.OrgUnitPath}");
                OrgUnit employeeOU = CreateOrgUnit(orgUnit.OrgUnitPath, employeeOUName);
                logger.LogInformation($"Created employee OU with path {employeeOU.OrgUnitPath}");

                if (oUsToAlwaysCreate != null)
                {
                    foreach (string alwaysCreate in oUsToAlwaysCreate.ToList())
                    {
                        OrgUnit alwaysCreateOU = CreateOrgUnit(orgUnit.OrgUnitPath, alwaysCreate);
                        logger.LogInformation($"Created OU configured to always be created with path {alwaysCreateOU.OrgUnitPath}");
                    }
                }
            }
            return orgUnit;
        }

        private void UpdateOU(string name, OrgUnit match)
        {
            if (!match.Name.Equals(name))
            {   
                OrgUnit editPayload = new OrgUnit();
                editPayload.Name = name;
                UpdateOrgUnitGoogleWorkspace(editPayload, match.OrgUnitId);
                logger.LogInformation($"Updated OU with path {match.OrgUnitPath} and id {match.OrgUnitId}");
            }
        }

        private void DeletePermission(string driveId, Permission permission)
        {
            var permissionRequest = driveService.Permissions.Delete(driveId, permission.Id);
            permissionRequest.UseDomainAdminAccess = true;
            permissionRequest.SupportsAllDrives = true;
            permissionRequest.Execute();
        }

        private Permission CreatePermission(string driveId, string userEmail)
        {
            var permissionRequest = driveService.Permissions.Create(
                new Permission()
                {
                    Type = "user",
                    Role = "reader",
                    EmailAddress = userEmail
                },
                driveId
            );
            permissionRequest.UseDomainAdminAccess = true;
            permissionRequest.SupportsAllDrives = true;

            return permissionRequest.Execute();
        }

        public Permission CreateDrivePermission(string driveId, string groupEmail)
        {
            var permissionRequest = driveService.Permissions.Create(
                new Permission()
                {
                    Type = "group",
                    Role = "reader",
                    EmailAddress = groupEmail
                },
                driveId
            );
            permissionRequest.UseDomainAdminAccess = true;
            permissionRequest.SupportsAllDrives = true;

            return permissionRequest.Execute();
        }

        private List<Permission> ListPermissions(string driveId)
        {
            var permissionRequest = driveService.Permissions.List(driveId);
            permissionRequest.UseDomainAdminAccess = true;
            permissionRequest.Fields = "permissions(id,role,type,emailAddress)";
            permissionRequest.SupportsAllDrives = true;
            return permissionRequest.Execute().Permissions.ToList();
        }

        private void DeleteDrive(string id)
        {
            var request = driveService.Drives.Delete(id);
            request.Execute();
        }

        private Drive UpdateDrive(string name, string id)
        {
            var request = driveService.Drives.Update(new Drive()
            {
                Name = name
            }, id);
            return request.Execute();
        }

        public Drive CreateDrive(string name)
        {
            var requestId = Guid.NewGuid().ToString();
            var request = driveService.Drives.Create(new Drive()
            {
                Name = name
            }, requestId);
            return request.Execute();
        }

        private Drive GetDrive(string id)
        {
            if (id == null)
            {
                return null;
            }

            try
            {
                var req = driveService.Drives.Get(id);
                req.UseDomainAdminAccess = true;
                return req.Execute();
            } catch (Exception ex)
            {
                return null;
            }
        }

        public List<Drive> ListDrives()
        {
            var request = driveService.Drives.List();
            request.UseDomainAdminAccess = true;
            //request.Q = "memberCount < 20";
            var result = request.Execute();
            return result.Drives.ToList();
        }

        public void MoveToDeletedOrgUnits(string id, string path)
        {
            var newOUName = "slettede_ous_" + DateTime.Now.ToString("yyyy_MM_dd");
            List<OrgUnit> orgUnits = ListOrgUnits(deletedOusOu, OrgunitsResource.ListRequest.TypeEnum.Children);
            OrgUnit match = null;
            foreach (OrgUnit ou in orgUnits)
            {
                if (ou.Name.Equals(newOUName))
                {
                    match = ou;
                }
            }

            if (match == null)
            {
                match = CreateOrgUnit(deletedOusOu, newOUName);
                logger.LogInformation("Created deleted ous ou for today: " + deletedOusOu + "/" + newOUName);
            }

            OrgUnit editedOU = new OrgUnit();
            editedOU.ParentOrgUnitPath = match.OrgUnitPath;
            UpdateOrgUnitGoogleWorkspace(editedOU, id);
            logger.LogInformation($"Moved ou with id {id} and path {path} to deleted ous ou for today");
        }

        private OrgUnit UpdateOrgUnitGoogleWorkspace(OrgUnit editedOrgunit, string id)
        {
            // Works like patch. Only set the fields you want to update.
            var updateReq = directoryService.Orgunits.Update(editedOrgunit, "my_customer", id);

            OrgUnit ou = updateReq.Execute();
            ou.OrgUnitId = id;
            if (workspaceIdsOUMapping.ContainsKey(id))
            {
                workspaceIdsOUMapping[id] = ou;
            } else
            {
                workspaceIdsOUMapping.Add(id, ou);
            }

            return ou;
        }

        public OrgUnit GetOrgUnit(string id)
        {
            if (id == null)
            {
                return null;
            }

            if (workspaceIdsOUMapping.ContainsKey(id))
            {
                return workspaceIdsOUMapping[id];
            }

            try
            {
                var getOrgunitReq = directoryService.Orgunits.Get("my_customer", id);

                // throws exception if ou isn't found
                OrgUnit ou = getOrgunitReq.Execute();
                workspaceIdsOUMapping.Add(id, ou);
                return ou;
            } catch (Exception ex)
            {
                return null;
            }
        }

        private OrgUnit CreateOrgUnit(string parentPath, string name)
        {
            var createReq = directoryService.Orgunits.Insert(new OrgUnit
            {
                Name = name,
                ParentOrgUnitPath = parentPath,
            }, "my_customer");

            OrgUnit ou = createReq.Execute();
            workspaceIdsOUMapping.Add(ou.OrgUnitId, ou);
            return ou;
        }

        public List<OrgUnit> ListOrgUnits(string fromPath, OrgunitsResource.ListRequest.TypeEnum type)
        {
            var listReq = directoryService.Orgunits.List("my_customer");
            listReq.Type = type;
            listReq.OrgUnitPath = fromPath;
            OrgUnits allOUs = listReq.Execute();
            if (allOUs != null && allOUs.OrganizationUnits != null)
            {
                return allOUs.OrganizationUnits.ToList();
            }
            return new List<OrgUnit>();
        }

        public Google.Apis.Admin.Directory.directory_v1.Data.User GetUser(string username)
        {
            try
            {
                var getUserReq = directoryService.Users.Get(username + "@" + domain);
                // kaster exception hvis bruger ikke findes
                return getUserReq.Execute();
            } catch (Exception e)
            {
                return null;
            }
        }

        private List<Google.Apis.Admin.Directory.directory_v1.Data.User> ListUsers(string fromPath)
        {
            var listReq = directoryService.Users.List();
            listReq.Domain = domain;
            //listReq.ViewType = UsersResource.ListRequest.ViewTypeEnum.DomainPublic;
            
            // for some reason this does not work anymore, so we are doing the filtering afterwards
            /*
            if (fromPath != null)
            {
                listReq.Query = $"orgUnitPath = '{fromPath}'";
            }*/

            Users allUsers = listReq.Execute();

            if (allUsers != null && allUsers.UsersValue != null)
            {
                if (fromPath != null)
                {
                    return allUsers.UsersValue.Where(u => u.OrgUnitPath.Equals(fromPath)).ToList();
                }
                else
                {
                    return allUsers.UsersValue.ToList();
                }
            }
            return new List<Google.Apis.Admin.Directory.directory_v1.Data.User>();
        }

        public Google.Apis.Admin.Directory.directory_v1.Data.User CreateUserInWorkspace(DBUser user, string path)
        {
            // password has to be set, so we use a random uuid
            var insertRequest = directoryService.Users.Insert(new Google.Apis.Admin.Directory.directory_v1.Data.User
            {
                PrimaryEmail = user.Username + "@" + domain,
                Name = new UserName
                {
                    GivenName = user.Firstname,
                    FamilyName = user.FamilyName,
                    FullName = user.Firstname + " " + user.FamilyName
                },
                Password = Guid.NewGuid().ToString(),
                OrgUnitPath = path
            }
            );
            return insertRequest.Execute();
        }

        private Google.Apis.Admin.Directory.directory_v1.Data.User UpdateUserInWorkspace(DBUser dbUser, string path)
        {
            // Works like patch. Only set the fields you want to update.
            var updateUserReq = directoryService.Users.Update(new Google.Apis.Admin.Directory.directory_v1.Data.User
            {
                Name = new UserName
                {
                    GivenName = dbUser.Firstname,
                    FamilyName = dbUser.FamilyName,
                    FullName = dbUser.Firstname + " " + dbUser.FamilyName
                },
                Suspended = false,
                OrgUnitPath = path

            }, dbUser.Username + "@" + domain);

            // kaster exception hvis bruger ikke findes
            Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
            return user;
        }

        public void HandleSuspendUser(string username)
        {
            string email = username + "@" + domain;
            if (HasStudentLicense(email))
            {
                RemoveStudentLicense(email);
            }

            if (HasStaffLicense(email))
            {
                RemoveStaffLicense(email);
            }

            SuspendUser(username);
        }

        private void SuspendUser(string username)
        {
            OrgUnit suspendedUsersOUToday = GetSuspendedUsersOUToday();
            var updateUserReq = directoryService.Users.Update(new Google.Apis.Admin.Directory.directory_v1.Data.User
            {
                Suspended = true,
                SuspensionReason = "ADMIN",
                OrgUnitPath = suspendedUsersOUToday.OrgUnitPath
            }, username + "@" + domain);

            // kaster exception hvis bruger ikke findes
            Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
            logger.LogInformation($"Disabled user with username {username}");
        }

        private OrgUnit GetSuspendedUsersOUToday()
        {
            var newOUName = "deaktiverede_brugere_" + DateTime.Now.ToString("yyyy_MM_dd");
            List<OrgUnit> orgUnits = ListOrgUnits(suspendedUsersOU, OrgunitsResource.ListRequest.TypeEnum.Children);
            OrgUnit match = null;
            foreach (OrgUnit ou in orgUnits)
            {
                if (ou.Name.Equals(newOUName))
                {
                    match = ou;
                }
            }

            if (match == null)
            {
                match = CreateOrgUnit(suspendedUsersOU, newOUName);
                logger.LogInformation("Created deleted ous ou for today: " + suspendedUsersOU + "/" + newOUName);
            }

            return match;
        }

        private Group UpdateGroupGoogleWorkspace(string name, string email, string generatedMail)
        {
            var updateReq = directoryService.Groups.Patch(new Group
            {
                Name = name,
                Email = generatedMail
            }, email);

            Group group = updateReq.Execute();

            return group;
        }

        public Group GetGroup(string email)
        {
            if (email == null)
            {
                return null;
            }

            try
            {
                var getGroupReq = directoryService.Groups.Get(email);

                // throws exception if ou isn't found
                Group group = getGroupReq.Execute();
                return group;
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public Group CreateGroup(string name, string email)
        {
            var createReq = directoryService.Groups.Insert(new Group
            {
                Email = email,
                Name = name
            });

            Group group = createReq.Execute();
            return group;
        }

        public void DeleteGroup(string groupId)
        {
            directoryService.Groups.Delete(groupId).Execute();
        }

        public List<Group> ListGroups()
        {
            var listReq = directoryService.Groups.List();
            listReq.Customer = "my_customer";
            Groups allGroups = listReq.Execute();
            if (allGroups != null && allGroups.GroupsValue != null)
            {
                return allGroups.GroupsValue.ToList();
            }
            return new List<Group>();
        }

        public Member AddMemberToGroup(string groupId, string userId)
        {
            var insertMemberReq = directoryService.Members.Insert(new Member
            {
                Email = userId
            }, groupId);

            Member member = insertMemberReq.Execute();

            return member;
        }

        public Member AddOwnerToGroup(string groupId)
        {
            var insertMemberReq = directoryService.Members.Insert(new Member
            {
                Email = emailAccountToImpersonate,
                Role = "OWNER"
            }, groupId);

            Member member = insertMemberReq.Execute();

            return member;
        }

        public void RemoveMemberFromGroup(string groupEmail, string userId)
        {
            var deleteMemberReq = directoryService.Members.Delete(groupEmail, userId);
            deleteMemberReq.Execute();
        }

        public List<Member> ListGroupMembers(string groupId)
        {
            var listMemberReq = directoryService.Members.List(groupId);

            var result = listMemberReq.Execute();

            if (result != null && result.MembersValue != null)
            {
                return result.MembersValue.ToList();
            }

            return new List<Member>();
        }

        public LicenseAssignment AddStudentLicense(String userEmail)
        {
            logger.LogInformation($"Trying to assign student license to user with email: {userEmail}");
            var req = licenseService.LicenseAssignments.Insert(new LicenseAssignmentInsert
            {
                UserId = userEmail
            }, studentLicenseProductId, studentLicenseSkuId);

            var result = req.Execute();
            logger.LogInformation($"Assigned student license to user with email: {userEmail}");
            return result;
        }

        public LicenseAssignment AddStaffLicense(String userEmail)
        {
            logger.LogInformation($"Trying to assign staff license to user with email: {userEmail}");
            var req = licenseService.LicenseAssignments.Insert(new LicenseAssignmentInsert
            {
                UserId = userEmail
            }, staffLicenseProductId, staffLicenseSkuId);

            var result = req.Execute();
            logger.LogInformation($"Assigned staff license to user with email: {userEmail}");
            return result;
        }

        public void RemoveStudentLicense(String userEmail)
        {
            licenseService.LicenseAssignments.Delete(studentLicenseProductId, studentLicenseSkuId, userEmail).Execute();
        }

        public void RemoveStaffLicense(String userEmail)
        {
            licenseService.LicenseAssignments.Delete(staffLicenseProductId, staffLicenseSkuId, userEmail).Execute();
        }

        public bool HasStudentLicense(String userEmail)
        {
            return HasLicense(userEmail, studentLicenseProductId, studentLicenseSkuId) != null;
        }

        public bool HasStaffLicense(String userEmail)
        {
            return HasLicense(userEmail, studentLicenseProductId, studentLicenseSkuId) != null;
        }

        public LicenseAssignment HasLicense(string userEmail, string productId, string skuId)
        {
            try
            {
                // throws exception if not found
                LicenseAssignment assignment = licenseService.LicenseAssignments.Get(productId, skuId, userEmail).Execute();
                return assignment;
            }
            catch (Exception ex)
            {
                return null;
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

            calculatedName = EscapeCharacters(calculatedName);

            if (calculatedName.Length > 64)
            {
                calculatedName = calculatedName.Substring(0, 64);
            }

            return calculatedName;
        }

        private string GetInstitutionDriveName(string type, Institution institution)
        {
            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                default:
                    throw new Exception("Unknown institution drive name standard type: " + type);
            }

            name = EscapeCharacters(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetInstitutionGroupName(string type, Institution institution)
        {
            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                default:
                    throw new Exception("Unknown institution group name standard type: " + type);
            }

            name = EscapeCharacters(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetClassDriveName(DBGroup currentClass, Institution institution)
        {
            string name = classDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{CLASS_NAME}", currentClass.GroupName.Unidecode())
                        .Replace("{CLASS_ID}", currentClass.GroupId)
                        .Replace("{CLASS_LEVEL}", currentClass.GroupLevel);

            if (currentClass.StartYear != 0)
            {
                name = name.Replace("{CLASS_YEAR}", currentClass.GroupLevel);
            }


            name = EscapeCharacters(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetClassGroupName(DBGroup currentClass, Institution institution)
        {
            string name = classGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName.Unidecode())
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{CLASS_NAME}", currentClass.GroupName.Unidecode())
                        .Replace("{CLASS_ID}", currentClass.GroupId)
                        .Replace("{CLASS_LEVEL}", currentClass.GroupLevel);

            if (currentClass.StartYear != 0)
            {
                name = name.Replace("{CLASS_YEAR}", currentClass.GroupLevel);
            }

            name = EscapeCharacters(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string EscapeCharacters(string name)
        {
            name = name.Replace("+", "\\+");
            name = name.Replace(",", "\\,");
            name = name.Replace("\"", "\\\"");
            name = name.Replace("<", "\\<");
            name = name.Replace(">", "\\>");
            name = name.Replace(";", "\\;");
            name = name.Replace("&", "\\&");
            name = name.Replace("#", "\\#");

            // \/ virker ikke, så kan ikke se en anden mulighed end helt at udlade /
            // TODO er der en anden mulighed?
            name = name.Replace("/", " ");
            //name = name.Replace("/", "\\\\/");
            return name;
        }

    }
}
