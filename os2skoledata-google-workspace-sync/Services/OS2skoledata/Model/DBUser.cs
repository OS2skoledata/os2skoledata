
using Newtonsoft.Json;
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
        // TODO skal EXTERNAL håndteres anerledes end employee?
        public DBRole Role { get; set; }
        public List<string> GroupIds { get; set; }
        public List<Institution> Institutions { get; set; }
        public Institution CurrentInstitution { get; set; }

        // empty for all other than students
        public List<string> StudentMainGroups { get; set; }
        public List<string> StudentMainGroupsGoogleWorkspaceIds { get; set; }
        public StudentRole StudentRole { get; set; }
        public long StudentMainGroupStartYearForInstitution { get; set; }

        // empty for all other than employees
        public List<EmployeeRole> EmployeeRoles { get; set; }

        // empty for all other than externals
        public ExternalRole ExternalRole { get; set; }

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