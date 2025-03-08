﻿using os2skoledata_ad_sync.Services.OS2skoledata.Model;

namespace os2skoledata_ad_sync.Services.ActiveDirectory.Model
{
    public class InfoDTO
    {
        public string InstitutionName { get; set; }
        public int StartYear { get; set; }

        public InfoDTO(MiniGroup c)
        {
            this.InstitutionName = c.InstitutionName;
            this.StartYear = c.StartYear;
        }
    }
}