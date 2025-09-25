using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class User
    {
        public long DatabaseId { get; set; }
        public string LocalPersonId { get; set; }
        public string Cpr { get; set; }
        public string Firstname { get; set; }
        public string FamilyName { get; set; }
        public string Username { get; set; }
        public string StilUsername { get; set; }
        public string UniId { get; set; }
        public Role Role { get; set; }
        public Role GlobalRole { get; set; }
        public List<long> GroupIds { get; set; }
        public List<Institution> Institutions { get; set; }
        public string CurrentInstitutionNumber { get; set; }
        public bool Deleted { get; set; }
        public List<string> TotalRoles { get; set; }
        public bool SetPasswordOnCreate { get; set; }
        public string Password { get; set; }
        public string ReservedUsername { get; set; }
        public Institution PrimaryInstitution { get; set; }
        public bool ApiOnly { get; set; }

        // empty for all other than students
        public List<string> StudentMainGroups { get; set; }
        public List<MiniGroup> StudentMainGroupsAsObjects { get; set; }
        public StudentRole StudentRole { get; set; }
        public StudentRole GlobalStudentRole { get; set; }
        public int StudentMainGroupStartYearForInstitution { get; set; }
        public string StudentMainGroupLevelForInstitution { get; set; }

        // empty for all other than employees
        public List<EmployeeRole> EmployeeRoles { get; set; }
        public List<EmployeeRole> GlobalEmployeeRoles { get; set; }

        // empty for all other than externals
        public ExternalRole ExternalRole { get; set; }
        public ExternalRole GlobalExternalRole { get; set; }


        // not from the api, only for optimizing the sync - is used to determine if a user should be added to the group - will e.g. be null if the user is excluded
        public string ADPath { get; set; }
    }

    public enum Role
    {
        STUDENT,
        EMPLOYEE,
        EXTERNAL
    }

    public enum StudentRole
    {
        BARN,
        ELEV,
        STUDERENDE,
        UNKNOWN
    }

    public enum EmployeeRole
    {
        LÆRER,
        PÆDAGOG,
        VIKAR,
        LEDER,
        LEDELSE,
        TAP,
        KONSULENT,
        UNKNOWN
    }

    public enum ExternalRole
    {
        PRAKTIKANT,
        EKSTERN,
        UNKNOWN
    }
}