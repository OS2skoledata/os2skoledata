namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class NamingSettings
    {
        public string EmployeeOUName { get; set; }
        public string StudentOUName { get; set; }
        public string StudentsWithoutGroupsOUName { get; set; }
        public string SecurityGroupOUName { get; set; }
        public string ClassOUNameStandard { get; set; }
        public string ClassOUNameStandardNoClassYear { get; set; }
        public string InstitutionOUNameStandard { get; set; }
        public string GlobalStudentSecurityGroupName { get; set; }
        public string GlobalEmployeeSecurityGroupName { get; set; }
        public string GlobalSecurityGroupForEmployeeTypeSchoolNameStandard { get; set; }
        public string GlobalSecurityGroupForEmployeeTypeDaycareNameStandard { get; set; }
        public string GlobalSecurityGroupForEmployeeTypeFUNameStandard { get; set; }
        public string AllInInstitutionSecurityGroupNameStandard { get; set; }
        public string AllStudentsInInstitutionSecurityGroupNameStandard { get; set; }
        public string AllEmployeesInInstitutionSecurityGroupNameStandard { get; set; }
        public string ClassSecurityGroupNameStandard { get; set; }
        public string ClassSecurityGroupNameStandardNoClassYear { get; set; }
        public string SecurityGroupForEmployeeTypeNameStandard {  get; set; }
        public string SecurityGroupForYearNameStandard { get; set; }
        public string SecurityGroupForLevelNameStandard { get; set; }
        public string SchoolOUName { get; set; }
        public string DaycareOUName { get; set; }
        public string FUOUName { get; set; }
        public string GlobalSecurityGroupForLevelNameStandard { get; set; }
    }
}