using System.Collections.Generic;

namespace os2skoledata_apple_school_manager.Services.OS2skoledata.Model
{
    public class AppleGroupDto
    {
        public long Id { get; set; }
        public string StilId { get; set; }
        public string GroupName { get; set; }
        public long InstitutionId { get; set; }
        public HashSet<string> TeacherUniIds { get; set; }
    }

}