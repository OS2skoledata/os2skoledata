﻿namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class OptionalSecurityGroupFields
    {
        public bool SetSamAccountName { get; set; } = false;
        public string SamAccountPrefix { get; set; } = "OS2";
        public bool SamAccountUseInstitutionType { get; set; } = false;
        public string MailField { get; set; }
        public string MailDomain { get; set; }
        public string NormalMailNameStandard { get; set; }
        public string NoLineNameStandard { get; set; }
        public string NoLineNoYearNameStandard { get; set; }
    }
}