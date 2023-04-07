using Newtonsoft.Json;
using System;
using System.Collections.Generic;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class Institution
    {
        public long DatabaseId { get; set; }
        [JsonProperty("number")]
        public string InstitutionNumber { get; set; }
        [JsonProperty("name")]
        public string InstitutionName { get; set; }
        public string GoogleWorkspaceId { get; set; }
        public string AllDriveGoogleWorkspaceId { get; set; }
        public string StudentDriveGoogleWorkspaceId { get; set; }
        public string EmployeeDriveGoogleWorkspaceId { get; set; }
        public string EmployeeGroupGoogleWorkspaceEmail { get; set; }
        public InstitutionType Type { get; set; }
        public Dictionary<string, string> GoogleWorkspaceEmailMappings { get; set; }
    }

    public enum InstitutionType
    {
        SCHOOL,
        DAYCARE,
        MUNICIPALITY
    }
}