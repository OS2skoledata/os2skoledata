using System.Collections.Generic;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class FolderOrGroupDTO
    {
        public string GoogleWorkspaceId { get; set; }
        public FolderOrGroup type { get; set; }
    }

    public enum FolderOrGroup
    {
        GROUP,
        FOLDER
    }
}