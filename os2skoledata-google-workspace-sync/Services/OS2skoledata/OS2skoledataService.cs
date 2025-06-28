using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using os2skoledata_google_workspace_sync.Services.OS2skoledata.Model;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata
{
    internal class OS2skoledataService : ServiceBase<OS2skoledataService>
    {
        private readonly Uri baseUri;
        private readonly string apiKey;
        private readonly bool userDryRun;
        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };

        public OS2skoledataService(IServiceProvider sp) : base(sp)
        {
            baseUri = new Uri(settings.OS2skoledataSettings.BaseUrl);
            apiKey = settings.OS2skoledataSettings.ApiKey;
            userDryRun = settings.WorkspaceSettings.UserDryRun;
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

            logger.LogInformation("finished fetching institutions from OS2skoledata");
            return new List<Institution>(institutionsArray);
        }

        public List<DBGroup> GetClassesForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching groups from {institution.InstitutionName} in OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/groups?institutionNumber={institution.InstitutionNumber}"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var groupArray = JsonConvert.DeserializeObject<DBGroup[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"finished fetching groups from {institution.InstitutionName} in OS2skoledata");
            return new List<DBGroup>(groupArray);
        }

        public List<DBUser> GetUsersForInstitution(Institution institution)
        {
            logger.LogInformation($"Fetching people from {institution.InstitutionName} in OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/persons?institutionNumber={institution.InstitutionNumber}"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var userArray = JsonConvert.DeserializeObject<DBUser[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"finished fetching people from {institution.InstitutionName} in OS2skoledata");
            return new List<DBUser>(userArray);
        }

        public void SetFields(long id, EntityType type, SetFieldType fieldType, string value)
        {
            SetFieldRequest request = new SetFieldRequest();
            request.Id = id;
            request.EntityType = type.ToString();
            request.FieldType = fieldType.ToString();
            request.Value = value;


            string json = JsonConvert.SerializeObject(request, getSerializerSettings());
            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/setfield"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public void SetGroupEmail(long institutionId, string groupKey, string email)
        {
            SetGroupEmailRequest request = new SetGroupEmailRequest();
            request.InstitutionId = institutionId;
            request.GroupKey = groupKey;
            request.GroupEmail = email;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/setgroupemail?integration=GW"), content);
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

            logger.LogInformation("finished fetching head from OS2skoledata");
            return head;
        }

        public void SetHead(long head, string institutionNumber)
        {
            if (userDryRun)
            {
                logger.LogInformation($"DryRun: would have sat head {head} for institution with number {institutionNumber} in OS2skoledata");
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
            }
            else
            {
                if (response.Result.StatusCode.Equals(HttpStatusCode.Conflict))
                {
                    throw new Exception("Do a full sync");
                }
                else
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

        public List<DBUser> GetChangedUsers(List<long> ids)
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
            var changeArray = JsonConvert.DeserializeObject<DBUser[]>(responseString.Result, jsonSerializerSettings);

            return new List<DBUser>(changeArray);
        }

        public List<DBGroup> GetChangedGroups(List<long> ids)
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
            var changeArray = JsonConvert.DeserializeObject<DBGroup[]>(responseString.Result, jsonSerializerSettings);

            return new List<DBGroup>(changeArray);
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

            var stringArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"finished locked usernames from OS2skoledata");
            return new List<string>(stringArray);
        }

        public List<string> GetLockedGroupEmails()
        {
            logger.LogInformation($"Fetching locked group emails from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/locked/groups/workspace"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var stringArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"finished locked group emails from OS2skoledata");
            return new List<string>(stringArray);
        }

        public List<string> GetLockedDriveIds()
        {
            logger.LogInformation($"Fetching locked drive ids from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/locked/drives/workspace"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var stringArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"finished locked drive ids from OS2skoledata");
            return new List<string>(stringArray);
        }

        public void SetUsernameOnUser(string username, long id)
        {
            UsernameRequest request = new UsernameRequest();
            request.PersonDatabaseId = id;
            request.Username = username;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/person/username"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
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

            logger.LogInformation($"finished fetching all usernames OS2skoledata");
            return new List<string>(usernameArray);
        }

        public void SetActionOnUser(string username, ActionType action)
        {
            ActionRequest request = new ActionRequest();
            request.Action = action;
            request.Username = username;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/person/action"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public List<string> GetKeepAliveUsernames()
        {
            logger.LogInformation($"Fetching keep alive usernames from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, $"api/keepalive"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var usernameArray = JsonConvert.DeserializeObject<string[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation($"Finshed fetching keep alive usernames OS2skoledata");
            return new List<string>(usernameArray);
        }

        public List<ClassroomPendingChange> GetPendingClassroomChanges()
        {
            logger.LogInformation($"Fetching all pending classroom changes from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/classrooms/pending"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var changesArray = JsonConvert.DeserializeObject<ClassroomPendingChange[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching all pending classroom changes from OS2skoledata");
            return new List<ClassroomPendingChange>(changesArray);
        }

        public void SetClassroomActionStatus(long id, ClassroomActionStatus status, string error)
        {
            SetClassroomStatusRequest request = new SetClassroomStatusRequest();
            request.Id = id;
            request.Status = status;
            request.ErrorMessage = error;

            using var httpClient = GetHttpClient();
            var content = new StringContent(JsonConvert.SerializeObject(request, getSerializerSettings()), Encoding.UTF8, "application/json");
            var response = httpClient.PostAsync(new Uri(baseUri + "api/classrooms/status"), content);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public bool GetPerformYearChange()
        {
            logger.LogInformation("Fetching perform year change from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/yearchange"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var perform = JsonConvert.DeserializeObject<bool>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching perform year change from OS2skoledata");
            return perform;
        }

        public void SetPerformedYearChange()
        {
            using var httpClient = GetHttpClient();
            var response = httpClient.PostAsync(new Uri(baseUri + "api/yearchange?deletedfoldersandgroups=true"), null);
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
        }

        public List<FolderOrGroupDTO> GetYearChangeGroupsAndFoldersForDeletion()
        {
            logger.LogInformation($"Fetching all groups and folders for deletion from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/yearchange/foldersandgroups/delete"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var changesArray = JsonConvert.DeserializeObject<FolderOrGroupDTO[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching all groups and folders for deletion from OS2skoledata");
            return new List<FolderOrGroupDTO>(changesArray);
        }

        public List<FolderOrGroupDTO> GetAllYearlyClassFolders()
        {
            logger.LogInformation($"Fetching all yearly class folders from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/yearchange/foldersandgroups/folders"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var changesArray = JsonConvert.DeserializeObject<FolderOrGroupDTO[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching all yearly class folders from OS2skoledata");
            return new List<FolderOrGroupDTO>(changesArray);
        }

        public List<FolderOrGroupDTO> GetAllYearlyClassGroups()
        {
            logger.LogInformation($"Fetching all yearly class groups from OS2skoledata");
            using var httpClient = GetHttpClient();
            var response = httpClient.GetAsync(new Uri(baseUri, "api/yearchange/foldersandgroups/groups"));
            response.Wait();
            response.Result.EnsureSuccessStatusCode();
            var responseString = response.Result.Content.ReadAsStringAsync();
            responseString.Wait();

            var changesArray = JsonConvert.DeserializeObject<FolderOrGroupDTO[]>(responseString.Result, jsonSerializerSettings);

            logger.LogInformation("finished fetching all yearly class groups from OS2skoledata");
            return new List<FolderOrGroupDTO>(changesArray);
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
