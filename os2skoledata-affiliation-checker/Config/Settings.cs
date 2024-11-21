using os2skoledata_affiliation_checker.Jobs;
using os2skoledata_affiliation_checker.Services.ActiveDirectory;
using os2skoledata_affiliation_checker.Services.OS2skoledata;
using os2skoledata_affiliation_checker.Services.Sync;

namespace os2skoledata_affiliation_checker.Config
{
    public class Settings
    {
        public JobSettings JobSettings { get; set; }
        public ActiveDirectorySettings ActiveDirectorySettings { get; set; }
        public OS2skoledataSettings OS2skoledataSettings { get; set; }
        public SyncSettings SyncSettings { get; set; }
    }
}
