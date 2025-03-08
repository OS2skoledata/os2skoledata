using os2skoledata_ad_sync.Services.PAM.Model;
using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text.Json;

namespace os2skoledata_ad_sync.Services.PowerShellRunner
{
    public class PAMService : ServiceBase<PAMService>
    {
        private readonly string cyberArkAppId;
        private readonly string cyberArkSafe;
        private readonly string cyberArkObject;
        private readonly string cyberArkAPI;
        public PAMService(IServiceProvider sp) : base(sp)
        {
            cyberArkAppId = settings.PAMSettings == null ? null : settings.PAMSettings.CyberArkAppId;
            cyberArkSafe = settings.PAMSettings == null ? null : settings.PAMSettings.CyberArkSafe;
            cyberArkObject = settings.PAMSettings == null ? null : settings.PAMSettings.CyberArkObject;
            cyberArkAPI = settings.PAMSettings == null ? null : settings.PAMSettings.CyberArkAPI;
        }

        public string GetApiKey()
        {
            string apiKey = null;
            HttpClient httpClient = GetHttpClient();
            var response = httpClient.GetAsync($"/AIMWebService/api/Accounts?AppID={cyberArkAppId}&Safe={cyberArkSafe}&Object={cyberArkObject}");
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();
            CyberArk cyberArk = JsonSerializer.Deserialize<CyberArk>(responseString.Result);

            if (cyberArk != null && cyberArk.Password != null)
            {
                apiKey = cyberArk.Password;
            }
            
            return apiKey;
        }

        private HttpClient GetHttpClient()
        {
            var httpClient = new HttpClient();
            httpClient.BaseAddress = new Uri(cyberArkAPI);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            return httpClient;
        }
    }
}
