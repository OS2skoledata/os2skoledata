using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.ActiveDirectory.Model
{
    public class UsernameInstitutionDTO
    {
        public string Username { get; set; }
        public List<string> institutionNumbers { get; set; }

    }
}