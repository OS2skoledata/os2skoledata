using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using os2skoledata_affiliation_checker.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;

namespace os2skoledata_affiliation_checker.Services.OS2skoledata
{
    internal class OS2skoledataService : ServiceBase<OS2skoledataService>
    {
        private readonly Uri baseUri;
        private readonly string apiKey;
        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };

        public OS2skoledataService(IServiceProvider sp) : base(sp)
        {
            baseUri = new Uri(settings.OS2skoledataSettings.BaseUrl);
            apiKey = settings.OS2skoledataSettings.ApiKey;
        }

        public void SendAffiliations(List<ActiveAffiliationsRequest> activeAffiliations)
        {
            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(activeAffiliations.ToArray(), getSerializerSettings()), Encoding.UTF8, "application/json");
            var uri = new Uri(baseUri + "api/affiliations/fullload");
            Console.WriteLine(uri.ToString());
            var response = httpClient.PostAsync(new Uri(baseUri + "api/affiliations/fullload"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public void ReportError(string errorMsg)
        {
            try
            {
                ErrorRequest request = new ErrorRequest();
                request.Message = errorMsg;

                using var httpClient = GetHttpClient();
                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = httpClient.PostAsync(new Uri(baseUri + "api/reporterror"), content);
                response.Wait();
                response.Result.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                logger.LogWarning("Could not report error to OS2skoledata backend: " + ex.Message);
            }
        }

        private HttpClient GetHttpClient()
        {
            var handler = new HttpClientHandler();
            handler.ClientCertificateOptions = ClientCertificateOption.Manual;
            handler.ServerCertificateCustomValidationCallback =
                (httpRequestMessage, cert, cetChain, policyErrors) =>
                {
                    return true;
                };

            var httpClient = new HttpClient(handler);
            httpClient.DefaultRequestHeaders.Add("ApiKey", apiKey);
            return httpClient;
        }
        private JsonSerializerSettings getSerializerSettings()
        {
            // api requires camel case
            return new JsonSerializerSettings
            {
                ContractResolver = new DefaultContractResolver
                {
                    NamingStrategy = new CamelCaseNamingStrategy()
                }
            };
        }
    }
}
