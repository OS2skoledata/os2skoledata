using os2skoledata_ad_sync.Jobs;
using os2skoledata_ad_sync.Services.OS2skoledata;

namespace os2skoledata_ad_sync.Config
{
    public class Settings
    {
        public JobSettings JobSettings { get; set; }
        public OS2skoledataSettings OS2skoledataSettings { get; set; }
        public DatabaseSettings databaseSettings { get; set; }
    }
}
