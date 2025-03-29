namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class SetClassroomStatusRequest
    {
        public long Id { get; set; }
        public ClassroomActionStatus Status { get; set; }
        public string ErrorMessage { get; set; }
    }

    public enum ClassroomActionStatus
    {
        WAITING,
        DONE,
        FAILED
    }
}
