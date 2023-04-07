using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using System;

namespace os2skoledata_ad_sync.Services.ActiveDirectory.Model
{
    public class OUDTO
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Level { get; set; }
        public string StilId { get; set; }
        public int StartYear { get; set; }
        public InstitutionType Type { get; set; }

        public OUDTO(Group c)
        {
            this.Id = "" + c.DatabaseId;
            this.Name = c.GroupName;
            this.Level = c.GroupLevel;
            this.StilId = c.GroupId;
            this.StartYear = c.StartYear;
        }

        public OUDTO(Institution i)
        {
            this.Id = "inst" + i.InstitutionNumber;
            this.Name = i.InstitutionName;
            this.StilId = i.InstitutionNumber;
            this.Type = i.Type;
        }
    }
}