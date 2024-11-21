namespace os2skoledata_affiliation_checker.Services.Sync
{
    public class SyncSettings
    {
        public FetchFrom FetchDataFrom { get; set; }
        public string OpusWagesFile { get; set; }
        public string SQLConnectionString { get; set; }
        public string SQLStatement { get; set; }
        public string SOFDBaseUrl { get; set; }
        public string SOFDApiKey { get; set; }
    }

    public enum FetchFrom
    {
        OPUS,
        SQL,
        SOFD
    }
}
