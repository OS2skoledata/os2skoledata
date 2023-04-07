using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data;
using System.Linq;

namespace os2skoledata_sql_sync.Model
{
    public class Extern
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public ExternalRoleType Role { get; set; }
        public List<ExternGroupId> GroupIds { get; set; }

        internal bool ApiEquals(ExternDTO @extern)
        {

            if (@extern == null)
            {
                return false;
            }

            if (this.Role != @extern.role)
            {
                return false;
            }

            if (this.GroupIds?.Count != @extern.groupIds?.Count)
            {
                return false;
            }
            else
            {
                List<string> dbGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = @extern.groupIds;

                dbGroupIds.Sort();
                stilGroupIds.Sort();

                if (!dbGroupIds.SequenceEqual(stilGroupIds))
                {
                    return false;
                }
            }

            return true;
        }

        internal void CopyFields(ExternDTO @extern)
        {
            if (@extern == null)
            {
                return;
            }

            this.Role = @extern.role;

            // GroupIds
            if (this.GroupIds == null && @extern.groupIds != null)
            {
                this.GroupIds = new List<ExternGroupId>();

                foreach (string groupId in @extern.groupIds)
                {
                    ExternGroupId dbExternGroupId = new ExternGroupId();
                    dbExternGroupId.GroupId = groupId;
                    dbExternGroupId.Extern = this;
                    this.GroupIds.Add(dbExternGroupId);
                }
            }
            else if (this.GroupIds != null && @extern.groupIds != null)
            {
                List<string> existingGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = @extern.groupIds;

                foreach (string groupId in stilGroupIds)
                {
                    if (!existingGroupIds.Contains(groupId))
                    {
                        ExternGroupId newGroupIdMapping = new ExternGroupId();
                        newGroupIdMapping.Extern = this;
                        newGroupIdMapping.GroupId = groupId;
                        this.GroupIds.Add(newGroupIdMapping);
                    }
                }

                foreach (string groupId in existingGroupIds)
                {
                    if (!stilGroupIds.Contains(groupId))
                    {
                        this.GroupIds.RemoveAll(r => r.GroupId.Equals(groupId));
                    }
                }
            }
            else if (this.GroupIds != null && @extern.groupIds == null)
            {
                this.GroupIds.Clear();
            }
        }
    }

}
