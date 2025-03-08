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
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace.SettingsModel;
using os2skoledata_google_workspace_sync.Services.OS2skoledata;
using os2skoledata_google_workspace_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Xml.Linq;
using Unidecode.NET;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace
{
    internal class WorkspaceService : ServiceBase<WorkspaceService>
    {
        private readonly OS2skoledataService oS2skoledataService;
        private readonly string emailAccountToImpersonate;
        private readonly string domain;
        private readonly string rootOrgUnitPath;
        private readonly string keepAliveOUPath;
        private readonly HierarchyType hierarchyType;
        private readonly DirectoryService directoryService;
        private readonly DriveService driveService;
        private readonly LicensingService licenseService;
        private readonly string[] oUsToAlwaysCreate;
        private readonly string employeeOUName;
        private readonly string studentOUName;
        private readonly string studentsWithoutGroupsOUName;
        private readonly string classOUNameStandard;
        private readonly string classOUNameStandardNoClassYear;
        private readonly string institutionOUNameStandard;
        private readonly string globalEmployeeDriveName;
        private readonly string allInInstitutionDriveNameStandard;
        private readonly string allStudentsInInstitutionDriveNameStandard;
        private readonly string allEmployeesInInstitutionDriveNameStandard;
        private readonly string classDriveNameStandard;
        private readonly string classDriveNameStandardNoClassYear;
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
        public readonly string classGroupNameStandardNoClassYear;
        public readonly string classGroupOnlyStudentsNameStandard;
        public readonly string classGroupOnlyStudentsNameStandardNoClassYear;
        public readonly string globalEmployeeGroupName;
        private readonly string schoolOUName;
        private readonly string daycareOUName;
        private readonly string staffLicenseProductId;
        private readonly string staffLicenseSkuId;
        private readonly string studentLicenseProductId;
        private readonly string studentLicenseSkuId;
        private readonly bool userDryRun;
        private readonly string[] rolesToBeCreatedDirectlyInGW;
        private readonly UsernameStandardType usernameStandard;
        private readonly string usernamePrefix;
        private readonly int randomStandardLetterCount;
        private readonly int randomStandardNumberCount;
        private readonly string institutitonStaffGroupEmailTypeName;
        private readonly bool useDanishCharacters;
        private readonly bool deleteDisabledUsersFully;
        private readonly int daysBeforeDeletionStudent;
        private readonly int daysBeforeDeletionEmployee;
        private readonly int daysBeforeDeletionExternal;
        private readonly bool gwTraceLog;
        private readonly bool setContactCard;

        private readonly string KEYS_OS2SKOLEDATA = "OS2skoledata";
        private readonly string KEYS_ROLE = "ROLE";
        private readonly string KEYS_DELETED_DATE = "DELETED_DATE";

        private char[] first;
        private char[] second;
        private char[] third;
        private Dictionary<long, string> uniqueIds;
        private Random random;

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
            keepAliveOUPath = settings.WorkspaceSettings.KeepAliveOU;
            oUsToAlwaysCreate = settings.WorkspaceSettings.OUsToAlwaysCreate;
            hierarchyType = settings.WorkspaceSettings.HierarchyType;
            employeeOUName = settings.WorkspaceSettings.NamingSettings.EmployeeOUName;
            studentOUName = settings.WorkspaceSettings.NamingSettings.StudentOUName;
            studentsWithoutGroupsOUName = settings.WorkspaceSettings.NamingSettings.StudentsWithoutGroupsOUName;
            classOUNameStandard = settings.WorkspaceSettings.NamingSettings.ClassOUNameStandard;
            classOUNameStandardNoClassYear = settings.WorkspaceSettings.NamingSettings.ClassOUNameStandardNoClassYear;
            institutionOUNameStandard = settings.WorkspaceSettings.NamingSettings.InstitutionOUNameStandard;
            globalEmployeeDriveName = settings.WorkspaceSettings.NamingSettings.GlobalEmployeeDriveName;
            allInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllInInstitutionDriveNameStandard;
            allStudentsInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllStudentsInInstitutionDriveNameStandard;
            allEmployeesInInstitutionDriveNameStandard = settings.WorkspaceSettings.NamingSettings.AllEmployeesInInstitutionDriveNameStandard;
            classDriveNameStandard = settings.WorkspaceSettings.NamingSettings.ClassDriveNameStandard;
            classDriveNameStandardNoClassYear = settings.WorkspaceSettings.NamingSettings.ClassDriveNameStandardNoClassYear;
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
            classGroupNameStandardNoClassYear = settings.WorkspaceSettings.NamingSettings.ClassGroupNameStandardNoClassYear;
            classGroupOnlyStudentsNameStandard = settings.WorkspaceSettings.NamingSettings.ClassGroupOnlyStudentsNameStandard;
            classGroupOnlyStudentsNameStandardNoClassYear = settings.WorkspaceSettings.NamingSettings.ClassGroupOnlyStudentsNameStandardNoClassYear;
            globalEmployeeGroupName = settings.WorkspaceSettings.NamingSettings.GlobalEmployeeGroupName;
            schoolOUName = settings.WorkspaceSettings.NamingSettings.SchoolOUName;
            daycareOUName = settings.WorkspaceSettings.NamingSettings.DaycareOUName;
            staffLicenseProductId = settings.WorkspaceSettings.LicensingSettings.StaffLicenseProductId;
            staffLicenseSkuId = settings.WorkspaceSettings.LicensingSettings.StaffLicenseSkuId;
            studentLicenseProductId = settings.WorkspaceSettings.LicensingSettings.StudentLicenseProductId;
            studentLicenseSkuId = settings.WorkspaceSettings.LicensingSettings.StudentLicenseSkuId;
            userDryRun = settings.WorkspaceSettings.UserDryRun;
            rolesToBeCreatedDirectlyInGW = settings.WorkspaceSettings.RolesToBeCreatedDirectlyInGW;
            usernameStandard = settings.WorkspaceSettings.usernameSettings == null ? UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN : settings.WorkspaceSettings.usernameSettings.UsernameStandard;
            usernamePrefix = settings.WorkspaceSettings.usernameSettings == null ? null : settings.WorkspaceSettings.usernameSettings.UsernamePrefix;
            randomStandardLetterCount = settings.WorkspaceSettings.usernameSettings == null ? 4 : settings.WorkspaceSettings.usernameSettings.RandomStandardLetterCount;
            randomStandardNumberCount = settings.WorkspaceSettings.usernameSettings == null ? 4 : settings.WorkspaceSettings.usernameSettings.RandomStandardNumberCount;
            institutitonStaffGroupEmailTypeName = settings.WorkspaceSettings.NamingSettings.InstitutitonStaffGroupEmailTypeName;
            useDanishCharacters = settings.WorkspaceSettings.UseDanishCharacters;
            deleteDisabledUsersFully = settings.WorkspaceSettings.DeleteDisabledUsersFully;
            daysBeforeDeletionStudent = settings.WorkspaceSettings.DaysBeforeDeletionStudent;
            daysBeforeDeletionEmployee = settings.WorkspaceSettings.DaysBeforeDeletionEmployee;
            daysBeforeDeletionExternal = settings.WorkspaceSettings.DaysBeforeDeletionExternal;
            gwTraceLog = settings.WorkspaceSettings.GWTraceLog;
            setContactCard = settings.WorkspaceSettings.SetContactCard;

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
                        DirectoryService.Scope.AdminDirectoryUserschema,
                        DriveService.Scope.Drive,
                        LicensingService.Scope.AppsLicensing
                    },
                   KeyId = serviceAccountCredentialDTO.KeyId,
                   User = emailAccountToImpersonate
               }.FromPrivateKey(serviceAccountCredentialDTO.PrivateKey));

            directoryService = new DirectoryService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Directory API",
            });

            driveService = new DriveService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Drive API",
            });

            licenseService = new LicensingService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credentialWithScopes,
                ApplicationName = "Licensing API",
            });

            first = "23456789abcdefghjkmnpqrstuvxyz".ToCharArray();
            second = "abcdefghjkmnpqrstuvxyz".ToCharArray();
            third = "23456789".ToCharArray();
            uniqueIds = new Dictionary<long, string>();
            random = new Random();

            PopulateTable();
        }

        public void InitializeDictionaries()
        {
            institutionStudentsOUMapping = new Dictionary<string, OrgUnit>();
            institutionStudentsWithoutGroupOUMapping = new Dictionary<string, OrgUnit>();
            institutionEmployeesOUMapping = new Dictionary<string, OrgUnit>();
            workspaceIdsOUMapping = new Dictionary<string, OrgUnit>();
        }

        public void deleteUsers()
        {
            if (deleteDisabledUsersFully && !userDryRun)
            {
                DateTime studentDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionStudent);
                DateTime employeeDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionEmployee);
                DateTime externalDaysAgo = DateTime.Now.AddDays(-daysBeforeDeletionExternal);

                // fetch disabled users
                List<Google.Apis.Admin.Directory.directory_v1.Data.User> users = ListUsers(suspendedUsersOU);
                foreach (Google.Apis.Admin.Directory.directory_v1.Data.User user in users)
                {
                    if ((bool)user.Suspended)
                    {
                        // if no note, we don't know deletion date, so we won't delete fully
                        IDictionary<string, object> os2skoledataSchema = GetOS2skoledataSchemaFromUser(user, true);
                        if (os2skoledataSchema == null)
                        {
                            continue;
                        }

                        // get deletion date and role, if null we don't know when to delete, so skip
                        string role = (string)GetOS2skoledataSchemaValue(KEYS_ROLE, os2skoledataSchema);
                        if (role == null)
                        {
                            continue;
                        }

                        bool shouldDelete = false;
                        string deletedDateString = (string)GetOS2skoledataSchemaValue(KEYS_DELETED_DATE, os2skoledataSchema);
                        if (DateTime.TryParse(deletedDateString, out DateTime parsedDateTime))
                        {
                            // check if date qualifies for deletion
                            if (DBRole.STUDENT.ToString().Equals(role))
                            {
                                if (parsedDateTime < studentDaysAgo)
                                {
                                    shouldDelete = true;
                                }
                            }
                            else if (DBRole.EMPLOYEE.ToString().Equals(role))
                            {
                                if (parsedDateTime < employeeDaysAgo)
                                {
                                    shouldDelete = true;
                                }
                            }
                            else if (DBRole.EXTERNAL.ToString().Equals(role))
                            {
                                if (parsedDateTime < externalDaysAgo)
                                {
                                    shouldDelete = true;
                                }
                            }
                        }

                        if (shouldDelete)
                        {
                            DeleteUser(user.PrimaryEmail);
                        }
                    }
                }
            }
        }

        public void UpdateClassesForInstitution(List<DBGroup> classes, Institution institution, OrgUnit institutionOrgUnit)
        {
            logger.LogInformation($"Handling classes for institution {institution.InstitutionName}");
            OrgUnit studentOrgUnit = GetStudentOrgUnitForInstitution(institutionOrgUnit, null, institution);

            // get children of root
            List<OrgUnit> children = ListOrgUnits(studentOrgUnit.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);

            // delete 
            foreach (OrgUnit ou in children)
            {
                if (!classes.Any(c => Object.Equals(c.GoogleWorkspaceId, ou.OrgUnitId)) && !Object.Equals(ou.Name, studentsWithoutGroupsOUName))
                {
                    MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath);
                }
            }

            // sort classes based on level (highest first) to make sure potential renaming is smooth
            classes = SortGroupsBasedOnLevel(classes);
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
                    // check if class ou has been deleted previously and should be moved back
                    if (!match.ParentOrgUnitPath.Equals(studentOrgUnit.OrgUnitPath))
                    {
                        OrgUnit editedOU = new OrgUnit();
                        editedOU.ParentOrgUnitPath = studentOrgUnit.OrgUnitPath;
                        match = UpdateOrgUnitGoogleWorkspace(editedOU, match.OrgUnitId);
                        logger.LogInformation($"Moved ou with id {match.OrgUnitId} to path {match.OrgUnitPath}");
                    }

                    // check if ou should be updated
                    UpdateOU(name, match);
                }
            }
        }

        private List<DBGroup> SortGroupsBasedOnLevel(List<DBGroup> groups)
        {
            return groups.OrderByDescending(group => ConvertToInt(group.GroupLevel)).ToList();
        }

        private int ConvertToInt(string level)
        {
            int result;
            return int.TryParse(level, out result) ? result : int.MinValue;
        }

        public void UpdateInstitutions(List<Institution> institutions)
        {
            logger.LogInformation("Handling institutions");
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                OrgUnit schoolsOU = GetSchoolsOrgUnit(rootOrgUnitPath);
                OrgUnit daycaresOU = GetDaycaresOrgUnit(rootOrgUnitPath);

                // get children of root
                List<OrgUnit> children = ListOrgUnits(rootOrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);
                children.AddRange(ListOrgUnits(schoolsOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children));
                children.AddRange(ListOrgUnits(daycaresOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children));

                // delete 
                foreach (OrgUnit ou in children)
                {
                    if (Object.Equals(ou.OrgUnitPath, deletedOusOu) || ou.OrgUnitPath.Equals(suspendedUsersOU) || ou.OrgUnitPath.Equals(schoolsOU.OrgUnitPath) || ou.OrgUnitPath.Equals(daycaresOU.OrgUnitPath))
                    {
                        continue;
                    }

                    if (!institutions.Any(i => i.GoogleWorkspaceId != null && Object.Equals(i.GoogleWorkspaceId, ou.OrgUnitId) || i.Locked))
                    {
                        MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath);
                    }
                }

                // create or update
                foreach (Institution institution in institutions)
                {
                    if (institution.Locked)
                    {
                        logger.LogInformation($"NOT handling institution with number {institution.InstitutionNumber} and name  {institution.InstitutionName}. It is locked due to school year change.");
                        continue;
                    }

                    logger.LogInformation($"Handling institution with number {institution.InstitutionNumber} and name  {institution.InstitutionName}");
                    string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
                    OrgUnit match = GetOrgUnit(institution.GoogleWorkspaceId);

                    string pathToCreateIn = rootOrgUnitPath;
                    if (institution.Type.Equals(InstitutionType.SCHOOL))
                    {
                        pathToCreateIn = schoolsOU.OrgUnitPath;
                    }
                    else if (institution.Type.Equals(InstitutionType.DAYCARE))
                    {
                        pathToCreateIn = daycaresOU.OrgUnitPath;
                    }

                    if (match == null)
                    {
                        // create
                        OrgUnit ou = CreateOU(pathToCreateIn, true, name);

                        institution.GoogleWorkspaceId = ou.OrgUnitId;
                        oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_WORKSPACE_ID, ou.OrgUnitId);
                    }
                    else
                    {
                        // check if institution ou has been deleted previously and should be moved back
                        if (!match.ParentOrgUnitPath.Equals(pathToCreateIn))
                        {
                            OrgUnit editedOU = new OrgUnit();
                            editedOU.ParentOrgUnitPath = pathToCreateIn;
                            match = UpdateOrgUnitGoogleWorkspace(editedOU, match.OrgUnitId);
                            logger.LogInformation($"Moved ou with id {match.OrgUnitId} to path {match.OrgUnitPath}");
                        }

                        // check if ou should be updated
                        UpdateOU(name, match);
                    }
                }
            } 
            else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                OrgUnit employeeOU = GetEmployeeOrgUnit(rootOrgUnitPath);
                OrgUnit studentOU = GetStudentOrgUnit(rootOrgUnitPath);

                // get children of root
                List<OrgUnit> childrenStudents = ListOrgUnits(studentOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);
                List<OrgUnit> childrenEmployees = ListOrgUnits(employeeOU.OrgUnitPath, OrgunitsResource.ListRequest.TypeEnum.Children);

                // delete student 
                foreach (OrgUnit ou in childrenStudents)
                {
                    if (Object.Equals(ou.OrgUnitPath, deletedOusOu) || ou.OrgUnitPath.Equals(suspendedUsersOU) || ou.OrgUnitPath.Equals(employeeOU.OrgUnitPath) || ou.OrgUnitPath.Equals(studentOU.OrgUnitPath))
                    {
                        continue;
                    }

                    if (!institutions.Any(i => 
                                              (i.StudentInstitutionGoogleWorkspaceId != null && Object.Equals(i.StudentInstitutionGoogleWorkspaceId, ou.OrgUnitId)) || i.Locked))
                    {
                        MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath, "Elever_", ou);
                    }
                }

                // delete employee 
                foreach (OrgUnit ou in childrenEmployees)
                {
                    if (Object.Equals(ou.OrgUnitPath, deletedOusOu) || ou.OrgUnitPath.Equals(suspendedUsersOU) || ou.OrgUnitPath.Equals(employeeOU.OrgUnitPath) || ou.OrgUnitPath.Equals(studentOU.OrgUnitPath))
                    {
                        continue;
                    }

                    if (!institutions.Any(i => (i.EmployeeInstitutionGoogleWorkspaceId != null && Object.Equals(i.EmployeeInstitutionGoogleWorkspaceId, ou.OrgUnitId)) || i.Locked))
                    {
                        MoveToDeletedOrgUnits(ou.OrgUnitId, ou.OrgUnitPath, "Medarbejdere_", ou);
                    }
                }

                // create or update
                foreach (Institution institution in institutions)
                {
                    if (institution.Locked)
                    {
                        logger.LogInformation($"NOT handling institution with number {institution.InstitutionNumber} and name  {institution.InstitutionName}. It is locked due to school year change.");
                        continue;
                    }

                    logger.LogInformation($"Handling institution with number {institution.InstitutionNumber} and name  {institution.InstitutionName}");
                    string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
                    OrgUnit matchInstitutionStudent = GetOrgUnit(institution.StudentInstitutionGoogleWorkspaceId);
                    OrgUnit matchInstitutionEmployee = GetOrgUnit(institution.EmployeeInstitutionGoogleWorkspaceId);

                    if (matchInstitutionStudent == null)
                    {
                        // create
                        OrgUnit institutionStudentOU = CreateOU(studentOU.OrgUnitPath, false, name);

                        institution.StudentInstitutionGoogleWorkspaceId = institutionStudentOU.OrgUnitId;
                        oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_STUDENT_WORKSPACE_ID, institutionStudentOU.OrgUnitId);
                    }
                    else
                    {
                        // check if ou should be updated
                        UpdateOU(name, matchInstitutionStudent);
                    }

                    if (matchInstitutionEmployee == null)
                    {
                        // create
                        OrgUnit institutionEmployeeOU = CreateOU(employeeOU.OrgUnitPath, false, name);

                        institution.EmployeeInstitutionGoogleWorkspaceId = institutionEmployeeOU.OrgUnitId;
                        oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_EMPLOYEE_WORKSPACE_ID, institutionEmployeeOU.OrgUnitId);
                    }
                    else
                    {
                        // check if ou should be updated
                        UpdateOU(name, matchInstitutionEmployee);
                    }
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
            if (user.GlobalRole.Equals(DBRole.STUDENT))
            {
                if (excludedRoles.Contains(user.GlobalStudentRole.ToString()))
                {
                    return true;
                }
            }
            else if (user.GlobalRole.Equals(DBRole.EMPLOYEE))
            {
                foreach (EmployeeRole role in user.GlobalEmployeeRoles)
                {
                    if (!excludedRoles.Contains(role.ToString()))
                    {
                        return false;
                    }
                }
                return true;
            }
            else if (user.GlobalRole.Equals(DBRole.EXTERNAL))
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

        public void DisableInactiveUsersFromRoot(List<DBUser> allActiveUsers, List<string> lockedUsernames, List<string> keepAliveUsernames)
        {
            // fetch OS2skoledata users from GW from both root, disabledUsersOU and deleted ous OU
            List<Google.Apis.Admin.Directory.directory_v1.Data.User> users = new List<Google.Apis.Admin.Directory.directory_v1.Data.User>();
            users.AddRange(ListUsers(rootOrgUnitPath));
            if (!suspendedUsersOU.Contains(rootOrgUnitPath))
            {
                users.AddRange(ListUsers(suspendedUsersOU));
            }
            if (!deletedOusOu.Contains(rootOrgUnitPath))
            {
                users.AddRange(ListUsers(deletedOusOu));
            }
            if (!string.IsNullOrEmpty(keepAliveOUPath) && !keepAliveOUPath.Contains(keepAliveOUPath))
            {
                users.AddRange(ListUsers(keepAliveOUPath));
            }


            foreach (Google.Apis.Admin.Directory.directory_v1.Data.User gwUser in users)
            {
                string username = gwUser.PrimaryEmail.Replace("@" + domain, "");

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

                bool exists = allActiveUsers.Where(u => Object.Equals(username, u.Username)).Any();
                if (!exists)
                {
                    
                    HandleSuspendUser(username, gwUser);
                }
            }
        }

        public Google.Apis.Admin.Directory.directory_v1.Data.User CreateUser(DBUser user, Institution institution, List<DBGroup> classes, OrgUnit institutionOrgUnit, OrgUnit studentInstitutionOrgUnit, OrgUnit employeeInstitutionOrgUnit)
        {
            if (userDryRun)
            {
                logger.LogInformation($"UserDryRun: would have created user with databaseId {user.DatabaseId} and {user.Username} in Google Workspace");
                return null;
            }
            else
            {
                string ouPath = null;
                if (user.Role.Equals(DBRole.STUDENT))
                {
                    OrgUnit emptyGroupsOU = GetStudentsWithoutGroupOrgUnitForInstitution(institutionOrgUnit, studentInstitutionOrgUnit);
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
                    OrgUnit employeeOU = GetEmployeeOrgUnitForInstitution(institutionOrgUnit, employeeInstitutionOrgUnit, institution);
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

                Google.Apis.Admin.Directory.directory_v1.Data.User createdUser = CreateUserInWorkspace(user, ouPath);

                // register as reactivated in OS2skoledata
                oS2skoledataService.SetActionOnUser(user.Username, ActionType.CREATE);

                // handle license
                if (user.Role.Equals(DBRole.STUDENT))
                {
                    if (String.IsNullOrEmpty(studentLicenseProductId) || String.IsNullOrEmpty(studentLicenseSkuId))
                    {
                        logger.LogInformation($"Would have added student license to user with username {user.Username} but licensing settings is not configured");
                    }
                    else
                    {
                        AddStudentLicense(user.Username + "@" + domain);
                    }
                }
                else if (user.Role.Equals(DBRole.EXTERNAL) || user.Role.Equals(DBRole.EMPLOYEE))
                {
                    if (String.IsNullOrEmpty(staffLicenseProductId) || String.IsNullOrEmpty(staffLicenseSkuId))
                    {
                        logger.LogInformation($"Would have added staff license to user with username {user.Username} but licensing settings is not configured");
                    } else
                    {
                        AddStaffLicense(user.Username + "@" + domain);
                    }
                }
                else
                {
                    throw new Exception($"Failed to assign license to user with database id: {user.DatabaseId}. Neither student, external or employee");
                }

                return createdUser;
            }
        }

        public void UpdateAndMoveUser(DBUser user, Google.Apis.Admin.Directory.directory_v1.Data.User match, Institution institution, List<DBGroup> classes, OrgUnit institutionOrgUnit, OrgUnit studentInstitutionOrgUnit, OrgUnit employeeInstitutionOrgUnit, List<string> keepAliveUsernames)
        {
            bool changes = false;
            bool reactivated = false;

            // check for user changes
            if (!Object.Equals(match.Name.FullName, user.Firstname + " " + user.FamilyName))
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have updated name on workspace user with username {user.Username} and db id {user.DatabaseId} to {user.Firstname + " " + user.FamilyName}");
                } else
                {
                    logger.LogInformation($"Attempting to change name on workspace user with username {user.Username} and db id {user.DatabaseId} to {user.Firstname + " " + user.FamilyName}");
                }
                changes = true;
            }
            if (match.Suspended.Equals(true))
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have reactivated workspace user with username {user.Username} and db id {user.DatabaseId}");
                } else
                {
                    logger.LogInformation($"Attempting to reactivate workspace user with username {user.Username} and db id {user.DatabaseId}");
                }
                changes = true;
                reactivated = true;
            }

            // check for contact card changes 
            if (setContactCard)
            {
                HashSet<string> currentDepartments = new HashSet<string>();
                if (match.Organizations != null)
                {
                    currentDepartments = match.Organizations.Select(o => o.Department).ToHashSet();
                }

                HashSet<string> contactCardInstitutionNames = new HashSet<string>();
                if (user.ContactCards != null)
                {
                    contactCardInstitutionNames = user.ContactCards.Select(o => o.InstitutionName).ToHashSet();
                }

                bool areEqual = currentDepartments.SetEquals(contactCardInstitutionNames);
                if (!areEqual)
                {
                    changes = true;
                }
            }

            object roleNoteObject = GetOS2skoledataSchemaValue(KEYS_ROLE, GetOS2skoledataSchemaFromUser(match));
            string roleNote = roleNoteObject == null ? null : (string) roleNoteObject;
            if (!Object.Equals(roleNote, user.GlobalRole.ToString()))
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have updated OS2skoledata.role on workspace user with username {user.Username} and db id {user.DatabaseId} from {roleNote} to {user.GlobalRole.ToString()}");
                }
                else
                {
                    logger.LogInformation($"Attempting to change OS2skoledata.role on workspace user with username {user.Username} and db id {user.DatabaseId} from {roleNote} to {user.GlobalRole.ToString()}");
                    
                    // this one is saved directly. So do not set changes = true
                    UpdateOS2skoledataSchemaValue(KEYS_ROLE, user.GlobalRole.ToString(), match, true);
                }
            }

            // check for move
            string path = match.OrgUnitPath;
            if (keepAliveUsernames != null && keepAliveUsernames.Contains(user.Username) && !string.IsNullOrEmpty(keepAliveOUPath) && keepAliveOUPath.Equals(path))
            {
                // do not move
            }
            if (user.Role.Equals(DBRole.STUDENT))
            {
                OrgUnit emptyGroupsOU = GetStudentsWithoutGroupOrgUnitForInstitution(institutionOrgUnit, studentInstitutionOrgUnit);
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
                    if (possiblePaths.Count() == 0 && !Object.Equals(match.OrgUnitPath, emptyGroupsOU.OrgUnitPath))
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
                foreach (Institution current in user.Institutions)
                {
                    if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
                    {
                        var currentInstitutionOU = GetOrgUnit(current.GoogleWorkspaceId);
                        if (currentInstitutionOU == null)
                        {
                            continue;
                        }

                        var employeeOU = GetEmployeeOrgUnitForInstitution(currentInstitutionOU, null, current);
                        if (employeeOU != null)
                        {
                            possiblePaths.Add(employeeOU.OrgUnitPath);
                        }
                    } 
                    else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
                    {
                        var employeeOU = GetEmployeeOrgUnitForInstitution(null, null, current);
                        if (employeeOU != null)
                        {
                            possiblePaths.Add(employeeOU.OrgUnitPath);
                        }
                    }
                }

                // if user is already in one of the possible ous - don't move, else move to first of list
                if (!possiblePaths.Contains(match.OrgUnitPath))
                {
                    path = possiblePaths[0];
                }
            }

            if (!Object.Equals(match.OrgUnitPath, path))
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have updated ou path on user with username {user.Username} and db id {user.DatabaseId} in Google Workspace from {match.OrgUnitPath} to {path}");
                } else
                {
                    logger.LogInformation($"Attempting to update ou path on user with username {user.Username} and db id {user.DatabaseId} in Google Workspace from {match.OrgUnitPath} to {path}");
                }
            }

            if (changes || !Object.Equals(match.OrgUnitPath, path))
            {
                if (!userDryRun)
                {
                    UpdateUserInWorkspace(user, path);
                    logger.LogInformation($"Updated user with username {user.Username}. Path = {path}");

                    if (reactivated)
                    {
                        // register as reactivated in OS2skoledata
                        oS2skoledataService.SetActionOnUser(user.Username, ActionType.REACTIVATE);
                    }
                }
            }
        }

        public void UpdateGlobalGroups(List<DBUser> users, List<string> lockedUsernames)
        {
            logger.LogInformation($"Handling global groups");
            Group globalEmployeeGroup = UpdateGroup(null, null, SetFieldType.NONE, "alle-ansatte@" + domain, globalEmployeeGroupName, "alle-ansatte@" + domain, null, false, null);

            // all employees in institution group
            if (globalEmployeeGroup != null)
            {
                List<string> usersInEmployee = users.Where(u => (u.Role.Equals(DBRole.EMPLOYEE) || u.Role.Equals(DBRole.EXTERNAL)) && !u.IsExcluded && u.Username != null).Select(u => u.Username).ToList();
                List<Member> membersInEmployeeGroup = ListGroupMembers(globalEmployeeGroup.Email);
                HandleMembersForGroup(globalEmployeeGroup.Email, usersInEmployee, membersInEmployeeGroup, lockedUsernames);
            }
        }

        public void UpdateGroups(Institution institution, List<DBUser> users, List<DBGroup> classes, List<Group> allOurGWGroups)
        {
            logger.LogInformation($"Handling groups for institution {institution.InstitutionName}");
            bool hasRandomNumberEmployee = false;
            Group institutionEmployeeGroup = UpdateGroup(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_GROUP_WORKSPACE_EMAIL, institution.EmployeeGroupGoogleWorkspaceEmail, GetInstitutionGroupName("EMPLOYEES", institution), GenerateEmailForGroup(institutitonStaffGroupEmailTypeName, institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberEmployee), null, hasRandomNumberEmployee, allOurGWGroups);

            Boolean createClassGroups = true;
            Boolean createClassOnlyStudentsGroups = true;

            // handle group groups
            // sort classes based on level (highest first) to make sure potential renaming is smooth
            classes = SortGroupsBasedOnLevel(classes);
            foreach (DBGroup currentClass in classes)
            {
                bool hasRandomNumberClass = false;
                Group classGroup = UpdateGroup(null, currentClass, SetFieldType.GROUP_GROUP_WORKSPACE_EMAIL, currentClass.GroupGoogleWorkspaceEmail, GetClassGroupName(currentClass, institution, false), GenerateEmailForGroup(currentClass.GroupName, institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberClass), null, hasRandomNumberClass, allOurGWGroups);
                if (classGroup == null)
                {
                    createClassGroups = false;
                }

                bool hasRandomNumberStudents = false;
                Group classGroupOnlyStudents = UpdateGroup(null, currentClass, SetFieldType.GROUP_ONLY_STUDENTS_GROUP_WORKSPACE_EMAIL, currentClass.GroupOnlyStudentsGoogleWorkspaceEmail, GetClassGroupName(currentClass, institution, true), GenerateEmailForGroup(currentClass.GroupName + "-elever", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberStudents), null, hasRandomNumberStudents, allOurGWGroups);
                if (classGroupOnlyStudents == null)
                {
                    createClassOnlyStudentsGroups = false;
                }
            }

            // some kind of timing issue with members i guess. So create groups above and add members here - it needs a bit of time between the actions
            foreach (DBGroup currentClass in classes)
            {
                if (createClassGroups)
                {
                    List<string> usersInClass = users.Where(u => !u.IsExcluded && u.Username != null && (u.GroupIds.Contains("" + currentClass.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + currentClass.DatabaseId)))).Select(u => u.Username).ToList();
                    List<Member> membersInClassGroup = ListGroupMembers(currentClass.GroupGoogleWorkspaceEmail);
                    HandleMembersForGroup(currentClass.GroupGoogleWorkspaceEmail, usersInClass, membersInClassGroup, null);
                }
                if (createClassOnlyStudentsGroups)
                {
                    List<string> studentsInClass = users.Where(u => u.Role.Equals(DBRole.STUDENT) && !u.IsExcluded && u.Username != null && (u.GroupIds.Contains("" + currentClass.DatabaseId) || (u.StudentMainGroups != null && u.StudentMainGroups.Contains("" + currentClass.DatabaseId)))).Select(u => u.Username).ToList();
                    List<Member> membersInClassGroup = ListGroupMembers(currentClass.GroupOnlyStudentsGoogleWorkspaceEmail);
                    HandleMembersForGroup(currentClass.GroupOnlyStudentsGoogleWorkspaceEmail, studentsInClass, membersInClassGroup, null);
                }
            }

            // all employees in institution group
            if (institutionEmployeeGroup != null)
            {
                List<string> usersInEmployee = users.Where(u => (u.Role.Equals(DBRole.EMPLOYEE) || u.Role.Equals(DBRole.EXTERNAL)) && !u.IsExcluded && u.Username != null).Select(u => u.Username).ToList();
                List<Member> membersInEmployeeGroup = ListGroupMembers(institutionEmployeeGroup.Email);
                HandleMembersForGroup(institutionEmployeeGroup.Email, usersInEmployee, membersInEmployeeGroup, null);
            }

            // groups for adults based on role
            HandleInstitutionEmployeeTypeGroups(institution, users, allOurGWGroups);

            Boolean createYearGroups = true;

            // groups for students in same year
            List<int> classStartYears = GetClassStartYears(classes);
            foreach (int year in classStartYears)
            {
                bool hasRandomNumberYear = false;
                Group institutionYearGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, year + ""), GetYearSecurityGroupName(year, institution), GenerateEmailForGroup(year + "", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberYear), year + "", hasRandomNumberYear, allOurGWGroups);
                if (institutionYearGroup == null)
                {
                    createYearGroups = false;
                    break;
                }
            }

            // some kind of timing issue with members i guess. So create groups above and add members here - it needs a bit of time between the actions
            foreach (int year in classStartYears)
            {
                if (createYearGroups)
                {
                    List<string> usersInYear = users.Where(u => u.Role.Equals(DBRole.STUDENT) && !u.IsExcluded && u.Username != null && u.StudentMainGroupStartYearForInstitution != 0 && u.StudentMainGroupStartYearForInstitution == year).Select(u => u.Username).ToList();
                    List<Member> membersInYear = ListGroupMembers(GetEmail(institution, year + ""));
                    HandleMembersForGroup(GetEmail(institution, year + ""), usersInYear, membersInYear, null);
                }
            }
        }

        private string GetYearSecurityGroupName(int year, Institution institution)
        {
            string name = groupForYearNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{YEAR}", year + "");

            if (!useDanishCharacters)
            {
                name = name.Replace("æ", "ae");
                name = name.Replace("ø", "oe");
                name = name.Replace("å", "aa");
                name = name.Unidecode();
            }

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        public List<int> GetClassStartYears(List<DBGroup> classes)
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

        private void HandleInstitutionEmployeeTypeGroups(Institution institution, List<DBUser> users, List<Group> allOurGWGroups)
        {
            // if one has an empty name, they all will - in that case skip
            if (String.IsNullOrEmpty(GetInstitutionEmployeeTypeGroupName("Lærere", institution)))
            {
                return;
            }

            bool hasRandomNumberLærer = false;
            bool hasRandomNumberPædagog = false;
            bool hasRandomNumberVikar = false;
            bool hasRandomNumberLeder = false;
            bool hasRandomNumberLedelse = false;
            bool hasRandomNumberTap = false;
            bool hasRandomNumberKonsulent = false;
            bool hasRandomNumberUnknown = false;
            bool hasRandomNumberPraktikant = false;
            bool hasRandomNumberEkstern = false;

            Group institutionEmployeeLærerGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LÆRER"), GetInstitutionEmployeeTypeGroupName("Lærere", institution), GenerateEmailForGroup("laerere", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberLærer), "LÆRER", hasRandomNumberLærer, allOurGWGroups);
            Group institutionEmployeePædagogGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "PÆDAGOG"), GetInstitutionEmployeeTypeGroupName("Pædagoger", institution), GenerateEmailForGroup("paedagoger", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberPædagog), "PÆDAGOG", hasRandomNumberPædagog, allOurGWGroups);
            Group institutionEmployeeVikarGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "VIKAR"), GetInstitutionEmployeeTypeGroupName("Vikarer", institution), GenerateEmailForGroup("vikarer", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberVikar), "VIKAR", hasRandomNumberVikar, allOurGWGroups);
            Group institutionEmployeeLederGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LEDER"), GetInstitutionEmployeeTypeGroupName("Ledere", institution), GenerateEmailForGroup("ledere", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberLeder), "LEDER", hasRandomNumberLeder, allOurGWGroups);
            Group institutionEmployeeLedelseGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "LEDELSE"), GetInstitutionEmployeeTypeGroupName("Ledelse", institution), GenerateEmailForGroup("ledelse", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberLedelse), "LEDELSE", hasRandomNumberLedelse, allOurGWGroups);
            Group institutionEmployeeTapGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "TAP"), GetInstitutionEmployeeTypeGroupName("TAPer", institution), GenerateEmailForGroup("taper", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberTap), "TAP", hasRandomNumberTap, allOurGWGroups);
            Group institutionEmployeeKonsulentGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "KONSULENT"), GetInstitutionEmployeeTypeGroupName("Konsulenter", institution), GenerateEmailForGroup("konsulenter", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberKonsulent), "KONSULENT", hasRandomNumberKonsulent, allOurGWGroups);
            Group institutionEmployeeUnknownGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "UNKNOWN"), GetInstitutionEmployeeTypeGroupName("Ukendte", institution), GenerateEmailForGroup("ukendte", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberUnknown), "UNKNOWN", hasRandomNumberUnknown, allOurGWGroups);
            Group institutionEmployeePraktikantGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "PRAKTIKANT"), GetInstitutionEmployeeTypeGroupName("Praktikanter", institution), GenerateEmailForGroup("praktikanter", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberPraktikant), "PRAKTIKANT", hasRandomNumberPraktikant, allOurGWGroups);
            Group institutionEmployeeEksternGroup = UpdateGroup(institution, null, SetFieldType.NONE, GetEmail(institution, "EKSTERN"), GetInstitutionEmployeeTypeGroupName("Eksterne", institution), GenerateEmailForGroup("eksterne", institution.InstitutionName, institution.CurrentSchoolYear, ref hasRandomNumberEkstern), "EKSTERN", hasRandomNumberEkstern, allOurGWGroups);

            List<string> usersInLærer = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LÆRER)).Select(u => u.Username).ToList();
            List<Member> membersInLærer = ListGroupMembers(institutionEmployeeLærerGroup.Email);
            HandleMembersForGroup(institutionEmployeeLærerGroup.Email, usersInLærer, membersInLærer, null);

            List<string> usersInPædagog = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.PÆDAGOG)).Select(u => u.Username).ToList();
            List<Member> membersInPædagog = ListGroupMembers(institutionEmployeePædagogGroup.Email);
            HandleMembersForGroup(institutionEmployeePædagogGroup.Email, usersInPædagog, membersInPædagog, null);

            List<string> usersInVikar = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.VIKAR)).Select(u => u.Username).ToList();
            List<Member> membersInVikar = ListGroupMembers(institutionEmployeeVikarGroup.Email);
            HandleMembersForGroup(institutionEmployeeVikarGroup.Email, usersInVikar, membersInVikar, null);

            List<string> usersInLeder = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDER)).Select(u => u.Username).ToList();
            List<Member> membersInLeder = ListGroupMembers(institutionEmployeeLederGroup.Email);
            HandleMembersForGroup(institutionEmployeeLederGroup.Email, usersInLeder, membersInLeder, null);

            List<string> usersInLedelse = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.LEDELSE)).Select(u => u.Username).ToList();
            List<Member> membersInLedelse = ListGroupMembers(institutionEmployeeLedelseGroup.Email);
            HandleMembersForGroup(institutionEmployeeLedelseGroup.Email, usersInLedelse, membersInLedelse, null);

            List<string> usersInTAP = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.TAP)).Select(u => u.Username).ToList();
            List<Member> membersInTAP = ListGroupMembers(institutionEmployeeTapGroup.Email);
            HandleMembersForGroup(institutionEmployeeTapGroup.Email, usersInTAP, membersInTAP, null);

            List<string> usersInKonsulent = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.KONSULENT)).Select(u => u.Username).ToList();
            List<Member> membersInKonsulent = ListGroupMembers(institutionEmployeeKonsulentGroup.Email);
            HandleMembersForGroup(institutionEmployeeKonsulentGroup.Email, usersInKonsulent, membersInKonsulent, null);

            List<string> usersInPraktikant = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.PRAKTIKANT)).Select(u => u.Username).ToList();
            List<Member> membersInPraktikant = ListGroupMembers(institutionEmployeePraktikantGroup.Email);
            HandleMembersForGroup(institutionEmployeePraktikantGroup.Email, usersInPraktikant, membersInPraktikant, null);

            List<string> usersInEkstern = users.Where(u => !u.IsExcluded && u.Username != null && u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.EKSTERN)).Select(u => u.Username).ToList();
            List<Member> membersInEkstern = ListGroupMembers(institutionEmployeeEksternGroup.Email);
            HandleMembersForGroup(institutionEmployeeEksternGroup.Email, usersInEkstern, membersInEkstern, null);

            List<string> usersInUnknown = users.Where(u => !u.IsExcluded && u.Username != null && ((u.Role.Equals(DBRole.EXTERNAL) && u.ExternalRole.Equals(ExternalRole.UNKNOWN)) || (u.Role.Equals(DBRole.EMPLOYEE) && u.EmployeeRoles.Contains(EmployeeRole.UNKNOWN)))).Select(u => u.Username).ToList();
            List<Member> membersInUnknown = ListGroupMembers(institutionEmployeeUnknownGroup.Email);
            HandleMembersForGroup(institutionEmployeeUnknownGroup.Email, usersInUnknown, membersInUnknown, null);
        }

        private string GetInstitutionEmployeeTypeGroupName(string type, Institution institution)
        {
            string name = groupForEmployeeTypeNameStandard
                        .Replace("{INSTITUTION_NAME}", institution.InstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{TYPE}", type);

            if (!useDanishCharacters)
            {
                name = name.Replace("æ", "ae");
                name = name.Replace("ø", "oe");
                name = name.Replace("å", "aa");
                name = name.Unidecode();
            }

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        public string GetEmail(Institution institution, String key)
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

        private string GenerateEmailForGroup(string type, string institutionName, string institutionYear, ref bool hasRandomNumber)
        {
            string namePart = EscapeCharactersForGroupEmailHard(institutionName);
            string typePart = EscapeCharactersForGroupEmailHard(type);

            string emailPart = institutionYear + "-" + typePart + "-" + namePart;

            // if mail is too long shorten it to 55 chars and append 8 random numbers at the end to ensure it is uniqe
            if (emailPart.Length > 64)
            {
                emailPart = emailPart.Substring(0, 55);

                Random random = new Random();

                for (int i = 0; i < 8; i++)
                {
                    int randomNumber = random.Next(0, 10);
                    emailPart = emailPart + randomNumber;
                }

                hasRandomNumber = true;
            }

            return emailPart + "@" + domain;
        }

        private string EscapeCharactersForGroupEmailHard(string name)
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
            name = name.Replace("*", "_");
            name = name.Replace(".", "");
            name = name.Replace("@", "at");
            name = name.Replace(" ", "-");
            name = name.Replace("§", "ss");

            // weird characters
            name = name.Replace("ë", "e");
            name = name.Replace("é", "e");
            name = name.Replace("ü", "u");
            name = name.Replace("ú", "u");
            name = name.Replace("ä", "a");
            name = name.Replace("á", "a");

            // danish characters
            name = name.Replace("ø", "oe");
            name = name.Replace("å", "aa");
            name = name.Replace("æ", "ae");
            name = name.Replace("Ø", "Oe");
            name = name.Replace("Å", "Aa");
            name = name.Replace("Æ", "Ae");

            return name;
        }

        public void UpdateSharedDrives(Institution institution, List<DBUser> users, List<DBGroup> classes)
        {
            logger.LogInformation($"Handling shared drives for institution {institution.InstitutionName}");
            Drive institutionEmployeeDrive = UpdateSharedDrive(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_DRIVE_WORKSPACE_ID, institution.EmployeeDriveGoogleWorkspaceId, GetInstitutionDriveName("EMPLOYEES", institution));

            // handle group drives - sort classes based on level (highest first) to make sure potential renaming is smooth
            classes = SortGroupsBasedOnLevel(classes);
            foreach (DBGroup currentClass in classes)
            {
                Drive classDrive = UpdateSharedDrive(null, currentClass, SetFieldType.GROUP_DRIVE_WORKSPACE_ID, currentClass.DriveGoogleWorkspaceId, GetClassDriveName(currentClass, institution));
            }

            // some kind of timing issue with permissions i guess. So create drives above and add permissions here - it needs a bit of time between the actions
            foreach (DBGroup currentClass in classes)
            {
                List<string> employees = users.Where(u => u.Role.Equals(DBRole.EMPLOYEE) && !u.IsExcluded && u.Username != null && (u.GroupIds.Contains("" + currentClass.DatabaseId))).Select(u => u.Username).ToList();
                List<Permission> permissionsInClassDrive = ListPermissions(currentClass.DriveGoogleWorkspaceId);
                HandlePersmissionsForDrive(currentClass.DriveGoogleWorkspaceId, permissionsInClassDrive, currentClass.GroupGoogleWorkspaceEmail, "writer", employees, "fileOrganizer");
            }

            // all employees in institution drive
            List<Permission> permissionsInEmployeeDrive = ListPermissions(institutionEmployeeDrive.Id);
            HandlePersmissionsForDrive(institutionEmployeeDrive.Id, permissionsInEmployeeDrive, institution.EmployeeGroupGoogleWorkspaceEmail, "fileOrganizer", null, null);
        }

        private void HandleMembersForGroup(string groupEmail, List<string> usernames, List<Member> members, List<string> lockedUsernames)
        {
            logger.LogInformation($"Handling members for group with email {groupEmail}");
            // delete permissions
            foreach (Member member in members)
            {
                // only remove members with role member - not MANAGER OR OWNER
                if (member.Role.ToUpper().Equals("MEMBER") && member.Type.ToUpper().Equals("USER") && member.Email != null && !member.Email.Equals(emailAccountToImpersonate))
                {
                    var username = member.Email.Replace("@" + domain, "");
                    
                    // if the user is in a locked institution, don't remove the user
                    if (lockedUsernames != null && lockedUsernames.Contains(username))
                    {
                        continue;
                    }

                    if (!usernames.Contains(username))
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have removed member with username {username} from group with id {groupEmail}");
                        } else
                        {
                            logger.LogInformation($"Trying to remove member with username {username} from group with id {groupEmail}");
                            RemoveMemberFromGroup(groupEmail, member.Id);
                            logger.LogInformation($"Removed member with username {username} from group with id {groupEmail}");
                        }
                    }
                }
            }

            // create permissions
            List<string> addedUsernames = new List<string>();
            foreach (string username in usernames)
            {
                if (!addedUsernames.Contains(username))
                {
                    Member matchMember = members.Where(p => p.Email != null && Object.Equals(p.Email, username.ToLower() + "@" + domain)).FirstOrDefault();
                    if (matchMember == null)
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have added member with username {username} to group with id {groupEmail}");
                        }
                        else
                        {
                            logger.LogInformation($"Trying to add member with username {username} to group with id {groupEmail}");
                            AddMemberToGroup(groupEmail, username + "@" + domain);
                            addedUsernames.Add(username);
                            logger.LogInformation($"Added member with username {username} to group with id {groupEmail}");
                        }
                    }
                }
            }

            // make sure our impersonated user owns the group. A group can have multiple owners
            Member matchOwner = members.Where(m => m.Email != null && Object.Equals(m.Email, emailAccountToImpersonate) && m.Role.Equals("OWNER")).FirstOrDefault();
            if (matchOwner == null)
            {
                logger.LogInformation($"Trying to set group owner to our impersonated user for group with id {groupEmail}");
                AddOwnerToGroup(groupEmail);
                logger.LogInformation($"Sat group owner to our impersonated user for group with id {groupEmail}");
            }
        }

        private void HandlePersmissionsForDrive(string driveId, List<Permission> permissions, String groupEmail, string role, List<string> employeeUsernames, string employeeRole)
        {
            logger.LogInformation($"Handling permissions for shared drive with id {driveId}");

            // delete permissions
            foreach (Permission permission in permissions)
            {
                if (permission.Role.Equals(role) && permission.Type.Equals("group") && permission.EmailAddress != null)
                {
                    if (!groupEmail.Equals(permission.EmailAddress) && !permission.EmailAddress.Equals(emailAccountToImpersonate))
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have removed member with email {permission.EmailAddress} from drive with id {driveId}");
                        }
                        else
                        {
                            DeletePermission(driveId, permission);
                            logger.LogInformation($"Removed permission with email {permission.EmailAddress} from drive with id {driveId}");
                        }
                    }
                } else if (employeeUsernames != null && permission.Role.Equals(employeeRole) && permission.Type.Equals("user") && permission.EmailAddress != null)
                {
                    if (!employeeUsernames.Where(e => permission.EmailAddress.StartsWith(e)).Any() && !permission.EmailAddress.Equals(emailAccountToImpersonate))
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have removed member with email {permission.EmailAddress} from drive with id {driveId}");
                        }
                        else
                        {
                            DeletePermission(driveId, permission);
                            logger.LogInformation($"Removed permission with email {permission.EmailAddress} from drive with id {driveId}");
                        }
                    }
                }
            }

            // make sure our user is owner - also if userDryRun
            Permission ownerPermission = permissions.Where(p => p.EmailAddress != null && p.EmailAddress.Equals(emailAccountToImpersonate) && Object.Equals(p.Type, "user")).FirstOrDefault();
            if (ownerPermission == null)
            {
                CreateDrivePermission(driveId, emailAccountToImpersonate, "organizer", "user");
                logger.LogInformation($"Added organizer with email {emailAccountToImpersonate} to drive with id {driveId}");
            }
            else if (ownerPermission != null && !ownerPermission.Role.Equals("organizer"))
            {
                EditDrivePermission(ownerPermission.Id, driveId, emailAccountToImpersonate, "organizer", "user");
                logger.LogInformation($"Edited permission for organizer with email {emailAccountToImpersonate} as organizer of drive with id {driveId}");
            }

            // create permissions
            Permission matchPermission = permissions.Where(p => p.EmailAddress != null && p.EmailAddress.Equals(groupEmail) && Object.Equals(p.Type, "group")).FirstOrDefault();
            if (matchPermission == null)
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have added group with email {groupEmail} as member to drive with id {driveId}");
                } else
                {
                    CreateDrivePermission(driveId, groupEmail, role, "group");
                    logger.LogInformation($"Added group with email {groupEmail} to drive with id {driveId}");
                }
            } else if (matchPermission != null && !matchPermission.Role.Equals(role))
            {
                if (userDryRun)
                {
                    logger.LogInformation($"UserDryRun: would have edited permission for group with email {groupEmail} as member to drive with id {driveId}");
                }
                else
                {
                    EditDrivePermission(matchPermission.Id, driveId, groupEmail, role, "group");
                    logger.LogInformation($"Edited permission for group with email {groupEmail} as member to drive with id {driveId}");
                }
            }

            if (employeeUsernames != null)
            {
                foreach (var employee in employeeUsernames)
                {
                    string employeeEmail = employee + "@" + domain;
                    Permission matchEmployeePermission = permissions.Where(p => p.EmailAddress != null && p.EmailAddress.Equals(employeeEmail) && Object.Equals(p.Type, "user")).FirstOrDefault();
                    if (matchEmployeePermission == null)
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have added employee with email {employeeEmail} as member to drive with id {driveId}");
                        }
                        else
                        {
                            CreateDrivePermission(driveId, employeeEmail, employeeRole, "user");
                            logger.LogInformation($"Added employee with email {employeeEmail} to drive with id {driveId}");
                        }
                    }
                    else if (matchEmployeePermission != null && !matchEmployeePermission.Role.Equals(employeeRole))
                    {
                        if (userDryRun)
                        {
                            logger.LogInformation($"UserDryRun: would have edited permission for employee with email {employeeEmail} as member to drive with id {driveId}");
                        }
                        else
                        {
                            EditDrivePermission(matchEmployeePermission.Id, driveId, employeeEmail, employeeRole, "user");
                            logger.LogInformation($"Edited permission for employee with email {employeeEmail} as member to drive with id {driveId}");
                        }
                    }
                }
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
                if (!Object.Equals(match.Name, name))
                {
                    UpdateDrive(name, driveId);
                    logger.LogInformation($"Updated shared drive with name {name} and id {match.Id}");
                }
            }

            return match;
        }

        private Group UpdateGroup(Institution institution, DBGroup group, SetFieldType setFieldType, string groupEmail, string name, string generatedEmail, string os2skoledataKey, bool hasRandomNumber, List<Group> allOurGWGroups)
        {
            if (String.IsNullOrEmpty(name))
            {
                return null;
            }

            logger.LogInformation($"Checking if group with name {name} and email {groupEmail} should be updated or created");
            Group match = GetGroup(groupEmail, allOurGWGroups);

            if (match == null)
            {
                logger.LogInformation($"Attempting to create group with {name} and email {generatedEmail}");
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

                if (!Object.Equals(match.Name, name))
                {
                    changes = true;
                    logger.LogInformation($"Updating name on group with name {match.Name} to {name}");
                }

                if (hasRandomNumber)
                {
                    string matchEmailPart = match.Email.Substring(0, 55);
                    string generatedEmailPart = generatedEmail.Substring(0, 55);
                    if (!matchEmailPart.ToLower().Equals(generatedEmailPart.ToLower()))
                    {
                        changes = true;
                        mailChanges = true;
                        logger.LogInformation($"Updating email on group with name {match.Name} from {match.Email} to {generatedEmail}");
                    }
                } 
                else
                {
                    if (!match.Email.ToLower().Equals(generatedEmail.ToLower()))
                    {
                        changes = true;
                        mailChanges = true;
                        logger.LogInformation($"Updating email on group with name {match.Name} from {match.Email} to {generatedEmail}");
                    }
                }
                
                if (changes)
                {
                    logger.LogInformation($"Attempting to update group with name {match.Name}");
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
            else if (setFieldType.Equals(SetFieldType.GROUP_ONLY_STUDENTS_GROUP_WORKSPACE_EMAIL))
            {
                group.GroupOnlyStudentsGoogleWorkspaceEmail = match.Email;
            }
        }

        public void DeltaSyncCreateGroup(DBGroup group)
        {
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                OrgUnit institutionOrgUnit = GetOrgUnit(group.InstitutionGoogleWorkspaceId);
                if (institutionOrgUnit == null)
                {
                    throw new Exception("Could not find institution orgunit for intitution with number " + group.InstitutionNumber);
                }

                OrgUnit studentOrgUnit = GetStudentOrgUnitForInstitution(institutionOrgUnit, group, null);

                string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
                CreateOU(studentOrgUnit.OrgUnitPath, false, name);
            }
            else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                OrgUnit studentInstitutionOrgUnit = GetOrgUnit(group.StudentInstitutionGoogleWorkspaceId);
                if (studentInstitutionOrgUnit == null)
                {
                    throw new Exception("Could not find student institution orgunit for intitution with number " + group.InstitutionNumber);
                }

                // users from institution
                string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
                CreateOU(studentInstitutionOrgUnit.OrgUnitPath, false, name);
            }
        }

        public void DeltaSyncUpdateGroup(DBGroup group, OrgUnit entry)
        {
            string name = GetNameForOU(false, group.InstitutionName, group.InstitutionNumber, group.GroupName, group.GroupId, group.GroupLevel, group.StartYear);
            UpdateOU(name, entry);
        }

        public void DeltaSyncCreateInstitution(Institution institution)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);

            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                OrgUnit schoolsOU = GetSchoolsOrgUnit(rootOrgUnitPath);
                OrgUnit daycaresOU = GetDaycaresOrgUnit(rootOrgUnitPath);

                OrgUnit ou;
                if (institution.Type.Equals(InstitutionType.SCHOOL))
                {
                    ou = CreateOU(schoolsOU.OrgUnitPath, true, name);
                }
                else if (institution.Type.Equals(InstitutionType.DAYCARE))
                {
                    ou = CreateOU(daycaresOU.OrgUnitPath, true, name);
                }
                else if (institution.Type.Equals(InstitutionType.MUNICIPALITY))
                {
                    ou = CreateOU(rootOrgUnitPath, true, name);
                }
                else
                {
                    throw new Exception($"Unknown institution type: {institution.Type.ToString()}. Institution with database id: {institution.DatabaseId} and institution number: {institution.InstitutionNumber}");
                }

                institution.GoogleWorkspaceId = ou.OrgUnitId;
                oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_WORKSPACE_ID, ou.OrgUnitId);
            } else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                OrgUnit employeeOU = GetEmployeeOrgUnit(rootOrgUnitPath);
                OrgUnit studentOU = GetStudentOrgUnit(rootOrgUnitPath);

                OrgUnit institutionStudentOU = CreateOU(studentOU.OrgUnitPath, true, name);

                institution.StudentInstitutionGoogleWorkspaceId = institutionStudentOU.OrgUnitId;
                oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_STUDENT_WORKSPACE_ID, institutionStudentOU.OrgUnitId);

                OrgUnit institutionEmployeeOU = CreateOU(employeeOU.OrgUnitPath, true, name);

                institution.EmployeeInstitutionGoogleWorkspaceId = institutionEmployeeOU.OrgUnitId;
                oS2skoledataService.SetFields(institution.DatabaseId, EntityType.INSTITUTION, SetFieldType.INSTITUTION_EMPLOYEE_WORKSPACE_ID, institutionEmployeeOU.OrgUnitId);
            }
        }

        public void DeltaSyncUpdateInstitution(Institution institution, OrgUnit entry, OrgUnit studentInstitutionOrgUnit, OrgUnit employeeInstitutionOrgUnit)
        {
            string name = GetNameForOU(true, institution.InstitutionName, institution.InstitutionNumber, null, null, null, 0);
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                UpdateOU(name, entry);
            } else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                UpdateOU(name, studentInstitutionOrgUnit);
                UpdateOU(name, employeeInstitutionOrgUnit);
            }
        }


        public void DeleteGroups(List<string> groupEmails, List<string> lockedGroupEmails)
        {
            logger.LogInformation("Before fetch groups");
            List<Group> ownedGroups = ListGroups(true);
            logger.LogInformation("After fetch groups");

            foreach (Group group in ownedGroups)
            {
                // if group is locked, skip
                if (lockedGroupEmails.Contains(group.Email))
                {
                    continue;
                }

                if (!groupEmails.Contains(group.Email))
                {
                    DeleteGroup(group.Id);
                    logger.LogInformation($"Group with name {group.Name} was deleted");
                }
            }
        }

        private bool IsGroupMember(Group group)
        {
            TraceLog("IsGroupMember", group.Name);
            return RetryUtil.WithRetry((() =>
            {
                var req = directoryService.Members.HasMember(group.Email, emailAccountToImpersonate);
                var result = req.Execute();
                return result.IsMember != null && (bool)result.IsMember;
            }));
        }

        public void RenameDrivesToDelete(List<string> driveIds, List<string> lockedDriveIds)
        {
            List<Drive> ownedDrives = ListDrives().Where(d => IsOwner(d)).ToList();

            foreach (Drive drive in ownedDrives)
            {
                // if drive is in locked institution, skip
                if (lockedDriveIds.Contains(drive.Id))
                {
                    continue;
                }

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
            return permissions.Any(p => p.Role.Equals("organizer") && Object.Equals(p.EmailAddress, emailAccountToImpersonate));
        }

        private OrgUnit GetSchoolsOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, schoolOUName);
        }

        private OrgUnit GetDaycaresOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, daycareOUName);
        }

        private OrgUnit GetEmployeeOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, employeeOUName);
        }

        private OrgUnit GetStudentOrgUnit(string path)
        {
            return GetTypeOrgUnit(path, studentOUName);
        }

        private OrgUnit GetStudentOrgUnitForInstitution(OrgUnit institutionOU, DBGroup group, Institution institution)
        {
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                if (institutionStudentsOUMapping.ContainsKey(institutionOU.OrgUnitPath))
                {
                    return institutionStudentsOUMapping[institutionOU.OrgUnitPath];
                }
                else
                {
                    OrgUnit ou = GetTypeOrgUnit(institutionOU.OrgUnitPath, studentOUName);
                    institutionStudentsOUMapping.Add(institutionOU.OrgUnitPath, ou);
                    return ou;
                }
            } 
            else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                string id;
                if (group != null)
                {
                    id = group.StudentInstitutionGoogleWorkspaceId;
                }
                else if (institution != null)
                {
                    id = institution.StudentInstitutionGoogleWorkspaceId;
                }
                else
                {
                    // will never happen unless method is called wrongly
                    throw new Exception("Failed to find StudentInstitutionGoogleWorkspaceId. Both group and institution is null");
                }

                if (institutionStudentsOUMapping.ContainsKey(id))
                {
                    return institutionStudentsOUMapping[id];
                }
                else
                {
                    OrgUnit ou = GetOrgUnit(id);
                    institutionStudentsOUMapping.Add(id, ou);
                    return ou;
                }
            } 
            else
            {
                throw new Exception("Failed to find student OrgUnit for institution. Unknown hierarchyType.");
            }
        }

        private OrgUnit GetEmployeeOrgUnitForInstitution(OrgUnit institutionOU, OrgUnit employeeInstitutionOrgUnit, Institution institution)
        {
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                if (institutionEmployeesOUMapping.ContainsKey(institutionOU.OrgUnitPath))
                {
                    return institutionEmployeesOUMapping[institutionOU.OrgUnitPath];
                }
                else
                {
                    OrgUnit ou = GetTypeOrgUnit(institutionOU.OrgUnitPath, employeeOUName);
                    institutionEmployeesOUMapping.Add(institutionOU.OrgUnitPath, ou);
                    return ou;
                }
            }
            else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                if (employeeInstitutionOrgUnit != null)
                {
                    return employeeInstitutionOrgUnit;
                }
                else
                {
                    return GetOrgUnit(institution.EmployeeInstitutionGoogleWorkspaceId);
                }
            }
            else
            {
                throw new Exception("Failed to find student OrgUnit for institution. Unknown hierarchyType.");
            }
        }

        private OrgUnit GetStudentsWithoutGroupOrgUnitForInstitution(OrgUnit institutionOU, OrgUnit studentInstitution)
        {
            if (hierarchyType.Equals(HierarchyType.INSTITUTION_FIRST))
            {
                if (institutionStudentsWithoutGroupOUMapping.ContainsKey(institutionOU.OrgUnitPath))
                {
                    return institutionStudentsWithoutGroupOUMapping[institutionOU.OrgUnitPath];
                } else
                {
                    OrgUnit ou = GetTypeOrgUnit(institutionOU.OrgUnitPath, studentsWithoutGroupsOUName);
                    institutionStudentsWithoutGroupOUMapping.Add(institutionOU.OrgUnitPath, ou);
                    return ou;
                }
            } 
            else if (hierarchyType.Equals(HierarchyType.INSTITUTION_LAST))
            {
                if (institutionStudentsWithoutGroupOUMapping.ContainsKey(studentInstitution.OrgUnitPath))
                {
                    return institutionStudentsWithoutGroupOUMapping[studentInstitution.OrgUnitPath];
                } else
                {
                    OrgUnit ou = GetTypeOrgUnit(studentInstitution.OrgUnitPath, studentsWithoutGroupsOUName);
                    institutionStudentsWithoutGroupOUMapping.Add(studentInstitution.OrgUnitPath, ou);
                    return ou;
                }
            } else
            {
                throw new Exception("Failed to find StudentsWithoutGroupOrgUnitForInstitution. Unknown hierarchyType");
            }
        }

        private OrgUnit GetTypeOrgUnit(string fromPath, string typeNameString)
        {
            List<OrgUnit> children = ListOrgUnits(fromPath, OrgunitsResource.ListRequest.TypeEnum.Children);
            OrgUnit matchOrgUnit = children.Where(c => Object.Equals(c.Name, typeNameString)).FirstOrDefault();
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

        private OrgUnit CreateOU(string ouPathToCreateIn, bool createSubOrgUnits, string name)
        {
            OrgUnit orgUnit = CreateOrgUnit(ouPathToCreateIn, name);
            logger.LogInformation($"Created OU with path {orgUnit.OrgUnitPath} and id {orgUnit.OrgUnitId}");

            if (createSubOrgUnits)
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
            if (!Object.Equals(match.Name, name))
            {   
                OrgUnit editPayload = new OrgUnit();
                editPayload.Name = name;
                UpdateOrgUnitGoogleWorkspace(editPayload, match.OrgUnitId);
                logger.LogInformation($"Updated OU with path {match.OrgUnitPath} and id {match.OrgUnitId}");
            }
        }

        private void DeletePermission(string driveId, Permission permission)
        {
            TraceLog("DeletePermission", $"driveId: {driveId}, PermissionId: {permission.Id}");
            RetryUtil.WithRetry((() =>
            {
                var permissionRequest = driveService.Permissions.Delete(driveId, permission.Id);
                permissionRequest.UseDomainAdminAccess = true;
                permissionRequest.SupportsAllDrives = true;
                permissionRequest.Execute();
                return true;
            }));
        }

        private Permission CreatePermission(string driveId, string userEmail)
        {
            TraceLog("CreatePermission", $"driveId: {driveId}, userEmail: {userEmail}");
            return RetryUtil.WithRetry((() =>
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
            }));
        }

        public Permission CreateDrivePermission(string driveId, string groupEmail, string role, string type)
        {
            TraceLog("CreateDrivePermission", $"driveId: {driveId}, groupEmail: {groupEmail}, role: {role}, type: {type}");
            return RetryUtil.WithRetry((() =>
            {
                var permissionRequest = driveService.Permissions.Create(
                    new Permission()
                    {
                        Type = type,
                        Role = role,
                        EmailAddress = groupEmail
                    },
                    driveId
                );
                permissionRequest.UseDomainAdminAccess = true;
                permissionRequest.SupportsAllDrives = true;

                return permissionRequest.Execute();
            }));
        }

        public Permission EditDrivePermission(string permissionId, string driveId, string groupEmail, string role, string type)
        {
            TraceLog("EditDrivePermission", $"permissionId: {permissionId}, driveId: {driveId}, groupEmail: {groupEmail}, type: {type}, role: {role}");
            return RetryUtil.WithRetry((() =>
            {
                var permissionRequest = driveService.Permissions.Update(
                    new Permission()
                    {
                        Role = role
                    },
                    driveId,
                    permissionId
                );
                permissionRequest.UseDomainAdminAccess = true;
                permissionRequest.SupportsAllDrives = true;

                return permissionRequest.Execute();
            }));
        }

        private List<Permission> ListPermissions(string driveId)
        {
            // timing issue in google workspace. Might work on first try, might not. So retry and sleep logic.
            for (int i = 1; i <= 10; i++)
            {
                try
                {
                    List<Permission> permissions = new List<Permission>();
                    ListPermissionsPage(null, driveId, permissions);
                    return permissions;
                }
                catch (Exception ex)
                {
                    if (i == 10)
                    {
                        throw new Exception($"Failed to list permissions for drive after 10 tries for drive with google workspace id {driveId}. Google Workspace exception: {ex.Message} ");
                    }
                    else
                    {
                        Thread.Sleep(5000 * i);
                    }
                }
            }

            return null;
        }

        private void ListPermissionsPage(string page, string driveId, List<Permission> permissions)
        {
            TraceLog("ListPermissionsPage", $"page: {page}, driveId: {driveId}, permissionsCount: {permissions.Count()}");
            RetryUtil.WithRetry((() =>
            {
                var permissionRequest = driveService.Permissions.List(driveId);
                permissionRequest.UseDomainAdminAccess = true;
                permissionRequest.Fields = "permissions(id,role,type,emailAddress)";
                permissionRequest.SupportsAllDrives = true;
                permissionRequest.PageSize = 100;
                if (page != null)
                {
                    permissionRequest.PageToken = page;
                }

                PermissionList result = permissionRequest.Execute();

                if (result != null && result.Permissions != null)
                {
                    permissions.AddRange(result.Permissions);

                    if (result.NextPageToken != null)
                    {
                        ListPermissionsPage(result.NextPageToken, driveId, permissions);
                    }
                }

                return true;
            }));
        }

        private void DeleteDrive(string id)
        {
            TraceLog("DeleteDrive", $"driveId: {id}");
            RetryUtil.WithRetry((() =>
            {
                var request = driveService.Drives.Delete(id);
                request.Execute();
                return true;
            }));
        }

        private Drive UpdateDrive(string name, string id)
        {
            TraceLog("UpdateDrive", $"name: {name}, driveId: {id}");
            return RetryUtil.WithRetry((() =>
            {
                var request = driveService.Drives.Update(new Drive()
                {
                    Name = name
                }, id);
                return request.Execute();
            }));
        }

        public Drive CreateDrive(string name)
        {
            TraceLog("CreateDrive", $"name: {name}");
            return RetryUtil.WithRetry((() =>
            {
                var requestId = Guid.NewGuid().ToString();
                var request = driveService.Drives.Create(new Drive()
                {
                    Name = name
                }, requestId);
                return request.Execute();
            }));
        }

        private Drive GetDrive(string id)
        {
            TraceLog("GetDrive", $"driveId: {id}");
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
            return RetryUtil.WithRetry((() =>
            {
                List<Drive> drives = new List<Drive>();
                ListDrivesPage(null, drives);
                return drives;
            }));
        }

        private void ListDrivesPage(string page, List<Drive> drives)
        {
            TraceLog("ListDrivesPage", $"page: {page}, drivesCount: {drives.Count()}");
            RetryUtil.WithRetry((() =>
            {
                var request = driveService.Drives.List();
                request.UseDomainAdminAccess = true;
                request.PageSize = 100;
                if (page != null)
                {
                    request.PageToken = page;
                }
                //request.Q = "memberCount < 20";
                var result = request.Execute();

                if (result != null && result.Drives != null)
                {
                    drives.AddRange(result.Drives.ToList());

                    if (result.NextPageToken != null)
                    {
                        ListDrivesPage(result.NextPageToken, drives);
                    }
                }
                return true;
            }));
        }

        public void MoveToDeletedOrgUnits(string id, string path, string prefix = null, OrgUnit orgUnit = null)
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
            if (prefix != null && orgUnit != null)
            {
                editedOU.Name = prefix + orgUnit.Name;
            }
            editedOU.ParentOrgUnitPath = match.OrgUnitPath;
            UpdateOrgUnitGoogleWorkspace(editedOU, id);
            logger.LogInformation($"Moved ou with id {id} and path {path} to deleted ous ou for today");
        }

        private OrgUnit UpdateOrgUnitGoogleWorkspace(OrgUnit editedOrgunit, string id)
        {
            TraceLog("UpdateOrgUnitGoogleWorkspace", editedOrgunit.Name);
            return RetryUtil.WithRetry((() =>
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
            }));
        }

        public OrgUnit GetOrgUnit(string id)
        {
            if (id == null)
            {
                return null;
            }

            if (workspaceIdsOUMapping.ContainsKey(id))
            {
                TraceLog("GetOrgUnit", "id: " + id + ", from cached map");
                return workspaceIdsOUMapping[id];
            }

            try
            {
                TraceLog("GetOrgUnit", "id: " + id + ", from Google Workspace");
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
            TraceLog("CreateOrgUnit", $"parentPath: {parentPath}, name {name}");
            return RetryUtil.WithRetry((() =>
            {
                var createReq = directoryService.Orgunits.Insert(new OrgUnit
                {
                    Name = name,
                    ParentOrgUnitPath = parentPath,
                }, "my_customer");

                OrgUnit ou = createReq.Execute();
                workspaceIdsOUMapping.Add(ou.OrgUnitId, ou);
                return ou;
            }));
        }

        public List<OrgUnit> ListOrgUnits(string fromPath, OrgunitsResource.ListRequest.TypeEnum type)
        {
            TraceLog("ListOrgUnits", $"fromPath: {fromPath}");
            return RetryUtil.WithRetry((() =>
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
            }));
            
        }

        public Google.Apis.Admin.Directory.directory_v1.Data.User GetUser(string username)
        {
            TraceLog("GetUser", $"username: {username}");
            try
            {
                var getUserReq = directoryService.Users.Get(username + "@" + domain);
                getUserReq.Projection = UsersResource.GetRequest.ProjectionEnum.Full;
                // kaster exception hvis bruger ikke findes
                return getUserReq.Execute();
            } catch (Exception e)
            {
                return null;
            }
        }

        public bool AccountExists(string stilUsername)
        {
            return GetUser(stilUsername) != null;
        }

        public List<Google.Apis.Admin.Directory.directory_v1.Data.User> ListUsers(string fromPath, bool includeSubPaths = true)
        {
            List<Google.Apis.Admin.Directory.directory_v1.Data.User> users = new List<Google.Apis.Admin.Directory.directory_v1.Data.User>();
            ListUsersPage(fromPath, null, users, includeSubPaths);
            return users;
        }

        private void ListUsersPage(string fromPath, string page, List<Google.Apis.Admin.Directory.directory_v1.Data.User> users, bool includeSubPaths)
        {
            TraceLog("ListUsersPage", $"fromPath: {fromPath}, page: {page}, currentFetchedUsers: {users.Count()}, includeSubPaths: {includeSubPaths}");
            var listReq = directoryService.Users.List();
            listReq.Domain = domain;
            listReq.Projection = UsersResource.ListRequest.ProjectionEnum.Full;

            // page size
            listReq.MaxResults = 500;

            //listReq.ViewType = UsersResource.ListRequest.ViewTypeEnum.DomainPublic;

            // for some reason this does not work anymore, so we are doing the filtering afterwards
            /*
            if (fromPath != null)
            {
                listReq.Query = $"orgUnitPath = '{fromPath}'";
            }*/

            if (page != null)
            {
                listReq.PageToken = page;
            }

            Users pageUsers = listReq.Execute();

            if (pageUsers != null && pageUsers.UsersValue != null)
            {
                if (fromPath != null)
                {
                    if (includeSubPaths)
                    {
                        users.AddRange(pageUsers.UsersValue.Where(u => u.OrgUnitPath.StartsWith(fromPath)).ToList());
                    } else
                    {
                        users.AddRange(pageUsers.UsersValue.Where(u => Object.Equals(u.OrgUnitPath, fromPath)).ToList());
                    }
                }
                else
                {
                    users.AddRange(pageUsers.UsersValue.ToList());
                }

                if (pageUsers.NextPageToken != null)
                {
                    ListUsersPage(fromPath, pageUsers.NextPageToken, users, includeSubPaths);
                }
            }
        }

        public Google.Apis.Admin.Directory.directory_v1.Data.User CreateUserInWorkspace(DBUser user, string path)
        {
            TraceLog("CreateUserInWorkspace", $"user: {user.Username}, path: {path}");

            var organizations = new List<UserOrganization>();
            if (setContactCard && user.ContactCards != null)
            {
                foreach (DBContactCard contactCard in user.ContactCards)
                {
                    var organization = new UserOrganization
                    {
                        Title = contactCard.InstitutionRoles,
                        Department = contactCard.InstitutionName,
                        Location = contactCard.InstitutionGroups,
                        Primary = Object.Equals(user.CurrentInstitution.InstitutionName.ToLower(), contactCard.InstitutionName.ToLower()),
                    };
                    organizations.Add(organization);
                }
            }

            return RetryUtil.WithRetry((() =>
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
                    OrgUnitPath = path,
                    Organizations = organizations,
                    CustomSchemas = BuildInitialOS2skoledataSchemas(user)
                });

                Google.Apis.Admin.Directory.directory_v1.Data.User savedUser = insertRequest.Execute();
                return savedUser;
            }));
        }

        private IDictionary<string, IDictionary<string, object>> TESTBuildInitialOS2skoledataSchemas()
        {
            IDictionary<string, IDictionary<string, object>> schemas = new Dictionary<string, IDictionary<string, object>>();
            IDictionary<string, object> os2skoledataSchema = new Dictionary<string, object>();
            os2skoledataSchema.Add(KEYS_ROLE, "STUDENT");
            schemas.Add(KEYS_OS2SKOLEDATA, os2skoledataSchema);

            return schemas;
        }

        private IDictionary<string, IDictionary<string, object>> BuildInitialOS2skoledataSchemas(DBUser user)
        {
            IDictionary<string, IDictionary<string, object>> schemas = new Dictionary<string, IDictionary<string, object>>();
            IDictionary<string, object> os2skoledataSchema = new Dictionary<string, object>();
            os2skoledataSchema.Add(KEYS_ROLE, user.GlobalRole.ToString());
            schemas.Add(KEYS_OS2SKOLEDATA, os2skoledataSchema);
           
            return schemas;
        }

        private Google.Apis.Admin.Directory.directory_v1.Data.User UpdateUserInWorkspace(DBUser dbUser, string path)
        {
            TraceLog("UpdateUserInWorkspace", $"user: {dbUser.Username}, path: {path}");

            var organizations = new List<UserOrganization>();
            if (setContactCard && dbUser.ContactCards != null)
            {
                foreach (DBContactCard contactCard in dbUser.ContactCards)
                {
                    var organization = new UserOrganization
                    {
                        Title = contactCard.InstitutionRoles,
                        Department = contactCard.InstitutionName,
                        Location = contactCard.InstitutionGroups,
                        Primary = isPrimaryOrganization(contactCard.InstitutionName, path),
                    };
                    organizations.Add(organization);
                }
            }

            return RetryUtil.WithRetry((() =>
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
                    OrgUnitPath = path,
                    Organizations = organizations

                }, dbUser.Username + "@" + domain);

                // kaster exception hvis bruger ikke findes
                Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
                return user;
            }));
        }

        private bool isPrimaryOrganization(string institutionName, string path)
        {
            if (!useDanishCharacters)
            {
                institutionName = institutionName.Unidecode();
            }

            return path.ToLower().Contains(institutionName.ToLower());
        }

        public void HandleSuspendUser(string username, Google.Apis.Admin.Directory.directory_v1.Data.User gwUser)
        {
            if ((bool)gwUser.Suspended)
            {
                logger.LogDebug($"Skipping {username}. User is already suspended in Google Workspace");
                return;
            }

            if (userDryRun)
            {
                logger.LogInformation($"UserDryRun: would have suspended user with username {username} in Google Workspace");
            }
            else 
            {
                string email = gwUser.PrimaryEmail;
                if (HasStudentLicense(email))
                {
                    if (String.IsNullOrEmpty(studentLicenseProductId) || String.IsNullOrEmpty(studentLicenseSkuId))
                    {
                        logger.LogInformation($"Would have removed student license from user with username ${username} but licensing settings is not configured");
                    } else
                    {
                        RemoveStudentLicense(email);
                    }
                }

                if (HasStaffLicense(email))
                {
                    if (String.IsNullOrEmpty(staffLicenseProductId) || String.IsNullOrEmpty(staffLicenseSkuId))
                    {
                        logger.LogInformation($"Would have staff removed license from user with username ${username} but licensing settings is not configured");
                    }
                    else
                    {
                        RemoveStaffLicense(email);
                    }
                }

                // if user has never been logged in (year == 1970), delete completely
                DateTime lastlogin = gwUser.LastLoginTime ?? DateTime.MinValue;
                if (lastlogin.Year < 1971)
                {
                    logger.LogInformation($"User {email} should have been suspended but because it has never logged in it will be deleted.");
                    DeleteUser(email);
                } else
                {
                    // update custom schemas with deleted date
                    UpdateOS2skoledataSchemaValue(KEYS_DELETED_DATE, DateTime.Now.ToString(), gwUser, true);
                    SuspendUser(username);
                }                
            }
        }

        private void SuspendUser(string username)
        {
            TraceLog("SuspendUser", $"username: {username}");
            RetryUtil.WithRetry((() =>
            {
                logger.LogInformation($"Attempting to suspend user with username {username}");
                OrgUnit suspendedUsersOUToday = GetSuspendedUsersOUToday();
                var updateUserReq = directoryService.Users.Update(new Google.Apis.Admin.Directory.directory_v1.Data.User
                {
                    Suspended = true,
                    SuspensionReason = "ADMIN",
                    OrgUnitPath = suspendedUsersOUToday.OrgUnitPath
                }, username + "@" + domain);

                // kaster exception hvis bruger ikke findes
                Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
                logger.LogInformation($"Suspended user with username {username}");

                // register as deactivated in OS2skoledata
                oS2skoledataService.SetActionOnUser(username, ActionType.DEACTIVATE);

                return true;
            }));
        }

        private OrgUnit GetSuspendedUsersOUToday()
        {
            var newOUName = "deaktiverede_brugere_" + DateTime.Now.ToString("yyyy_MM_dd");
            List<OrgUnit> orgUnits = ListOrgUnits(suspendedUsersOU, OrgunitsResource.ListRequest.TypeEnum.Children);
            OrgUnit match = null;
            foreach (OrgUnit ou in orgUnits)
            {
                if (Object.Equals(ou.Name, newOUName))
                {
                    match = ou;
                }
            }

            if (match == null)
            {
                match = CreateOrgUnit(suspendedUsersOU, newOUName);
                logger.LogInformation("Created suspended users ou for today: " + suspendedUsersOU + "/" + newOUName);
            }

            return match;
        }

        private Group UpdateGroupGoogleWorkspace(string name, string email, string generatedMail)
        {
            TraceLog("UpdateGroupGoogleWorkspace", $"name: {name}, email: {email}, generatedMail: {generatedMail}");
            return RetryUtil.WithRetry((() =>
            {
                var updateReq = directoryService.Groups.Patch(new Group
                {
                    Name = name,
                    Email = generatedMail
                }, email);

                Group group = updateReq.Execute();

                return group;
            }));
        }

        public Group GetGroup(string email, List<Group> allOurGWGroups)
        {
            if (email == null)
            {
                return null;
            }

            if (allOurGWGroups != null)
            {
                Group cachedGroup = allOurGWGroups.Where(g => Object.Equals(g.Email.ToLower(), email.ToLower())).FirstOrDefault();
                if (cachedGroup != null)
                {
                    TraceLog("GetGroup", $"email: {email}, from cached list");
                    return cachedGroup;
                }
            }

            try
            {
                TraceLog("GetGroup", $"email: {email}");
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
            TraceLog("CreateGroup", $"name: {name}, email: {email}");
            return RetryUtil.WithRetry((() =>
            {
                var createReq = directoryService.Groups.Insert(new Group
                {
                    Email = email,
                    Name = name
                });

                Group group = createReq.Execute();
                return group;
            }));
        }

        public void DeleteGroup(string groupId)
        {
            TraceLog("DeleteGroup", $"groupId: {groupId}");
            RetryUtil.WithRetry((() =>
            {
                directoryService.Groups.Delete(groupId).Execute();
                return true;
            }));
        }

        public List<Group> ListGroups(bool weAreMember = false)
        {
            List<Group> groups = new List<Group>();
            GetGroupsPage(null, groups, weAreMember);

            return groups;
        }

        private void GetGroupsPage(String page, List<Group> groups, bool weAreMember)
        {
            TraceLog("GetGroupsPage", $"page: {page}, groupCount: {groups.Count()}, weAreMember: {weAreMember}");
            RetryUtil.WithRetry((() =>
            {
                var listReq = directoryService.Groups.List();
                listReq.MaxResults = 200;
            
                if (page != null)
                {
                    listReq.PageToken = page;
                }

                if (weAreMember)
                {
                    listReq.UserKey = emailAccountToImpersonate;
                } else
                {
                    listReq.Customer = "my_customer";
                }

                Groups pageGroups = listReq.Execute();
                if (pageGroups != null && pageGroups.GroupsValue != null)
                {
                    groups.AddRange(pageGroups.GroupsValue.ToList());
                    logger.LogInformation("Have fetched " + groups.Count + " now");
                    if (pageGroups.NextPageToken != null)
                    {
                        logger.LogInformation("Fetching next page of groups");
                        GetGroupsPage(pageGroups.NextPageToken, groups, weAreMember);
                    }
                }

                return true;
            }));
        }

        public Member AddMemberToGroup(string groupId, string userId)
        {
            TraceLog("AddMemberToGroup", $"groupId: {groupId}, userId: {userId}");
            return RetryUtil.WithRetry((() =>
            {
                var insertMemberReq = directoryService.Members.Insert(new Member
                {
                    Email = userId
                }, groupId);

                Member member = insertMemberReq.Execute();

                return member;
            }));
        }

        public Member AddOwnerToGroup(string groupId)
        {
            TraceLog("AddOwnerToGroup", $"groupId: {groupId}");
            return RetryUtil.WithRetry((() =>
            {
                var insertMemberReq = directoryService.Members.Insert(new Member
                {
                    Email = emailAccountToImpersonate,
                    Role = "OWNER"
                }, groupId);

                Member member = insertMemberReq.Execute();

                return member;
            }));
        }

        public void RemoveMemberFromGroup(string groupEmail, string userId)
        {
            TraceLog("RemoveMemberFromGroup", $"groupEmail: {groupEmail}, userId: {userId}");
            RetryUtil.WithRetry((() =>
            {
                var deleteMemberReq = directoryService.Members.Delete(groupEmail, userId);
                deleteMemberReq.Execute();
                return true;
            }));
        }

        public List<Member> ListGroupMembers(string groupId, bool ownersOnly = false)
        {
            // timing issue in google workspace. Might work on first try, might not. So retry and sleep logic.
            for (int i = 1; i <= 10; i++)
            {
                try
                {
                    List<Member> members = new List<Member>();
                    GetGroupMembersPage(null, members, groupId, ownersOnly);

                    return members;
                }
                catch (Exception ex)
                {
                    if (i == 10)
                    {
                        throw new Exception($"Failed to list group members after 10 tries for group with google workspace id {groupId}. Google Workspace exception: {ex.Message} ");
                    }
                    else
                    {
                        Thread.Sleep(5000 * i);
                    }
                }
            }

            return null;
        }

        private void GetGroupMembersPage(String page, List<Member> members, string groupId, bool ownersOnly)
        {
            TraceLog("GetGroupMembersPage", $"page: {page}, memberCount: {members.Count()}, groupId: {groupId}, ownersOnly: {ownersOnly}");
            RetryUtil.WithRetry((() =>
            {
                var listMemberReq = directoryService.Members.List(groupId);
                listMemberReq.MaxResults = 200;
                if (page != null)
                {
                    listMemberReq.PageToken = page;
                }
                if (ownersOnly)
                {
                    listMemberReq.Roles = "OWNER";
                }

                var result = listMemberReq.Execute();

                if (result != null && result.MembersValue != null)
                {
                    members.AddRange(result.MembersValue.ToList());
                    if (result.NextPageToken != null)
                    {
                        GetGroupMembersPage(result.NextPageToken, members, groupId, ownersOnly);
                    }
                }

                return true;
            }));
        }

        public LicenseAssignment AddStudentLicense(String userEmail)
        {
            TraceLog("AddStudentLicense", $"userEmail: {userEmail}");
            return RetryUtil.WithRetry((() =>
            {
                logger.LogInformation($"Trying to assign student license to user with email: {userEmail}");
                var req = licenseService.LicenseAssignments.Insert(new LicenseAssignmentInsert
                {
                    UserId = userEmail
                }, studentLicenseProductId, studentLicenseSkuId);

                var result = req.Execute();
                logger.LogInformation($"Assigned student license to user with email: {userEmail}");
                return result;
            }));
        }

        public LicenseAssignment AddStaffLicense(String userEmail)
        {
            TraceLog("AddStaffLicense", $"userEmail: {userEmail}");
            return RetryUtil.WithRetry((() =>
            {
                logger.LogInformation($"Trying to assign staff license to user with email: {userEmail}");
                var req = licenseService.LicenseAssignments.Insert(new LicenseAssignmentInsert
                {
                    UserId = userEmail
                }, staffLicenseProductId, staffLicenseSkuId);

                var result = req.Execute();
                logger.LogInformation($"Assigned staff license to user with email: {userEmail}");
                return result;
            }));
        }

        public void RemoveStudentLicense(String userEmail)
        {
            TraceLog("RemoveStudentLicense", $"userEmail: {userEmail}");
            RetryUtil.WithRetry((() =>
            {
                licenseService.LicenseAssignments.Delete(studentLicenseProductId, studentLicenseSkuId, userEmail).Execute();
                return true;
            }));
        }

        public void RemoveStaffLicense(String userEmail)
        {
            TraceLog("RemoveStaffLicense", $"userEmail: {userEmail}");
            RetryUtil.WithRetry((() =>
            {
                licenseService.LicenseAssignments.Delete(staffLicenseProductId, staffLicenseSkuId, userEmail).Execute();
                return true;
            }));
        }

        public bool HasStudentLicense(String userEmail)
        {
            if (String.IsNullOrEmpty(studentLicenseProductId) || String.IsNullOrEmpty(studentLicenseSkuId))
            {
                logger.LogInformation($"Would have checked if user with userEmail {userEmail} has student license but licensing settings is not configured");
                return false;
            }
            return HasLicense(userEmail, studentLicenseProductId, studentLicenseSkuId) != null;
        }

        public bool HasStaffLicense(String userEmail)
        {
            if (String.IsNullOrEmpty(staffLicenseProductId) || String.IsNullOrEmpty(staffLicenseSkuId))
            {
                logger.LogInformation($"Would have checked if user with userEmail {userEmail} has staff license but licensing settings is not configured");
                return false;
            }
            return HasLicense(userEmail, studentLicenseProductId, studentLicenseSkuId) != null;
        }

        public LicenseAssignment HasLicense(string userEmail, string productId, string skuId)
        {
            TraceLog("HasLicense", $"userEmail: {userEmail}, productId: {productId}");
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
            string calculatedInstitutionName = institutionName;
            string calculatedClassName = name;
            if (!useDanishCharacters)
            {
                calculatedInstitutionName = institutionName.Unidecode();
                calculatedClassName = name.Unidecode();
            }

            string calculatedName = "";
            if (institutionLevel)
            {
                calculatedName = institutionOUNameStandard
                    .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber);
            }
            else
            {
                if (startYear != 0)
                {
                    calculatedName = classOUNameStandard
                    .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber)
                    .Replace("{CLASS_NAME}", calculatedClassName)
                    .Replace("{CLASS_ID}", id)
                    .Replace("{CLASS_LEVEL}", level)
                    .Replace("{CLASS_YEAR}", startYear + "");
                } else
                {
                    string nameStandard = null;
                    if (String.IsNullOrEmpty(classOUNameStandardNoClassYear))
                    {
                        nameStandard = classOUNameStandard;
                    } else
                    {
                        nameStandard = classOUNameStandardNoClassYear;
                    }

                    calculatedName = nameStandard
                    .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                    .Replace("{INSTITUTION_NUMBER}", institutionNumber)
                    .Replace("{CLASS_NAME}", calculatedClassName)
                    .Replace("{CLASS_ID}", id)
                    .Replace("{CLASS_LEVEL}", level);
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
            string calculatedInstitutionName = institution.InstitutionName;
            if (!useDanishCharacters)
            {
                calculatedInstitutionName = calculatedInstitutionName.Unidecode();
            }

            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionDriveNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
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
            string calculatedInstitutionName = institution.InstitutionName;
            if (!useDanishCharacters)
            {
                calculatedInstitutionName = calculatedInstitutionName.Unidecode();
            }

            string name = "";
            switch (type)
            {
                case "ALL":
                    name = allInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "EMPLOYEES":
                    name = allEmployeesInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber);
                    break;
                case "STUDENTS":
                    name = allStudentsInInstitutionGroupNameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
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
            string calculatedInstitutionName = institution.InstitutionName;
            string calculatedClassName = currentClass.GroupName;
            if (!useDanishCharacters)
            {
                calculatedInstitutionName = calculatedInstitutionName.Unidecode();
                calculatedClassName = calculatedClassName.Unidecode();
            }

            string name = "";
            if (currentClass.StartYear != 0)
            {
                name = classDriveNameStandard
                    .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                    .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                    .Replace("{CLASS_NAME}", calculatedClassName)
                    .Replace("{CLASS_ID}", currentClass.GroupId)
                    .Replace("{CLASS_LEVEL}", currentClass.GroupLevel)
                    .Replace("{CLASS_YEAR}", currentClass.StartYear.ToString());
            } else
            {
                string nameStandard = null;
                if (String.IsNullOrEmpty(classDriveNameStandardNoClassYear))
                {
                    nameStandard = classDriveNameStandard;
                } else
                {
                    nameStandard = classDriveNameStandardNoClassYear;
                }

                name = nameStandard
                    .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                    .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                    .Replace("{CLASS_NAME}", calculatedClassName)
                    .Replace("{CLASS_ID}", currentClass.GroupId)
                    .Replace("{CLASS_LEVEL}", currentClass.GroupLevel);
            }

            name = EscapeCharacters(name);

            if (name.Length > 64)
            {
                name = name.Substring(0, 64);
            }

            return name;
        }

        private string GetClassGroupName(DBGroup currentClass, Institution institution, bool onlyStudents)
        {
            string name = "";
            string calculatedInstitutionName = institution.InstitutionName;
            string calculatedClassName = currentClass.GroupName;
            if (!useDanishCharacters)
            {
                calculatedInstitutionName = calculatedInstitutionName.Unidecode();
                calculatedClassName = calculatedClassName.Unidecode();
            }

            if (currentClass.StartYear != 0)
            {
                string nameStandard = null;
                if (onlyStudents)
                {
                    nameStandard = classGroupOnlyStudentsNameStandard;
                }
                else
                {
                    nameStandard = classGroupNameStandard;
                }

                if (nameStandard == null)
                {
                    return null;
                }

                name = nameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{CLASS_NAME}", calculatedClassName)
                        .Replace("{CLASS_ID}", currentClass.GroupId)
                        .Replace("{CLASS_LEVEL}", currentClass.GroupLevel)
                        .Replace("{CLASS_YEAR}", currentClass.StartYear.ToString());
            } else
            {
                string nameStandard = null;
                if (onlyStudents)
                {
                    if (String.IsNullOrEmpty(classGroupOnlyStudentsNameStandardNoClassYear))
                    {
                        nameStandard = classGroupOnlyStudentsNameStandard;
                    }
                    else
                    {
                        nameStandard = classGroupOnlyStudentsNameStandardNoClassYear;
                    }
                } else
                {
                    if (String.IsNullOrEmpty(classGroupNameStandardNoClassYear))
                    {
                        nameStandard = classGroupNameStandard;
                    }
                    else
                    {
                        nameStandard = classGroupNameStandardNoClassYear;
                    }
                }

                if (nameStandard == null)
                {
                    return null;
                }

                name = nameStandard
                        .Replace("{INSTITUTION_NAME}", calculatedInstitutionName)
                        .Replace("{INSTITUTION_NUMBER}", institution.InstitutionNumber)
                        .Replace("{CLASS_NAME}", calculatedClassName)
                        .Replace("{CLASS_ID}", currentClass.GroupId)
                        .Replace("{CLASS_LEVEL}", currentClass.GroupLevel);
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

        public bool CheckIfUserShouldBeCreatedInGW(DBUser user)
        {
            bool create = false;
            if (rolesToBeCreatedDirectlyInGW != null)
            {
                foreach (string role in rolesToBeCreatedDirectlyInGW)
                {
                    if (user.TotalRoles.Contains(role))
                    {
                        return true;
                    }
                }
            }
            return create;
        }

        private void PopulateTable()
        {
            // if usernameStandard == UNIID, we are not generating usernames, but just using UNIID from STIL
            if (usernameStandard.Equals(UsernameStandardType.UNIID)) {
                return;
            }

            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN))
            {
                int idx = 0;
                char[] possibleNumbers = "0123456789".ToCharArray();
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
            else if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM))
            {
                // do nothing. Random number will be generated later 
            }
            else if (usernameStandard.Equals(UsernameStandardType.THREE_NUMBERS_THREE_CHARS_FROM_NAME))
            {
                int idx = 0;
                char[] possibleNumbers = "23456789".ToCharArray();
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


        public Dictionary<string, List<string>> GenerateUsernameMap(List<string> allOS2skoledataUsernames)
        {
            Dictionary<string, List<string>> map = new Dictionary<string, List<string>>();

            // if usernameStandard == UNIID, we are not generating usernames, but just using UNIID from STIL
            if (usernameStandard.Equals(UsernameStandardType.UNIID))
            {
                return map;
            }

            List<string> allUsernames = new List<string>();
            allUsernames.AddRange(allOS2skoledataUsernames);
            foreach (string username in allUsernames)
            {
                string key = "";
                if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM))
                {
                    try
                    {
                        int wantedTotalLength = usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) ? randomStandardLetterCount + randomStandardNumberCount : 8;
                        if (username.Length != wantedTotalLength)
                        {
                            continue;
                        }

                        int wantedLetterLength = usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM) ? randomStandardLetterCount : 4;
                        if (username.Length >= wantedLetterLength)
                        {
                            key = username.Substring(0, wantedLetterLength);
                        }
                        else
                        {
                            key = username;
                        }

                    }
                    catch (Exception e)
                    {
                        logger.LogWarning(e, "Failed to add username " + username + " to username map");
                        continue;
                    }
                }
                else if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_FIRST))
                {
                    try
                    {
                        string usernameWithoutPrefix = username.Replace(usernamePrefix, "");
                        if (usernameWithoutPrefix.Length < 3)
                        {
                            continue;
                        }
                        key = usernameWithoutPrefix.Substring(0, 3);
                    }
                    catch (Exception e)
                    {
                        logger.LogWarning(e, "Failed to add username " + username + " to username map");
                        continue;
                    }
                }
                else if (usernameStandard.Equals(UsernameStandardType.PREFIX_NAME_LAST) || usernameStandard.Equals(UsernameStandardType.THREE_NUMBERS_THREE_CHARS_FROM_NAME))
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
                }
                else
                {
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

        private string GetPrefix()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.UNIID) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM))
            {
                return "";
            }
            return usernamePrefix == null ? "" : usernamePrefix;
        }

        private int getNamePartLength()
        {
            if (usernameStandard.Equals(UsernameStandardType.AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN) || usernameStandard.Equals(UsernameStandardType.UNIID))
            {
                return 4;
            }
            else if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM))
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

        public string GenerateUsername(string firstname, Dictionary<string, List<string>> usernameMap)
        {
            if (userDryRun)
            {
                logger.LogInformation("not generating username because of userDryRun");
                return null;
            }

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
                if (usernameStandard.Equals(UsernameStandardType.FROM_STIL_OR_AS_UNILOGIN_RANDOM))
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

        private string GetNamePart(string firstname)
        {
            int namePartLength = getNamePartLength();
            string name = firstname.ToLower();
            name.Replace("æ", "ae");
            name.Replace("ø", "oe");
            name.Replace("å", "aa");
            name = name.Unidecode();
            name = System.Text.RegularExpressions.Regex.Replace(name, "[^a-zA-Z0-9]*", "", System.Text.RegularExpressions.RegexOptions.None);

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


        private IDictionary<String, Object> GetOS2skoledataSchemaFromUser(Google.Apis.Admin.Directory.directory_v1.Data.User user, bool nullIfNull = false)
        {
            if (user.CustomSchemas == null)
            {
                IDictionary<string, IDictionary<string, object>> schemas = new Dictionary<string, IDictionary<string, object>>();
                user.CustomSchemas = schemas;
            }

            if (!user.CustomSchemas.ContainsKey(KEYS_OS2SKOLEDATA))
            {
                if (nullIfNull)
                {
                    return null;
                }

                IDictionary<string, object> os2skoledataSchema = new Dictionary<string, object>();
                user.CustomSchemas.Add(KEYS_OS2SKOLEDATA, os2skoledataSchema);
            }

            return user.CustomSchemas[KEYS_OS2SKOLEDATA];
        }

        private object GetOS2skoledataSchemaValue(string key, IDictionary<string, object> os2skoledataSchema)
        {
            if (os2skoledataSchema != null && os2skoledataSchema.TryGetValue(key, out var value))
            {
                return value;
            }
            return null;
        }

        private void UpdateOS2skoledataSchemaValue(string key, object value, Google.Apis.Admin.Directory.directory_v1.Data.User user, bool saveInGW)
        {
            if (user.CustomSchemas == null)
            {
                IDictionary<string, IDictionary<string, object>> schemas = new Dictionary<string, IDictionary<string, object>>();
                user.CustomSchemas = schemas;
            }

            if (!user.CustomSchemas.ContainsKey(KEYS_OS2SKOLEDATA))
            {
                IDictionary<string, object> os2skoledataSchema = new Dictionary<string, object>();
                user.CustomSchemas.Add(KEYS_OS2SKOLEDATA, os2skoledataSchema);
            }

            user.CustomSchemas[KEYS_OS2SKOLEDATA][key] = value;

            if (saveInGW)
            {
                UpdateOS2skoledataSchemaOnUser(user);
            }
        }

        private Google.Apis.Admin.Directory.directory_v1.Data.User UpdateOS2skoledataSchemaOnUser(Google.Apis.Admin.Directory.directory_v1.Data.User gwUser)
        {
            TraceLog("UpdateOS2skoledataSchemaOnUser", $"gwUser: {gwUser.PrimaryEmail}");
            var updateUserReq = directoryService.Users.Update(new Google.Apis.Admin.Directory.directory_v1.Data.User
            {
                CustomSchemas = gwUser.CustomSchemas

            }, gwUser.PrimaryEmail);

            // kaster exception hvis bruger ikke findes
            Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
            return user;
        }

        private void DeleteUser(string email)
        {
            TraceLog("DeleteUser", $"email: {email}");
            RetryUtil.WithRetry((() =>
            {
                var deleteUserReq = directoryService.Users.Delete(email);

                // kaster exception hvis bruger ikke findes
                deleteUserReq.Execute();

                logger.LogInformation($"User {email} has been fully deleted.");
                return true;
            }));
        }

        public void UpdateCustomOS2skoledataSchema()
        {
            TraceLog("UpdateCustomOS2skoledataSchema", $"");
            RetryUtil.WithRetry((() =>
            {
                Schema os2skoledataSchema = GetOS2skoledataSchema();
                if (os2skoledataSchema == null)
                {
                    var newSchema = new Schema
                    {
                        SchemaName = KEYS_OS2SKOLEDATA,
                        DisplayName = "OS2skoledata",
                        Fields = new List<SchemaFieldSpec>
                        {
                            new SchemaFieldSpec
                            {
                                FieldName = KEYS_ROLE,
                                FieldType = "STRING",
                                MultiValued = false,
                                DisplayName = "Role",
                                ReadAccessType = "ADMINS_AND_SELF"
                            },
                            new SchemaFieldSpec
                            {
                                FieldName = KEYS_DELETED_DATE,
                                FieldType = "STRING",
                                MultiValued = false,
                                DisplayName = "Deleted date",
                                ReadAccessType = "ADMINS_AND_SELF"
                            }
                        }
                    };

                    var insertRequest = directoryService.Schemas.Insert(newSchema, "my_customer");
                    os2skoledataSchema = insertRequest.Execute();
                }
                return true;
            }));
        }

        private Schema GetOS2skoledataSchema()
        {
            TraceLog("GetOS2skoledataSchema", $"");
            try
            {
                var getRequest = directoryService.Schemas.Get("my_customer", KEYS_OS2SKOLEDATA);
                // throws exception if not present
                return getRequest.Execute();
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        private void TraceLog(string methodName, string details)
        {
            if (gwTraceLog)
            {
                logger.LogInformation($"Google Workspace trace log - method {methodName} called. Details: {details}");
            }
        }

        public void MoveKeepAliveUsers(List<string> keepAliveUsernames, List<Google.Apis.Admin.Directory.directory_v1.Data.User> allGWUsers)
        {
            if (string.IsNullOrEmpty(keepAliveOUPath))
            {
                return;
            }

            foreach (string username in keepAliveUsernames)
            {
                Google.Apis.Admin.Directory.directory_v1.Data.User user = allGWUsers.Where(u => Object.Equals(u.PrimaryEmail, username + "@" + domain)).FirstOrDefault();
                if (user != null)
                {
                    string userPath = user.OrgUnitPath;

                    if (!keepAliveOUPath.Equals(userPath))
                    {
                        MoveToKeepAliveOU(username);
                    }
                }
            }
        }

        private void MoveToKeepAliveOU(string username)
        {
            RetryUtil.WithRetry((() =>
            {
                var updateUserReq = directoryService.Users.Update(new Google.Apis.Admin.Directory.directory_v1.Data.User
                {
                    OrgUnitPath = keepAliveOUPath
                }, username + "@" + domain);

                // kaster exception hvis bruger ikke findes
                Google.Apis.Admin.Directory.directory_v1.Data.User user = updateUserReq.Execute();
                logger.LogInformation($"Moved user with username {username} to keepAliveOUPath {keepAliveOUPath}");

                return true;
            }));
        }
    }
}
