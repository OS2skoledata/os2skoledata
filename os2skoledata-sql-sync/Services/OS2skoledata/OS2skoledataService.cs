using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;

namespace os2skoledata_ad_sync.Services.OS2skoledata
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

        public List<InstitutionDTO> GetEverything()
        {
            List<InstitutionReadDTO> institutions = GetInstitutions();
            
            logger.LogDebug("Fetching everyting from OS2skoledata");

            using var httpClient = GetHttpClient();

            var result = new List<InstitutionDTO>();
            foreach (InstitutionReadDTO institutionDTO in institutions)
            {
                var response = httpClient.GetAsync(new Uri(baseUri, "api/full/" + institutionDTO.InstitutionNumber));
                response.Wait();
                response.Result.EnsureSuccessStatusCode();
                var responseString = response.Result.Content.ReadAsStringAsync();
                responseString.Wait();
                
                var institution = JsonConvert.DeserializeObject<InstitutionDTO>(responseString.Result, jsonSerializerSettings);

                result.Add(institution);
            }
            logger.LogDebug("Finshed fetching everyting from OS2skoledata");

            return result;
        }

        public List<InstitutionReadDTO> GetInstitutions()
        {
            logger.LogDebug("Fetching institutions from OS2skoledata");
            using var httpClient = GetHttpClient();

            var response = httpClient.GetAsync(new Uri(baseUri, "api/institutions/"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var institutionsArray = JsonConvert.DeserializeObject<InstitutionReadDTO[]>(responseString.Result, jsonSerializerSettings);

            logger.LogDebug("Finshed fetching institutions from OS2skoledata");

            return new List<InstitutionReadDTO>(institutionsArray);
        }

        public void ReportError(string errorMsg)
        {
            try
            {
                ErrorRequest request = new ErrorRequest();
                request.Message = errorMsg;

                using var httpClient = GetHttpClient();
                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = httpClient.PostAsync(new Uri(baseUri + "api/reporterror"), content).Result;
                response.EnsureSuccessStatusCode();
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
