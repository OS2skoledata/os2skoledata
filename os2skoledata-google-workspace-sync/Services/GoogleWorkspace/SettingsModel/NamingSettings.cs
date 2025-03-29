namespace os2skoledata_ad_sync.Services.GoogleWorkspace
{
    public class NamingSettings
    {
        public string EmployeeOUName { get; set; }
        public string StudentOUName { get; set; }
        public string StudentsWithoutGroupsOUName { get; set; }
        public string ClassOUNameStandard { get; set; }
        public string ClassOUNameStandardNoClassYear { get; set; }
        public string InstitutionOUNameStandard { get; set; }
        public string GlobalEmployeeDriveName { get; set; }
        public string AllInInstitutionDriveNameStandard { get; set; }
        public string AllStudentsInInstitutionDriveNameStandard { get; set; }
        public string AllEmployeesInInstitutionDriveNameStandard { get; set; }
        public string ClassDriveNameStandard { get; set; }
        public string ClassDriveNameStandardNoClassYear { get; set; }
        public string DeletedDrivePrefix { get; set; }
        public string AllInInstitutionGroupNameStandard { get; set; }
        public string AllStudentsInInstitutionGroupNameStandard { get; set; }
        public string AllEmployeesInInstitutionGroupNameStandard { get; set; }
        public string GroupForEmployeeTypeNameStandard { get; set; }
        public string GroupForYearNameStandard { get; set; }
        public string ClassGroupNameStandard { get; set; }
        public string ClassGroupNameStandardNoClassYear { get; set; }
        public string ClassGroupOnlyStudentsNameStandard { get; set; }
        public string ClassGroupOnlyStudentsNameStandardNoClassYear { get; set; }
        public string GlobalEmployeeGroupName { get; set; }
        public string SchoolOUName { get; set; }
        public string DaycareOUName { get; set; }
        public string InstitutitonStaffGroupEmailTypeName { get; set; } = "staff";
        public bool OnlyUseYearInClassGroupEmail { get; set; } = false;
        public string SecurityGroupForLevelNameStandard { get; set; }
        public string GlobalSecurityGroupForLevelNameStandard { get; set; }

    }
}