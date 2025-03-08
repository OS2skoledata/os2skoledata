namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class ActionRequest
    {
        public string Username { get; set; }
        public ActionType Action { get; set; }
        public string IntegrationType { get; set; } = "GW";
    }

    public enum ActionType
    {
        CREATE,
        DEACTIVATE,
        REACTIVATE
    }
}
