using os2skoledata_apple_school_manager.Jobs;
using os2skoledata_apple_school_manager.Services.AppleSchoolManager;
using os2skoledata_apple_school_manager.Services.OS2skoledata;

namespace os2skoledata_apple_school_manager.Config
{
    public class Settings
    {
        public JobSettings JobSettings { get; set; }
        public AppleSchoolManagerSettings AppleSchoolManagerSettings { get; set; }
        public OS2skoledataSettings OS2skoledataSettings { get; set; }
        public bool DryRun { get; set; }
        public string DryRunFilePath { get; set; }
    }
}
