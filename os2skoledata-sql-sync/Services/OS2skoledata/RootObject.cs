using Newtonsoft.Json;
using os2skoledata_sql_sync.Model.Enums;
using System;
using System.Collections.Generic;

namespace os2skoledata_sql_sync.Services.OS2skoledata
{
    public class InstitutionDTO
    {
        [JsonConverter(typeof(CustomDateTimeConverter), "yyyy-MM-dd HH:mm:ss")]
        public DateTime lastModified { get; set; }
        public bool deleted { get; set; }
        public string institutionName { get; set; }
        public string institutionNumber { get; set; }
        public InstitutionType type { get; set; }
        public List<InstitutionPersonDTO> institutionPersons { get; set; }
        public List<GroupDTO> groups { get; set; }
    }

    public class InstitutionPersonDTO
    {
        public int os2skoledataDatabaseId { get; set; }
        [JsonConverter(typeof(CustomDateTimeConverter), "yyyy-MM-dd HH:mm:ss")]
        public DateTime lastModified { get; set; }
        public bool deleted { get; set; }
        public string source { get; set; }
        public string localPersonId { get; set; }
        public string username { get; set; }
        public EmployeeDTO employee { get; set; }
        public ExternDTO @extern { get; set; }
        public StudentDTO student { get; set; }
        public PersonDTO person { get; set; }
        public UniLoginDTO uniLogin { get; set; }
        public bool primaryInstitution { get; set; }
        public string reservedUsername  { get; set; }
        public DateTime StilCreated { get; set; }
        public DateTime StilDeleted { get; set; }
        public DateTime AdCreated { get; set; }
        public DateTime AdDeactivated { get; set; }
        public DateTime GwCreated { get; set; }
        public DateTime GwDeactivated { get; set; }
        public DateTime AzureCreated { get; set; }
        public DateTime AzureDeactivated { get; set; }
    }

    public class EmployeeDTO
    {
        public string location { get; set; }
        public string occupation { get; set; }
        public string shortName { get; set; }
        public List<string> roles { get; set; }
        public List<string> groupIds { get; set; }
    }

    public class ExternDTO
    {
        public ExternalRoleType role { get; set; }
        public List<string> groupIds { get; set; }
    }

    public class StudentDTO
    {
        public string level { get; set; }
        public string location { get; set; }
        public string mainGroupId { get; set; }
        public StudentRole role { get; set; }
        public string studentNumber { get; set; }
        public List<ContactPersonDTO> contactPersons { get; set; }
        public List<string> groupIds { get; set; }
    }

    public class ContactPersonDTO
    {
        public int? accessLevel { get; set; }
        public string cvr { get; set; }
        public string pnr { get; set; }
        public RelationType relation { get; set; }
        public bool childCustody { get; set; }
        public UniLoginDTO uniLogin { get; set; }
        public PersonDTO person { get; set; }
    }

    public class PersonDTO
    {
        public string aliasFamilyName { get; set; }
        public string aliasFirstName { get; set; }
        public DateTime? birthDate { get; set; }
        public string civilRegistrationNumber { get; set; }
        public string emailAddress { get; set; }
        public string familyName { get; set; }
        public string firstName { get; set; }
        public Gender? gender { get; set; }
        public string photoId { get; set; }
        public int verificationLevel { get; set; }
        public bool @protected { get; set; }
        public AddressDTO address { get; set; }
        public PhoneNumberDTO homePhoneNumber { get; set; }
        public PhoneNumberDTO mobilePhoneNumber { get; set; }
        public PhoneNumberDTO workPhoneNumber { get; set; }
    }

    public class AddressDTO
    {
        public string country { get; set; }
        public CountryCode? countryCode { get; set; }
        public string municipalityCode { get; set; }
        public string municipalityName { get; set; }
        public string postalCode { get; set; }
        public string postalDistrict { get; set; }
        public string streetAddress { get; set; }
    }

    public class PhoneNumberDTO
    {
        public string value { get; set; }
        public bool @protected { get; set; }
    }

    public class UniLoginDTO
    {
        public string civilRegistrationNumber { get; set; }
        public string initialPassword { get; set; }
        public string name { get; set; }
        public PasswordState passwordState { get; set; }
        public string userId { get; set; }
    }

    public class GroupDTO
    {
        [JsonConverter(typeof(CustomDateTimeConverter), "yyyy-MM-dd HH:mm:ss")]
        public DateTime lastModified { get; set; }
        public bool deleted { get; set; }
        public DateTime? fromDate { get; set; }
        public DateTime? toDate { get; set; }
        public string groupId { get; set; }
        public string groupLevel { get; set; }
        public string groupName { get; set; }
        public ImportGroupType groupType { get; set; }
        public string line { get; set; }
    }

}
