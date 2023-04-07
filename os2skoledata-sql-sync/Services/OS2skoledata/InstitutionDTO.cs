using Newtonsoft.Json;

namespace os2skoledata_sql_sync.Services.OS2skoledata
{
    public class InstitutionReadDTO
    {
        public long DatabaseId { get; set; }
        [JsonProperty("number")]
        public string InstitutionNumber { get; set; }
        [JsonProperty("name")]
        public string InstitutionName { get; set; }
    }
}
