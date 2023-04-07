namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class SetGroupEmailRequest
    {
        public long InstitutionId { get; set; }
        public string GroupKey { get; set; }
        public string GroupEmail { get; set; }
    }
}
