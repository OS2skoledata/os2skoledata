using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_apple_school_manager.Services.OS2skoledata.Model;
using System;
using System.Net.Http;
using System.Text;

namespace os2skoledata_apple_school_manager.Services.OS2skoledata
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

        public AppleFullLoadDto GetFullLoad()
        {
            logger.LogInformation("Fetching full load from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/apple/full"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            AppleFullLoadDto dto = JsonConvert.DeserializeObject<AppleFullLoadDto>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching full load from OS2skoledata");
            return dto;
        }

        public void ReportError(string errorMsg)
        {
            ErrorRequest request = new ErrorRequest();
            request.Message = errorMsg;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/reporterror"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public void SetLastFullSync()
        {
            using var httpClient = GetHttpClient();

            var timestamp = DateTime.Now;
            var formatted = timestamp.ToString("yyyy-MM-dd'T'HH:mm:ss");

            var content = new StringContent(JsonConvert.SerializeObject(formatted), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/lastsynced"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
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
