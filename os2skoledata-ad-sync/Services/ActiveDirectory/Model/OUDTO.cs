using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using System;

namespace os2skoledata_ad_sync.Services.ActiveDirectory.Model
{
    public class OUDTO
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Abbreviation { get; set; }
        public string Level { get; set; }
        public string StilId { get; set; }
        public string Line { get; set; }
        public int StartYear { get; set; }
        public InstitutionType Type { get; set; }
        public bool InstitutionLocked { get; set; }

        public OUDTO(Group c)
        {
            this.Id = "" + c.DatabaseId;
            this.Name = c.GroupName;
            this.Abbreviation = c.InstitutionAbbreviation;
            this.Level = c.GroupLevel;
            this.StilId = c.GroupId;
            this.StartYear = c.StartYear;
            this.InstitutionLocked = c.InstitutionLocked;
            this.Line = c.Line;
        }

        public OUDTO(Institution i)
        {
            this.Id = "inst" + i.InstitutionNumber;
            this.Name = i.InstitutionName;
            this.Abbreviation = i.Abbreviation;
            this.StilId = i.InstitutionNumber;
            this.Type = i.Type;
            this.InstitutionLocked = i.Locked;
        }
    }
}