using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class FilteringSettings
    {
        public string[] GloballyExcludedRoles { get; set; }
        public Dictionary<string, string[]> ExludedRolesInInstitution { get; set; }
    }
}