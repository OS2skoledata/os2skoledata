using os2skoledata_ad_sync.Jobs;
using os2skoledata_ad_sync.Services.ActiveDirectory;
using os2skoledata_ad_sync.Services.OS2skoledata;
using os2skoledata_ad_sync.Services.PowerShellRunner;

namespace os2skoledata_ad_sync.Config
{
    public class Settings
    {
        public JobSettings JobSettings { get; set; }
        public ActiveDirectorySettings ActiveDirectorySettings { get; set; }
        public OS2skoledataSettings OS2skoledataSettings { get; set; }
        public PowerShellSettings PowerShellSettings { get; set; }
    }
}
