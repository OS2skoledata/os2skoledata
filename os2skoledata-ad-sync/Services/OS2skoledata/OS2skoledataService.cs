using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using os2skoledata_ad_sync.Services.PowerShellRunner;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading;

namespace os2skoledata_ad_sync.Services.OS2skoledata
{
    internal class OS2skoledataService : ServiceBase<OS2skoledataService>
    {
        private readonly Uri baseUri;
        private readonly string apiKey;
        private readonly bool dryRun;
        private readonly List<string> institutionWhitelist;
        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };
        private readonly HttpClient httpClient;
        public OS2skoledataService(IServiceProvider sp, HttpClient httpClient) : base(sp)
        {
            baseUri = new Uri(settings.OS2skoledataSettings.BaseUrl);
            dryRun = settings.OS2skoledataSettings.DryRun;
            institutionWhitelist = settings.OS2skoledataSettings.InstitutionWhitelist;

            this.httpClient = httpClient;
        }

        private string GetApiKeyFromSettings(IServiceProvider sp)
        {
           var pamEnabled = settings.PAMSettings == null ? false : settings.PAMSettings.Enabled;
            if (pamEnabled) 
            {
                PAMService pamService = sp.GetService<PAMService>();
                return pamService.GetApiKey();
            }
            else
            {
                return settings.OS2skoledataSettings.ApiKey;
            }
        }

        public List<Institution> GetInstitutions()
        {
            logger.LogInformation("Fetching institutions from OS2skoledata");

            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, "api/institutions")).Result);

            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var institutionsArray = JsonConvert.DeserializeObject<Institution[]>(responseString, jsonSerializerSettings);
            var institutionList = new List<Institution>(institutionsArray);
            institutionList = RemoveInstitutionsNotInWhitelist(institutionList);

            logger.LogInformation("Finished fetching institutions from OS2skoledata");
            return institutionList;
        }

        private List<Institution> RemoveInstitutionsNotInWhitelist(List<Institution> institutionList)
        {
            if (institutionWhitelist == null || institutionWhitelist.Count == 0)
            {
                return institutionList;
            }

            List<Institution> whitelistedInstitutions = new List<Institution>();
            foreach (Institution institution in institutionList)
            {
                if (institutionWhitelist.Contains(institution.InstitutionNumber))
                {
                    whitelistedInstitutions.Add(institution);
                }
            }

            return whitelistedInstitutions;
        }

        public List<Group> GetClassesForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching groups from {institution.InstitutionName} in OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/groups?institutionNumber={institution.InstitutionNumber}")).Result);
            
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var groupArray = JsonConvert.DeserializeObject<Group[]>(responseString, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching groups from {institution.InstitutionName} in OS2skoledata");
            return new List<Group>(groupArray);
        }

        public List<User> GetUsersForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching people from {institution.InstitutionName} in OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/persons?institutionNumber={institution.InstitutionNumber}")).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var userArray = JsonConvert.DeserializeObject<User[]>(responseString, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching people from {institution.InstitutionName} in OS2skoledata");
            return new List<User>(userArray);
        }

        public List<string> GetAllUsernames()
        {
            logger.LogInformation($"Fetching all usernames from OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/usernames/all")).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching all usernames OS2skoledata");
            return new List<string>(usernameArray);
        }

        public List<string> GetLockedUsernames()
        {
            logger.LogInformation($"Fetching locked usernames from OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/locked/usernames")).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching all locked usernames from OS2skoledata");
            return new List<string>(usernameArray);
        }

        public void SetUsernameOnUser(string username, long id)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have sat username ${username} on user with id {id} on user in OS2skoledata");
            } 
            else {
                UsernameRequest request = new UsernameRequest();
                request.PersonDatabaseId = id;
                request.Username = username;

                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri + "api/person/username"), content).Result);
                response.EnsureSuccessStatusCode();
            }
        }

        public void ReportError(string errorMsg)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have reported error with message ${errorMsg} to OS2skoledata");
            }
            else
            {
                try
                {
                    ErrorRequest request = new ErrorRequest();
                    request.Message = errorMsg;

                    var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                    var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri + "api/reporterror"), content).Result);
                    response.EnsureSuccessStatusCode();
                } catch (Exception ex)
                {
                    logger.LogWarning("Could not report error to OS2skoledata backend: " + ex.Message);
                }
                
            }
        }

        public long GetHead()
        {
            logger.LogInformation("Fetching head from OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, "api/head")).Result);
            if (response.IsSuccessStatusCode)
            {
                var responseString = response.Content.ReadAsStringAsync().Result;

                var head = JsonConvert.DeserializeObject<long>(responseString, jsonSerializerSettings);

                logger.LogInformation("Finshed fetching head from OS2skoledata");
                return head;
            }
            else
            {
                throw new Exception($"Failed to get head from OS2skoledata. HTTP: {response.StatusCode}. Message: {response.ReasonPhrase}");
            }
        }

        public void SetHead(long head, string institutionNumber)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have sat head to ${head} in OS2skoledata");
            }
            else
            {
                HeadRequest request = new HeadRequest();
                request.Head = head;

                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri + "api/head/" + institutionNumber), content).Result);
                response.EnsureSuccessStatusCode();
            }
        }

        public List<ModificationHistory> GetChangesForInstitution(string institutionNumber)
        {
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/changes/" + institutionNumber)).Result);
            if (response.IsSuccessStatusCode)
            {
                var responseString = response.Content.ReadAsStringAsync().Result;
                var changeArray = JsonConvert.DeserializeObject<ModificationHistory[]>(responseString, jsonSerializerSettings);

                return new List<ModificationHistory>(changeArray);
            } else
            {
                if (response.StatusCode.Equals(HttpStatusCode.Conflict))
                {
                    throw new Exception("Do a full sync");
                } else
                {
                    throw new Exception("Failed to fetch changes from os2skoledata. Http: " + response.StatusCode);
                }
            }
            
        }

        public List<Institution> GetChangedInstitutions(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;

            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri, $"api/changes/institutions"), content).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;
            var changeArray = JsonConvert.DeserializeObject<Institution[]>(responseString, jsonSerializerSettings);

            return new List<Institution>(changeArray);
        }

        public List<User> GetChangedUsers(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;

            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri, $"api/changes/persons"), content).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;
            var changeArray = JsonConvert.DeserializeObject<User[]>(responseString, jsonSerializerSettings);

            return new List<User>(changeArray);
        }

        public List<Group> GetChangedGroups(List<long> ids)
        {
            ChangeRequest request = new ChangeRequest();
            request.Ids = ids;

            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri, $"api/changes/groups"), content).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;
            var changeArray = JsonConvert.DeserializeObject<Group[]>(responseString, jsonSerializerSettings);

            return new List<Group>(changeArray);
        }



        public bool GetShouldUploadLog()
        {
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, "api/uploadLog")).Result);
            if (response.IsSuccessStatusCode)
            {
                var responseString = response.Content.ReadAsStringAsync().Result;

                return Boolean.Parse(responseString);
            }

            return false;
        }

        public void SetActionOnUser(string username, ActionType action)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have sat action ${action} on user(s) with username ${username} in OS2skoledata");
            }
            else
            {
                ActionRequest request = new ActionRequest();
                request.Action = action;
                request.Username = username;

                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = SendWithRetry(() => httpClient.PostAsync(new Uri(baseUri + "api/person/action"), content).Result);
                response.EnsureSuccessStatusCode();
            }
        }

        public List<string> GetKeepAliveUsernames()
        {
            logger.LogInformation($"Fetching keep alive usernames from OS2skoledata");
            var response = SendWithRetry(() => httpClient.GetAsync(new Uri(baseUri, $"api/keepalive")).Result);
            response.EnsureSuccessStatusCode();
            var responseString = response.Content.ReadAsStringAsync().Result;

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching keep alive usernames OS2skoledata");
            return new List<string>(usernameArray);
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

        private HttpResponseMessage SendWithRetry(Func<HttpResponseMessage> sendRequest, int maxRetries = 3)
        {
            int retryCount = 0;
            while (true)
            {
                try
                {
                    return sendRequest();
                }
                catch (HttpRequestException ex) when (retryCount < maxRetries)
                {
                    retryCount++;
                    logger.LogWarning($"HTTP-call failed ({retryCount}/{maxRetries}): {ex.Message}. Trying again...");
                    Thread.Sleep(1000 * retryCount);
                }
            }
        }
    }  
}
