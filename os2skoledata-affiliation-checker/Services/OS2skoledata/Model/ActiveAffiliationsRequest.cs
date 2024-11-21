using System;

namespace os2skoledata_affiliation_checker.Services.OS2skoledata.Model
{
    public class ActiveAffiliationsRequest
    {
        public string Cpr { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? StopDate { get; set; }
        public string EmployeeNumber { get; set; }
    }
}