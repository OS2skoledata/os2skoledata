namespace os2skoledata_ad_sync.Services.LogUploader
{
    public class LogUploaderSettings
    {
        public bool Enabled { get; set; }
        public string FileShareUrl { get; set; }
        // default log-bucket for OS2skoledata
        public string FileShareApiKey { get; set; }
    }
}