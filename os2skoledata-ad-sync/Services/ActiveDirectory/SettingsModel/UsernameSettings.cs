namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class UsernameSettings
    {
        public UsernameStandardType UsernameStandard { get; set; }
        public string UsernamePrefix { get; set; }
        public int RandomStandardLetterCount { get; set; } = 4;
        public int RandomStandardNumberCount { get; set; } = 4;
    }
}