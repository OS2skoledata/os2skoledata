using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using Serilog;
using System;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class Address
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Country { get; set; }
        [MaxLength(255)]
        public CountryCode? CountryCode { get; set; }
        [MaxLength(255)]
        public string MunicipalityCode { get; set; }
        [MaxLength(255)]
        public string MunicipalityName { get; set; }
        [MaxLength(255)]
        public string PostalCode { get; set; }
        [MaxLength(255)]
        public string PostalDistrict { get; set; }
        [MaxLength(255)]
        public string StreetAddress { get; set; }

        public bool ApiEquals(AddressDTO address)
        {
            if (address == null)
            {
                return false;
            }

            if (this.Country != address.country)
            {
                return false;
            }

            if ((this.CountryCode == null && address.countryCode != null) ||
                (this.CountryCode != null && address.countryCode == null) ||
                (this.CountryCode != null && address.countryCode != null && this.CountryCode.ToString() != address.countryCode.ToString()))
            {
                return false;
            }

            if (this.MunicipalityCode != address.municipalityCode)
            {
                return false;
            }

            if (this.MunicipalityName != address.municipalityName)
            {
                return false;
            }

            if (this.PostalCode != address.postalCode)
            {
                return false;
            }

            if (this.PostalDistrict != address.postalDistrict)
            {
                return false;
            }

            if (this.StreetAddress != address.streetAddress)
            {
                return false;
            }

            return true;
        }

        public void CopyFields(AddressDTO address)
        {
            if (address == null)
            {
                return;
            }

            this.Country = address.country;
            this.CountryCode = address.countryCode;
            this.MunicipalityCode = address.municipalityCode;
            this.MunicipalityName = address.municipalityName;
            this.PostalCode = address.postalCode;
            this.PostalDistrict = address.postalDistrict;
            this.StreetAddress = address.streetAddress;
        }
    }

}
