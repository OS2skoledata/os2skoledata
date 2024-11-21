namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class OptionalSecurityGroupFields
    {
        public bool SetSamAccountName { get; set; } = false;
        public string SamAccountPrefix { get; set; } = "OS2";
    }
}