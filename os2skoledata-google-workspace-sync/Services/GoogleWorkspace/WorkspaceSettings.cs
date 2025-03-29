using os2skoledata_ad_sync.Services.GoogleWorkspace;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace.SettingsModel;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace
{
    public class WorkspaceSettings
    {
        public string ServiceAccountDataFilePath { get; set; }
        public string EmailAccountToImpersonate { get; set; }
        public string Domain { get; set; }
        public string RootOrgUnitPath { get; set; }
        public string KeepAliveOU { get; set; }
        public string[] OUsToAlwaysCreate { get; set; }
        public HierarchyType HierarchyType { get; set; } = HierarchyType.INSTITUTION_FIRST;
        public NamingSettings NamingSettings { get; set; }
        public FilteringSettings FilteringSettings { get; set; }
        public string SuspendedUsersOU { get; set; }
        public string DeletedOusOu { get; set; }
        public LicensingSettings LicensingSettings { get; set; }
        public bool UserDryRun { get; set; }
        public string[] RolesToBeCreatedDirectlyInGW { get; set; }
        public UsernameSettings usernameSettings { get; set; }
        public bool UseDanishCharacters { get; set; }
        public bool DeleteDisabledUsersFully {  get; set; } = false;
        public int DaysBeforeDeletionStudent { get; set; } = 60;
        public int DaysBeforeDeletionEmployee { get; set; } = 60;
        public int DaysBeforeDeletionExternal { get; set; } = 60;
        public bool GWTraceLog { get; set; } = false;
        public bool SetContactCard { get; set; } = false;
        public string AddEmployeesToClassroomGroup { get; set; }
        public ClassroomSettings ClassroomSettings { get; set; } = new ClassroomSettings();
    }

    public enum HierarchyType
    {
        INSTITUTION_FIRST,
        INSTITUTION_LAST
    }
}
