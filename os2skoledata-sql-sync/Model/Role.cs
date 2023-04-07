using os2skoledata_sql_sync.Model.Enums;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace os2skoledata_sql_sync.Model
{
    public class Role
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public EmployeeRole EmployeeRole { get; set; }

        // back ref
        [ForeignKey("Employee")]
        public int employeeId { get; set; }
        public virtual Employee Employee { get; set; }
    }
}
