using os2skoledata_sql_sync.Services.OS2skoledata;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class PhoneNumber
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Value { get; set; }
        public bool Protected { get; set; }

        public bool ApiEquals(PhoneNumberDTO phoneNumber)
        {
            if (phoneNumber == null)
            {
                return false;
            }

            if (this.Value != phoneNumber.value)
            {
                return false;
            }

            if (this.Protected != phoneNumber.@protected)
            {
                return false;
            }

            return true;
        }

        public void CopyFields(PhoneNumberDTO phoneNumber)
        {
            if (phoneNumber == null)
            {
                return;
            }

            this.Value = phoneNumber.value;
            this.Protected = phoneNumber.@protected;
        }
    }

}
