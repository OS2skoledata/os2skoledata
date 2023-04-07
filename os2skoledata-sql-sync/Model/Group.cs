using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.ComponentModel.DataAnnotations;

namespace os2skoledata_sql_sync.Model
{
    public class Group
    {
        [Key]
        public int Id { get; set; }
        public DateTime LastModified { get; set; }
        public bool Deleted { get; set; }
        [DataType(DataType.Date)]
        public DateTime? FromDate { get; set; }
        [DataType(DataType.Date)]
        public DateTime? ToDate { get; set; }
        [MaxLength(255)]
        public string GroupId { get; set; }
        [MaxLength(255)]
        public string GroupLevel { get; set; }
        [MaxLength(255)]
        public string GroupName { get; set; }
        [MaxLength(255)]
        public ImportGroupType GroupType { get; set; }
        [MaxLength(255)]
        public string Line { get; set; }

        // back reference to Institution
        public virtual Institution Institution { get; set; }

        public bool ApiEquals(GroupDTO group)
        {
            if (group == null)
            {
                return false;
            }

            if (this.FromDate != group.fromDate)
            {
                return false;
            }

            if (this.ToDate != group.toDate)
            {
                return false;
            }

            if (this.GroupId != group.groupId)
            {
                return false;
            }

            if (this.GroupLevel != group.groupLevel)
            {
                return false;
            }

            if (this.GroupName != group.groupName)
            {
                return false;
            }

            if (this.Line != group.line)
            {
                return false;
            }

            if (this.GroupType != group.groupType)
            {
                return false;
            }

            if (this.LastModified != group.lastModified)
            {
                return false;
            }

            if (this.Deleted != group.deleted)
            {
                return false;
            }

            return true;
        }

        public void CopyFields(GroupDTO group)
        {
            if (group == null)
            {
                return;
            }

            this.FromDate = group.fromDate;
            this.ToDate = group.toDate;
            this.GroupId = group.groupId;
            this.GroupLevel = group.groupLevel;
            this.GroupName = group.groupName;
            this.GroupType = group.groupType;
            this.Line = group.line;
            this.Deleted = group.deleted;
            this.LastModified = group.lastModified;
        }
    }

}
