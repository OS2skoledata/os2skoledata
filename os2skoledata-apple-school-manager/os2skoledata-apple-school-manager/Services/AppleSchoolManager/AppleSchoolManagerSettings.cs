using os2skoledata_apple_school_manager.Services.AppleSchoolManager.SettingsModel;

namespace os2skoledata_apple_school_manager.Services.AppleSchoolManager
{
    public class AppleSchoolManagerSettings
    {
        public string Domain { get; set; }
        public string ZipFileName { get; set; } = "os2skoledata_apple_school_manager_upload.zip";
        public SFTP SFTP { get; set; }
        public bool MoreThanThreeInstructors { get; set; } = false;

    }
}
