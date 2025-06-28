using System.Collections.Generic;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class MiniGroup
    {
        public long DatabaseId { get; set; }
        public int StartYear { get; set; }
        public string InstitutionName { get; set; }
        public InstitutionType InstitutionType { get; set; }
        public string WorkspaceId { get; set; }
        public bool Primary { get; set; }
    }
}