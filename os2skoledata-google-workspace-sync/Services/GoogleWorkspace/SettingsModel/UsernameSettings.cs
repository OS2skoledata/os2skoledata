namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace.SettingsModel
{
    public class UsernameSettings
    {
        public UsernameStandardType UsernameStandard { get; set; }
        public string UsernamePrefix { get; set; }
    }

    public enum UsernameStandardType
    {
        FROM_STIL_OR_AS_UNILOGIN,
        AS_UNILOGIN,
        PREFIX_NAME_FIRST,
        PREFIX_NAME_LAST,
        UNIID,
        // numbers from 2-9 (0 and 1 is excluded) e.g. 222mad
        THREE_NUMBERS_THREE_CHARS_FROM_NAME
    }
}
