using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using Serilog;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;

namespace os2skoledata_sql_sync.Model
{
    public class Employee
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Location { get; set; }
        [MaxLength(255)]
        public string Occupation { get; set; }
        [MaxLength(255)]
        public string ShortName { get; set; }
        public List<Role> Roles { get; set; }
        
        public List<EmployeeGroupId> GroupIds { get; set; }

        public bool ApiEquals(EmployeeDTO employee)
        {
            if (employee == null)
            {
                return false;
            }

            if (this.Location != employee.location)
            {
                return false;
            }

            if (this.Occupation != employee.occupation)
            {
                return false;
            }

            if (this.ShortName != employee.shortName)
            {
                return false;
            }

            if (this.Roles?.Count != employee.roles?.Count)
            {
                return false;
            }
            else
            {
                List<Role> existingRoles = this.Roles;
                List<string> stilRoles = employee.roles;

                if (existingRoles.Where(r1 => !stilRoles.Any(r2 => r2 == r1.EmployeeRole.ToString())).Any())
                {
                    return false;
                }

                if (stilRoles.Where(r1 => !existingRoles.Any(r2 => r2.EmployeeRole.ToString() == r1)).Any())
                {
                    return false;
                }
            }

            if (this.GroupIds?.Count != employee.groupIds?.Count)
            {
                return false;
            }
            else
            {
                List<string> dbGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = employee.groupIds;

                dbGroupIds.Sort();
                stilGroupIds.Sort();

                if (!dbGroupIds.SequenceEqual(stilGroupIds))
                {
                    return false;
                }
            }

            return true;
        }

        public void CopyFields(EmployeeDTO employee)
        {
            if (employee == null)
            {
                return;
            }

            this.Location = employee.location;
            this.Occupation = employee.occupation;
            this.ShortName = employee.shortName;

            // Roles

            if (this.Roles == null && employee.roles != null)
            {
                this.Roles = new List<Role>();

                foreach (string role in employee.roles)
                {
                    Role dbRole = new Role();
                    dbRole.Employee = this;
                    dbRole.EmployeeRole = (EmployeeRole)Enum.Parse(typeof(EmployeeRole), role);
                    this.Roles.Add(dbRole);
                }
            }
            else if (this.Roles != null && employee.roles != null)
            {
                List<string> existingRoles = this.Roles.Select(r => r.EmployeeRole.ToString()).ToList();
                List<string> stilRoles = employee.roles;

                foreach (string role in stilRoles)
                {
                    if (!existingRoles.Contains(role))
                    {
                        Role dbRole = new Role();
                        dbRole.Employee = this;
                        dbRole.EmployeeRole = (EmployeeRole)Enum.Parse(typeof(EmployeeRole), role);
                        this.Roles.Add(dbRole);
                    }
                }

                foreach (string role in existingRoles)
                {
                    if (!stilRoles.Contains(role))
                    {
                        this.Roles.RemoveAll(r => r.EmployeeRole.ToString().Equals(role));
                    }
                }
            }
            else if (this.Roles != null && employee.roles == null)
            {
                this.Roles.Clear();
            }

            // GroupIds
            if (this.GroupIds == null && employee.groupIds != null)
            {
                this.GroupIds = new List<EmployeeGroupId>();

                foreach (string groupId in employee.groupIds)
                {
                    EmployeeGroupId dbEmployeeGroupId = new EmployeeGroupId();
                    dbEmployeeGroupId.GroupId = groupId;
                    dbEmployeeGroupId.Employee = this;
                    this.GroupIds.Add(dbEmployeeGroupId);
                }
            }
            else if (this.GroupIds != null && employee.groupIds != null)
            {
                List<string> existingGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = employee.groupIds;

                foreach (string groupId in stilGroupIds)
                {
                    if (!existingGroupIds.Contains(groupId))
                    {
                        EmployeeGroupId newGroupIdMapping = new EmployeeGroupId();
                        newGroupIdMapping.Employee = this;
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
            else if (this.GroupIds != null && employee.groupIds == null)
            {
                this.GroupIds.Clear();
            }
        }
    }

}
