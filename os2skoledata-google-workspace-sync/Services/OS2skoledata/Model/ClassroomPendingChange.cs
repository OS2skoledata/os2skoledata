namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class ClassroomPendingChange
    {
        public long Id { get; set; }
        public string CourseId { get; set; }
        public string Username { get; set; }
        public ClassroomAction Action { get; set; }
    }

    public enum ClassroomAction
    {
        TRANSFER,
        ARCHIVE
    }
}
