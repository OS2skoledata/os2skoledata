using Newtonsoft.Json;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace.Model
{
    public class ServiceAccountCredentialDTO
    {
        [JsonProperty("client_email")]
        public string ClientEmail { get; set; }
        [JsonProperty("private_key")]
        public string PrivateKey { get; set; }
        [JsonProperty("private_key_id")]
        public string KeyId { get; set; }

    }
}