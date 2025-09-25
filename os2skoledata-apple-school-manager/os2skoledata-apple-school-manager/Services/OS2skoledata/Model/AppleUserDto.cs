namespace os2skoledata_apple_school_manager.Services.OS2skoledata.Model
{
    public class AppleUserDto
    {
        public PersonRole Type { get; set; }
        public string UniId { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Username { get; set; }
        public long PrimaryInstitutionId { get; set; }
        public int? Level { get; set; }
    }

    public enum PersonRole
    {
        Student,
        Employee
    }

}