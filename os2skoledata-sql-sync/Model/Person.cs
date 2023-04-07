using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class Person
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string AliasFamilyName { get; set; }
        [MaxLength(255)]
        public string AliasFirstName { get; set; }
        [DataType(DataType.Date)]
        public DateTime? BirthDate { get; set; }
        [MaxLength(255)]
        public string CivilRegistrationNumber { get; set; }
        [MaxLength(255)]
        public string EmailAddress { get; set; }
        [MaxLength(255)]
        public string FamilyName { get; set; }
        [MaxLength(255)]
        public string FirstName { get; set; }
        [MaxLength(255)]
        public Gender? Gender { get; set; }
        [MaxLength(255)]
        public string PhotoId { get; set; }
        public int VerificationLevel { get; set; }
        public bool Protected { get; set; }
        public Address Address { get; set; }
        public PhoneNumber HomePhoneNumber { get; set; }
        public PhoneNumber MobilePhoneNumber { get; set; }
        public PhoneNumber WorkPhoneNumber { get; set; }

        public bool ApiEquals(PersonDTO person)
        {
            if (person == null)
            {
                return false;
            }

            if (this.AliasFamilyName != person.aliasFamilyName)
            {
                return false;
            }

            if (this.AliasFirstName != person.aliasFirstName)
            {
                return false;
            }

            if (this.CivilRegistrationNumber != person.civilRegistrationNumber)
            {
                return false;
            }

            if (this.EmailAddress != person.emailAddress)
            {
                return false;
            }

            if (this.FamilyName != person.familyName)
            {
                return false;
            }

            if (this.FirstName != person.firstName)
            {
                return false;
            }

            if (this.PhotoId != person.photoId)
            {
                return false;
            }

            if (this.BirthDate != person.birthDate)
            {
                return false;
            }

            if (this.VerificationLevel != person.verificationLevel)
            {
                return false;
            }

            if (this.Protected != person.@protected)
            {
                return false;
            }

            if ((this.Gender == null && person.gender != null) ||
                (this.Gender != null && person.gender == null) ||
                (this.Gender != person.gender))
            {
                return false;
            }

            if ((this.Address == null && person.address != null) ||
                (this.Address != null && !this.Address.ApiEquals(person.address)))
            {
                return false;
            }

            if ((this.HomePhoneNumber == null && person.homePhoneNumber != null) ||
                (this.HomePhoneNumber != null && !this.HomePhoneNumber.ApiEquals(person.homePhoneNumber)))
            {
                return false;
            }

            if ((this.MobilePhoneNumber == null && person.mobilePhoneNumber != null) ||
                (this.MobilePhoneNumber != null && !this.MobilePhoneNumber.ApiEquals(person.mobilePhoneNumber)))
            {
                return false;
            }

            if ((this.WorkPhoneNumber == null && person.workPhoneNumber != null) ||
                (this.WorkPhoneNumber != null && !this.WorkPhoneNumber.ApiEquals(person.workPhoneNumber)))
            {
                return false;
            }

            return true;
        }

        public void CopyFields(PersonDTO person)
        {
            if (person == null)
            {
                return;
            }

            this.AliasFamilyName = person.aliasFamilyName;
            this.AliasFirstName = person.aliasFirstName;
            this.BirthDate = person.birthDate;
            this.CivilRegistrationNumber = person.civilRegistrationNumber;
            this.EmailAddress = person.emailAddress;
            this.FamilyName = person.familyName;
            this.FirstName = person.firstName;
            this.Gender = person.gender;
            this.Protected = person.@protected;
            this.PhotoId = person.photoId;
            this.VerificationLevel = person.verificationLevel;

            if (this.Address == null && person.address != null)
            {
                this.Address = new Address();
            }

            if (this.Address != null)
            {
                this.Address.CopyFields(person.address);
            }

            if (this.HomePhoneNumber == null && person.homePhoneNumber != null)
            {
                this.HomePhoneNumber = new PhoneNumber();
            }

            if (this.HomePhoneNumber != null)
            {
                this.HomePhoneNumber.CopyFields(person.homePhoneNumber);
            }

            if (this.MobilePhoneNumber == null && person.mobilePhoneNumber != null)
            {
                this.MobilePhoneNumber = new PhoneNumber();
            }

            if (this.MobilePhoneNumber != null)
            {
                this.MobilePhoneNumber.CopyFields(person.mobilePhoneNumber);
            }

            if (this.WorkPhoneNumber == null && person.workPhoneNumber != null)
            {
                this.WorkPhoneNumber = new PhoneNumber();
            }

            if (this.WorkPhoneNumber != null)
            {
                this.WorkPhoneNumber.CopyFields(person.workPhoneNumber);
            }
        }
    }

}
