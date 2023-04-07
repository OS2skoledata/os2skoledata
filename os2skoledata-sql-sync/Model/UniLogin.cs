using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class UniLogin
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string CivilRegistrationNumber { get; set; }
        [MaxLength(255)]
        public string InitialPassword { get; set; }
        [MaxLength(255)]
        public string Name { get; set; }
        [MaxLength(255)]
        public PasswordState PasswordState { get; set; }
        [MaxLength(255)]
        public string UserId { get; set; }

        public bool ApiEquals(UniLoginDTO other)
        {
            if (other == null)
            {
                return false;
            }

            if (this.CivilRegistrationNumber != other.civilRegistrationNumber)
            {
                return false;
            }

            if (this.InitialPassword != other.initialPassword)
            {
                return false;
            }

            if (this.Name != other.name)
            {
                return false;
            }

            if (this.PasswordState != other.passwordState)
            {
                return false;
            }

            if (this.UserId != other.userId)
            {
                return false;
            }

            return true;
        }

        public void CopyFields(UniLoginDTO uniLogin)
        {
            if (uniLogin == null)
            {
                return;
            }

            this.CivilRegistrationNumber = uniLogin.civilRegistrationNumber;
            this.InitialPassword = uniLogin.initialPassword;
            this.Name = uniLogin.name;
            this.PasswordState = uniLogin.passwordState;
            this.UserId = uniLogin.userId;
        }
    }

}
