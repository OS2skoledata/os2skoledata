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

namespace os2skoledata_ad_sync.Services.OS2skoledata
{
    internal class OS2skoledataService : ServiceBase<OS2skoledataService>
    {
        private readonly Uri baseUri;
        private readonly string apiKey;
        private readonly bool dryRun;
        private readonly List<string> institutionWhitelist;
        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };
        public OS2skoledataService(IServiceProvider sp) : base(sp)
        {
            baseUri = new Uri(settings.OS2skoledataSettings.BaseUrl);
            dryRun = settings.OS2skoledataSettings.DryRun;
            institutionWhitelist = settings.OS2skoledataSettings.InstitutionWhitelist;

            var pamEnabled = settings.PAMSettings == null ? false : settings.PAMSettings.Enabled;
            if (pamEnabled)
            {
                PAMService pamService = sp.GetService<PAMService>();
                apiKey = pamService.GetApiKey();
            }
            else
            {
                apiKey = settings.OS2skoledataSettings.ApiKey;
            }
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
            var institutionList = new List<Institution>(institutionsArray);
            institutionList = RemoveInstitutionsNotInWhitelist(institutionList);
            
            logger.LogInformation("Finshed fetching institutions from OS2skoledata");
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

        public List<string> GetAllUsernames()
        {
            logger.LogInformation($"Fetching all usernames from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/usernames/all"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching all usernames OS2skoledata");
            return new List<string>(usernameArray);
        }

        public List<string> GetLockedUsernames()
        {
            logger.LogInformation($"Fetching locked usernames from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/locked/usernames"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

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

                using var httpClient = GetHttpClient();
                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = httpClient.PostAsync(new Uri(baseUri + "api/person/username"), content);
                response.Wait();
                response.Result.EnsureSuccessStatusCode();
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

                    using var httpClient = GetHttpClient();
                    var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                    var response = httpClient.PostAsync(new Uri(baseUri + "api/reporterror"), content);
                    response.Wait();
                    response.Result.EnsureSuccessStatusCode();
                } catch (Exception ex)
                {
                    logger.LogWarning("Could not report error to OS2skoledata backend: " + ex.Message);
                }
                
            }
        }

        public long GetHead()
        {
            logger.LogInformation("Fetching head from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/head"));
            response.Wait();
            if (response.Result.IsSuccessStatusCode)
            {
                var responseString = response.Result.Content.ReadAsStringAsync();
                responseString.Wait();

                var head = JsonConvert.DeserializeObject<long>(responseString.Result, jsonSerializerSettings);

                logger.LogInformation("Finshed fetching head from OS2skoledata");
                return head;
            }
            else
            {
                throw new Exception($"Failed to get head from OS2skoledata. HTTP: {response.Result.StatusCode}. Message: {response.Result.ReasonPhrase}");
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

                using var httpClient = GetHttpClient();
                var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
                var response = httpClient.PostAsync(new Uri(baseUri + "api/head/" + institutionNumber), content);
                response.Wait();
                response.Result.EnsureSuccessStatusCode();
            }
        }

        public List<ModificationHistory> GetChangesForInstitution(string institutionNumber)
        {
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/changes/" + institutionNumber));
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



        public bool GetShouldUploadLog()
        {
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/uploadLog"));
            response.Wait();
            if (response.Result.IsSuccessStatusCode)
            {
                var responseString = response.Result.Content.ReadAsStringAsync();
                responseString.Wait();

                return Boolean.Parse(responseString.Result);
            }

            return false;
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
