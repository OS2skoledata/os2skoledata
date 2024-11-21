using System;

namespace os2skoledata_affiliation_checker.Services.Sofd.Model
{
    public class Affiliation
    {
        public string Uuid { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? StopDate { get; set; }
        public String EmployeeId { get; set; }
    }
}
