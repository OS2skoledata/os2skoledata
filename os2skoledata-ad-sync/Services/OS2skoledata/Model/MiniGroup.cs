namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class MiniGroup
    {
        public long DatabaseId { get; set; }
        public int StartYear { get; set; }
        public string InstitutionName { get; set; }
        public InstitutionType InstitutionType { get; set; }
        public bool Primary { get; set; }
    }
}