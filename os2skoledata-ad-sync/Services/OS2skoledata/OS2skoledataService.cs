using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Net;
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

        public List<Institution> GetInstitutions()
        {
            logger.LogInformation("Fetching institutions from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/institutions"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var institutionsArray = JsonConvert.DeserializeObject<Institution[]>(responseString.Result, jsonSerializerSettings);
            
            logger.LogInformation("Finshed fetching institutions from OS2skoledata");
            return new List<Institution>(institutionsArray);
        }

        public List<Group> GetClassesForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching groups from {institution.InstitutionName} in OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/groups?institutionNumber={institution.InstitutionNumber}"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var groupArray = JsonConvert.DeserializeObject<Group[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching groups from {institution.InstitutionName} in OS2skoledata");
            return new List<Group>(groupArray);
        }

        public List<User> GetUsersForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching people from {institution.InstitutionName} in OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/persons?institutionNumber={institution.InstitutionNumber}"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var userArray = JsonConvert.DeserializeObject<User[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching people from {institution.InstitutionName} in OS2skoledata");
            return new List<User>(userArray);
        }

        public void SetUsernameOnUser(string username, string id)
        {
            UsernameRequest request = new UsernameRequest();
            request.LocalPersonId = id;
            request.Username = username;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/person/username"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
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

        public long GetHead()
        {
            logger.LogInformation("Fetching head from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/head"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var head = JsonConvert.DeserializeObject<long>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("Finshed fetching head from OS2skoledata");
            return head;
        }

        public void SetHead(long head)
        {
            HeadRequest request = new HeadRequest();
            request.Head = head;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/head"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public List<ModificationHistory> GetChanges()
        {
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/changes"));
            response.Wait();
            if (response.Result.IsSuccessStatusCode)
            {
                var responseString = response.Result.Content.ReadAsStringAsync();
                responseString.Wait();
                var changeArray = JsonConvert.DeserializeObject<ModificationHistory[]>(responseString.Result, jsonSerializerSettings);

                return new List<ModificationHistory>(changeArray);
            } else
            {
                if (response.Result.StatusCode.Equals(HttpStatusCode.Conflict))
                {
                    throw new Exception("Do a full sync");
                } else
                {
                    throw new Exception("Failed to fetch changes from os2skoledata. Http: " + response.Result.StatusCode);
                }
            }
            
        }

        public List<Institution> GetChangedInstitutions(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri, $"api/changes/institutions"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();
            var changeArray = JsonConvert.DeserializeObject<Institution[]>(responseString.Result, jsonSerializerSettings);

            return new List<Institution>(changeArray);
        }

        public List<User> GetChangedUsers(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri, $"api/changes/persons"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();
            var changeArray = JsonConvert.DeserializeObject<User[]>(responseString.Result, jsonSerializerSettings);

            return new List<User>(changeArray);
        }

        public List<Group> GetChangedGroups(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;


            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri, $"api/changes/groups"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();
            var changeArray = JsonConvert.DeserializeObject<Group[]>(responseString.Result, jsonSerializerSettings);

            return new List<Group>(changeArray);
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
