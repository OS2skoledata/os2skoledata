
namespace os2skoledata_google_workspace_sync.Jobs
{
    public class JobSettings
    {
        public string FullSyncCron { get; set; } = "0 43 2 * * ? *";
        public string DeltaSyncCron { get; set; } = "0 0/5 * * * ? *";
    }
}
