namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class Group
    {
        public long DatabaseId { get; set; }
        public string GroupName { get; set; }
        public string GroupId { get; set; }
        public string GroupLevel { get; set; }
        public string Line { get; set; }
        public string InstitutionNumber { get; set; }
        public string InstitutionName { get; set; }
        public string InstitutionAbbreviation { get; set; }
        public bool InstitutionLocked { get; set; }
        public int StartYear { get; set; }
        public DBGroupType GroupType {get; set; }
    }

    public enum DBGroupType
    {
        HOVEDGRUPPE, 
        ÅRGANG, 
        RETNING, 
        HOLD, 
        SFO, 
        TEAM, 
        ANDET, 
        UNKNOWN
    }
}