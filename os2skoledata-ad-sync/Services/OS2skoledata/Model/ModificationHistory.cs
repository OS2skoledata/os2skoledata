using System;
using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.OS2skoledata.Model
{
    public class ModificationHistory
    {
        public long Id { get; set; }
        public DateTime Tts { get; set; }
        public long EntityId { get; set; }
        public EntityType EntityType { get; set; }
        public EventType EventType { get; set; }
        public string EntityName { get; set; }
        public long InstitutionId { get; set; }
        public string InstitutionName { get; set; }

        // only for EntityType person
        public List<string> Groups { get; set; }
    }

    public enum EntityType
    {
        INSTITUTION,
        INSTITUTION_PERSON,
        GROUP
    }

    public enum EventType
    {
        CREATE,
        UPDATE,
        DELETE
    }
}