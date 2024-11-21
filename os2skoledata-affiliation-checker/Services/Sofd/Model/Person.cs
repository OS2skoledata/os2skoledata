using System;
using System.Collections.Generic;

namespace os2skoledata_affiliation_checker.Services.Sofd.Model
{
    public class Person
    {
        public String Uuid { get; set; }
        public String Cpr { get; set; }
        public List<Affiliation> Affiliations { get; set; }
        public Boolean Deleted { get; set; }
    }
}
