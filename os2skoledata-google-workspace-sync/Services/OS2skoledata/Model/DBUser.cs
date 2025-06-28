using System.Collections.Generic;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class DBUser
    {
        public long DatabaseId { get; set; }
        public string LocalPersonId { get; set; }
        public string Cpr { get; set; }
        public string Firstname { get; set; }
        public string FamilyName { get; set; }
        public string Username { get; set; }
        public DBRole Role { get; set; }
        public DBRole GlobalRole { get; set; }
        public List<string> GroupIds { get; set; }
        public List<Institution> Institutions { get; set; }
        public Institution CurrentInstitution { get; set; }
        public bool Deleted;
        public List<string> TotalRoles { get; set; }
        public string StilUsername { get; set; }
        public string UniId { get; set; }
        public List<DBContactCard> ContactCards { get; set; }
        public bool SetPasswordOnCreate { get; set; }
        public string Password { get; set; }
        public string ReservedUsername { get; set; }
        public Institution PrimaryInstitution { get; set; }

        // empty for all other than students
        public List<string> StudentMainGroups { get; set; }
        public List<string> StudentMainGroupsGoogleWorkspaceIds { get; set; }
        public List<MiniGroup> StudentMainGroupsAsObjects { get; set; }
        public StudentRole StudentRole { get; set; }
        public StudentRole GlobalStudentRole { get; set; }
        public long StudentMainGroupStartYearForInstitution { get; set; }
        public string StudentMainGroupLevelForInstitution { get; set; }

        // empty for all other than employees
        public List<EmployeeRole> EmployeeRoles { get; set; }
        public List<EmployeeRole> GlobalEmployeeRoles { get; set; }

        // empty for all other than externals
        public ExternalRole ExternalRole { get; set; }
        public ExternalRole GlobalExternalRole { get; set; }

        // not from api, only for keeping track of members of security groups
        public bool IsExcluded {get; set; } = false;
    }

    public enum DBRole
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