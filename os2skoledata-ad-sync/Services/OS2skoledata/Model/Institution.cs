using Newtonsoft.Json;
using System;

namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class Institution
    {
        public long DatabaseId { get; set; }
        [JsonProperty("number")]
        public string InstitutionNumber { get; set; }
        [JsonProperty("name")]
        public string InstitutionName { get; set; }
        public string Abbreviation { get; set; }
        public InstitutionType Type { get; set; }
        public bool Locked { get; set; }
    }

    public enum InstitutionType
    {
        SCHOOL,
        DAYCARE,
        MUNICIPALITY,
        FU
    }
}