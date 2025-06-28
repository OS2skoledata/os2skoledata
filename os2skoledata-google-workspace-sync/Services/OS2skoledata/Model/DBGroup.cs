using System;

namespace os2skoledata_google_workspace_sync.Services.OS2skoledata.Model
{
    public class DBGroup
    {
        public long DatabaseId { get; set; }
        public string GroupName { get; set; }
        public string GroupId { get; set; }
        public string GroupLevel { get; set; }
        public string InstitutionNumber { get; set; }
        public string InstitutionName { get; set; }
        public string InstitutionAbbreviation { get; set; }
        public string InstitutionGoogleWorkspaceId { get; set; }
        public bool InstitutionLocked { get; set; }
        public string GoogleWorkspaceId { get; set; }
        public string StudentInstitutionGoogleWorkspaceId { get; set; }
        public string EmployeeInstitutionGoogleWorkspaceId { get; set; }
        public string DriveGoogleWorkspaceId { get; set; }
        public string GroupGoogleWorkspaceEmail { get; set; }
        public string GroupOnlyStudentsGoogleWorkspaceEmail { get; set; }
        public int StartYear { get; set; }
        public string CurrentYearGWGroupIdentifier { get; set; }
        public string CurrentYearGWFolderIdentifier { get; set; }
    }
}