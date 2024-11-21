using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using os2skoledata_affiliation_checker.Services.Sofd.Model;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

namespace os2skoledata_affiliation_checker.Services.Sofd
{
    internal class SofdService : ServiceBase<SofdService>
    {
        private readonly Uri baseUri;
        private readonly string apiKey;
        private readonly long personsPageSize = 1000;
        private readonly long personsPageCount = 20;
        private readonly JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings() { NullValueHandling = NullValueHandling.Ignore };

        public SofdService(IServiceProvider sp) : base(sp)
        {
            baseUri = new Uri(settings.SyncSettings.SOFDBaseUrl);
            apiKey = settings.SyncSettings.SOFDApiKey;
        }

        public List<Person> GetPersons()
        {
            logger.LogInformation($"Fetching persons from SOFD");
            var result = new List<Person>();
            var tasks = new List<Task<List<Person>>>();
            for (var page = 0; page < personsPageCount; page++)
            {
                tasks.Add(GetPersonsAsync(page));
            }
            Task.WaitAll(tasks.ToArray());
            foreach (var task in tasks)
            {
                result.AddRange(task.Result);
            }
            result.RemoveAll(p => p.Deleted);
            logger.LogInformation($"Finished fetching persons from SOFD");
            return result;
        }

        private async Task<List<Person>> GetPersonsAsync(int page)
        {
            using var httpClient = GetHttpClient();
            var response = await httpClient.GetAsync(new Uri(baseUri, $"api/v2/persons?size={personsPageSize}&page={page}"));
            response.EnsureSuccessStatusCode();
            var responseString = await response.Content.ReadAsStringAsync();
            var getPersonsDto = JsonConvert.DeserializeObject<GetPersonDto>(responseString, jsonSerializerSettings);
            var result = getPersonsDto.Persons;
            // if we reached max entries on the last page there might be more data in SOFD!
            if (page + 1 == personsPageCount && result.Count == personsPageSize)
            {
                throw new Exception("Not all Persons was fetched from SOFD. Increase PageCount or PageSize");
            }
            return result;
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
    }
}
