namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class SetFieldRequest
    {
        public long Id { get; set; }
        public string EntityType { get; set; }
        public string FieldType { get; set; }
        public string Value { get; set; }
    }

    public enum SetFieldType
    {
        NONE,
        GROUP_WORKSPACE_ID,
        GROUP_DRIVE_WORKSPACE_ID,
        GROUP_GROUP_WORKSPACE_EMAIL,
        GROUP_ONLY_STUDENTS_GROUP_WORKSPACE_EMAIL,
        INSTITUTION_WORKSPACE_ID,
        INSTITUTION_STUDENT_WORKSPACE_ID,
        INSTITUTION_EMPLOYEE_WORKSPACE_ID,
        INSTITUTION_ALL_DRIVE_WORKSPACE_ID,
        INSTITUTION_STUDENT_DRIVE_WORKSPACE_ID,
        INSTITUTION_EMPLOYEE_DRIVE_WORKSPACE_ID,
        INSTITUTION_EMPLOYEE_GROUP_WORKSPACE_EMAIL
    }
}
