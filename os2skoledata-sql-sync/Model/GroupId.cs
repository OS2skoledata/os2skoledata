using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace os2skoledata_sql_sync.Model
{
    public class EmployeeGroupId
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string GroupId { get; set; }

        // back ref to Employee
        public virtual Employee Employee { get; set; }
    }

    public class ExternGroupId
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string GroupId { get; set; }

        // back ref to Extern
        public virtual Extern Extern { get; set; }
    }

    public class StudentGroupId
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string GroupId { get; set; }

        // back ref to Student
        public virtual Student Student { get; set; }
    }


}