
using Newtonsoft.Json;
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
        public Role Role { get; set; }
        public List<long> GroupIds { get; set; }
        public List<Institution> Institutions { get; set; }
        public string CurrentInstitutionNumber { get; set; }

        // empty for all other than students
        public List<string> StudentMainGroups { get; set; }
        public StudentRole StudentRole { get; set; }
        public int StudentMainGroupStartYearForInstitution { get; set; }

        // empty for all other than employees
        public List<EmployeeRole> EmployeeRoles { get; set; }

        // empty for all other than externals
        public ExternalRole ExternalRole { get; set; }


        // not from the api, only for optimizing the sync
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