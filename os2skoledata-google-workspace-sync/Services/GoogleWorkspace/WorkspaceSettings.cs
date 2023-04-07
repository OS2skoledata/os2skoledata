using os2skoledata_ad_sync.Services.GoogleWorkspace;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace
{
    public class WorkspaceSettings
    {
        public string ServiceAccountDataFilePath { get; set; }
        public string EmailAccountToImpersonate { get; set; }
        public string Domain { get; set; }
        public string RootOrgUnitPath { get; set; }
        public string[] OUsToAlwaysCreate { get; set; }
        public NamingSettings NamingSettings { get; set; }
        public FilteringSettings FilteringSettings { get; set; }
        public string SuspendedUsersOU { get; set; }
        public string DeletedOusOu { get; set; }
        public LicensingSettings LicensingSettings { get; set; }
    }

    public enum UsernameStandardType
    {
        AS_UNILOGIN,
        PREFIX_NAME_FIRST,
        PREFIX_NAME_LAST
    }
}
