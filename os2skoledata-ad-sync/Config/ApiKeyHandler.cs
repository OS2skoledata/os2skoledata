using os2skoledata_ad_sync.Services.PowerShellRunner;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace os2skoledata_ad_sync.Config
{
    public class ApiKeyHandler : DelegatingHandler
    {
        private readonly Settings settings;
        private readonly PAMService pamService;

        public ApiKeyHandler(Settings settings, PAMService pamService)
        {
            this.settings = settings;
            this.pamService = pamService;
        }

        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
        {
            string apiKey = settings.PAMSettings?.Enabled == true ? pamService.GetApiKey() : settings.OS2skoledataSettings.ApiKey;

            if (!string.IsNullOrEmpty(apiKey))
            {
                request.Headers.Add("ApiKey", apiKey);
            }

            return await base.SendAsync(request, cancellationToken);
        }
    }
}
