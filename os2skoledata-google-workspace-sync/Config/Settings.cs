using os2skoledata_google_workspace_sync.Jobs;
using os2skoledata_google_workspace_sync.Services.GoogleWorkspace;
using os2skoledata_google_workspace_sync.Services.OS2skoledata;

namespace os2skoledata_google_workspace_sync.Config
{
    public class Settings
    {
        public JobSettings JobSettings { get; set; }
        public WorkspaceSettings WorkspaceSettings { get; set; }
        public OS2skoledataSettings OS2skoledataSettings { get; set; }
    }
}
