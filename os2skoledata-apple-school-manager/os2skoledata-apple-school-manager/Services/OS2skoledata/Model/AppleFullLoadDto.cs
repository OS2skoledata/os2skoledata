using System.Collections.Generic;

namespace os2skoledata_apple_school_manager.Services.OS2skoledata.Model
{
    public class AppleFullLoadDto
    {
        public List<AppleInstitutionDto> Institutions { get; set; }
        public List<AppleGroupDto> Groups { get; set; }
        public List<AppleUserDto> Students { get; set; }
        public List<AppleUserDto> Staff { get; set; }
        public List<AppleRosterDto> Rosters { get; set; }
    }

}