namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class OptionalSecurityGroupFields
    {
        public bool SetSamAccountName { get; set; } = false;
        public string SamAccountPrefix { get; set; } = "OS2";
        public bool SamAccountUseInstitutionType { get; set; } = false;
        public string MailField { get; set; }
        public string MailDomain { get; set; }
        public bool OverwriteExistingMail { get; set; } = true;
        public string NormalMailNameStandard { get; set; }
        public string NoLineNameStandard { get; set; }
        public string NoLineNoYearNameStandard { get; set; }
        public string SecurityGroupForEmployeeTypeMailStandard { get; set; }
        public string SecurityGroupForEmployeesMailStandard { get; set; }
        public string OtherGroupsÅrgangSecurityGroupMailStandard { get; set; }
        public string OtherGroupsRetningSecurityGroupMailStandard { get; set; }
        public string OtherGroupsHoldSecurityGroupMailStandard { get; set; }
        public string OtherGroupsSFOSecurityGroupMailStandard { get; set; }
        public string OtherGroupsTeamSecurityGroupMailStandard { get; set; }
        public string OtherGroupsAndetSecurityGroupMailStandard { get; set; }
    }
}