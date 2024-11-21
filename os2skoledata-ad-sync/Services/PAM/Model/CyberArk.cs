using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace os2skoledata_ad_sync.Services.PAM.Model
{
    public class CyberArk
    {
        [JsonPropertyName("Content")]
        public string? Password { get; init; }
    }
}
