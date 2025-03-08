using os2skoledata_sql_sync.Services.OS2skoledata;
using Serilog;
using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace os2skoledata_sql_sync.Model
{
    public class InstitutionPerson
    {
        [Key]
        public int Id { get; set; }
        public DateTime LastModified { get; set; }
        public bool Deleted { get; set; }
        [MaxLength(255)]
        public string Source { get; set; }
        [MaxLength(255)]
        public string LocalPersonId { get; set; }
        [MaxLength(255)]
        public string Username { get; set; }
        public  Employee Employee { get; set; }
        public  Extern Extern { get; set; }
        public  Student Student { get; set; }
        public  Person Person { get; set; }
        public  UniLogin UniLogin { get; set; }

        // back reference to Institution
        [ForeignKey("Institution")]
        public int institutionId { get; set; }
        public virtual Institution Institution { get; set; }

        public DateTime StilCreated { get; set; }
        public DateTime StilDeleted { get; set; }
        public DateTime AdCreated { get; set; }
        public DateTime AdDeactivated { get; set; }
        public DateTime GwCreated { get; set; }
        public DateTime GwDeactivated { get; set; }
        public DateTime AzureCreated { get; set; }
        public DateTime AzureDeactivated { get; set; }


        public bool ApiEquals(InstitutionPersonDTO other)
        {
            if (other == null)
            {
                return false;
            }

            if (this.Source != other.source)
            {
                return false;
            }

            if (this.LastModified != other.lastModified)
            {
                return false;
            }

            if (this.Deleted != other.deleted)
            {
                return false;
            }

            if (this.LocalPersonId != other.localPersonId)
            {
                return false;
            }

            if (this.Username != other.username)
            {
                return false;
            }

            if ((this.Employee == null && other.employee != null) ||
                (this.Employee != null && !this.Employee.ApiEquals(other.employee)))
            {
                return false;
            }

            if ((this.Extern == null && other.@extern != null) ||
                (this.Extern != null && !this.Extern.ApiEquals(other.@extern))) 
            {
                return false;
            }

            if ((this.Person == null && other.person != null) ||
                (this.Person != null && !this.Person.ApiEquals(other.person)))
            {
                return false;
            }

            if ((this.Student == null && other.student != null) ||
                (this.Student != null && !this.Student.ApiEquals(other.student)))
            {
                return false;
            }

            if ((this.UniLogin == null && other.uniLogin != null) ||
                (this.UniLogin != null && !this.UniLogin.ApiEquals(other.uniLogin)))
            {
                return false;
            }

            if (this.StilCreated != other.StilCreated)
            {
                return false;
            }

            if (this.StilDeleted != other.StilDeleted)
            {
                return false;
            }

            if (this.AdCreated != other.AdCreated)
            {
                return false;
            }

            if (this.AdDeactivated != other.AdDeactivated)
            {
                return false;
            }

            if (this.GwCreated != other.GwCreated)
            {
                return false;
            }

            if (this.GwDeactivated != other.GwDeactivated)
            {
                return false;
            }

            if (this.AzureCreated != other.AzureCreated)
            {
                return false;
            }

            if (this.AzureDeactivated != other.AzureDeactivated)
            {
                return false;
            }

            return true;
        }

        public void CopyFields(InstitutionPersonDTO institutionPerson)
        {
            if (institutionPerson == null)
            {
                return;
            }

            this.LastModified = institutionPerson.lastModified;
            this.Deleted = institutionPerson.deleted;
            this.LocalPersonId = institutionPerson.localPersonId;
            this.Source = institutionPerson.source;
            this.Username = institutionPerson.username;

            this.StilCreated = institutionPerson.StilCreated;
            this.StilDeleted = institutionPerson.StilDeleted;
            this.AdCreated = institutionPerson.AdCreated;
            this.AdDeactivated = institutionPerson.AdDeactivated;
            this.GwCreated = institutionPerson.GwCreated;
            this.GwDeactivated = institutionPerson.GwDeactivated;
            this.AzureCreated = institutionPerson.AzureCreated;
            this.AzureDeactivated = institutionPerson.AzureDeactivated;

            if (this.Employee == null && institutionPerson.employee != null)
            {
                this.Employee = new Employee();
            }

            if (this.Employee != null)
            {
                this.Employee.CopyFields(institutionPerson.employee);
            }

            if (this.Extern == null && institutionPerson.@extern != null) {
                this.Extern = new Extern();
            }

            if (this.Extern != null) {
                this.Extern.CopyFields(institutionPerson.@extern);
            }

            if (this.Person == null && institutionPerson.person != null)
            {
                this.Person = new Person();
            }

            if (this.Person != null)
            {
                this.Person.CopyFields(institutionPerson.person);
            }

            if (this.Student == null && institutionPerson.student != null)
            {
                this.Student = new Student();
            }

            if (this.Student != null)
            {
                this.Student.CopyFields(institutionPerson.student);
            }

            if (this.UniLogin == null && institutionPerson.uniLogin != null)
            {
                this.UniLogin = new UniLogin();
            }

            if (this.UniLogin != null)
            {
                this.UniLogin.CopyFields(institutionPerson.uniLogin);
            }
        }
    }

}
