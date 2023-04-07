using System;

namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class Group
    {
        public long DatabaseId { get; set; }
        public string GroupName { get; set; }
        public string GroupId { get; set; }
        public string GroupLevel { get; set; }
        public string InstitutionNumber { get; set; }
        public string InstitutionName { get; set; }
        public int StartYear { get; set; }
    }
}