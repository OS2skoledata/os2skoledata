using System;
using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.OS2skoledata
{
    public class OS2skoledataSettings
    {
        public string ApiKey { get; set; }
        public string BaseUrl { get; set; }
        public List<string> InstitutionWhitelist { get; set; }

        // means that we only call the GET endpoints
        public bool DryRun { get; set; }
    }
}
