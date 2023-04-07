using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class ContactPerson
    {
        [Key]
        public int Id { get; set; }
        public int? AccessLevel { get; set; }
        [MaxLength(255)]
        public string Cvr { get; set; }
        [MaxLength(255)]
        public string Pnr { get; set; }
        [MaxLength(255)]
        public RelationType Relation { get; set; }
        public bool ChildCustody { get; set; }
        public UniLogin UniLogin { get; set; }
        public Person Person { get; set; }

        // back ref to Student
        public virtual Student Student { get; set; }

        public bool ApiEquals(ContactPersonDTO contactPerson)
        {
            if (contactPerson == null)
            {
                return false;
            }

            if (this.AccessLevel != contactPerson.accessLevel)
            {
                return false;
            }

            if (this.Cvr != contactPerson.cvr)
            {
                return false;
            }

            if (this.Pnr != contactPerson.pnr)
            {
                return false;
            }

            if (this.ChildCustody != contactPerson.childCustody)
            {
                return false;
            }

            if (this.Relation != contactPerson.relation)
            {
                return false;
            }

            if ((this.Person == null && contactPerson.person != null) ||
                (this.Person != null && !this.Person.ApiEquals(contactPerson.person)))
            {
                return false;
            }

            if ((this.UniLogin == null && contactPerson.uniLogin != null) ||
                (this.UniLogin != null && !this.UniLogin.ApiEquals(contactPerson.uniLogin)))
            {
                return false;
            }

            return true;
        }


        public void CopyFields(ContactPersonDTO contactPerson)
        {
            if (contactPerson == null)
            {
                return;
            }

            this.AccessLevel = contactPerson.accessLevel;
            this.Cvr = contactPerson.cvr;
            this.ChildCustody = contactPerson.childCustody;
            this.Pnr = contactPerson.pnr;
            this.Relation = contactPerson.relation;

            if (this.Person == null && contactPerson.person != null)
            {
                this.Person = new Person();
            }

            if (this.Person != null)
            {
                this.Person.CopyFields(contactPerson.person);
            }

            if (this.UniLogin == null && contactPerson.uniLogin != null)
            {
                this.UniLogin = new UniLogin();
            }

            if (this.UniLogin != null)
            {
                this.UniLogin.CopyFields(contactPerson.uniLogin);
            }
        }
    }

}
