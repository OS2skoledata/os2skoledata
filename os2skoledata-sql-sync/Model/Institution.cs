using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using Serilog;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text.RegularExpressions;

namespace os2skoledata_sql_sync.Model
{
    public class Institution
    {
        [Key]
        public int Id { get; set; }
        public DateTime LastModified { get; set; }
        public bool Deleted { get; set; }
        [MaxLength(255)]
        public string InstitutionName { get; set; }
        [MaxLength(255)]
        public string InstitutionNumber { get; set; }
        [MaxLength(255)]
        public InstitutionType type { get; set; }
        public List<InstitutionPerson> InstitutionPersons { get; set; }
        public List<Group> Groups { get; set; }

        public bool ApiEquals(InstitutionDTO other)
        {
            if (other == null)
            {
                return false;
            }

            if (!this.InstitutionName.Equals(other.institutionName))
            {
                return false;
            }

            if (!this.InstitutionNumber.Equals(other.institutionNumber))
            {
                return false;
            }

            if (this.Deleted != other.deleted)
            {
                return false;
            }

            if (this.LastModified != other.lastModified)
            {
                return false;
            }

            if (!this.type.Equals(other.type))
            {
                return false;
            }

            if ((this.Groups == null && other.groups != null) || (this.Groups != null && other.groups == null) || (this.Groups.Count != other.groups.Count))
            {
                return false;
            }
            else
            {
                List<Group> existingGroups = this.Groups;
                List<GroupDTO> apiGroups = other.groups;

                if (existingGroups.Where(g1 => apiGroups.Any(g2 => g2.groupId.Equals(g1.GroupId)) == false).Any())
                {
                    return false;
                }

                if (apiGroups.Where(g1 => existingGroups.Any(g2 => g2.GroupId.Equals(g1.groupId)) == false).Any())
                {
                    return false;
                }

                // if all the groupIds match on both list we check if the group fields are different
                foreach (Group existingGroup in existingGroups)
                {
                    var stilGroup = apiGroups.Where(sg => sg.groupId.Equals(existingGroup.GroupId)).FirstOrDefault();

                    if (stilGroup != null && !existingGroup.ApiEquals(stilGroup))
                    {
                        return false;
                    }
                }
            }

            if ((this.InstitutionPersons == null && other.institutionPersons != null) || (this.InstitutionPersons != null && other.institutionPersons == null) || (this.InstitutionPersons.Count != other.institutionPersons.Count))
            {
                return false;
            }
            else
            {
                List<InstitutionPerson> existingPeople = this.InstitutionPersons;
                List<InstitutionPersonDTO> apiPeople = other.institutionPersons;

                if (existingPeople.Where(p1 => apiPeople.Any(p2 => p2.os2skoledataDatabaseId.Equals(p1.OS2skoledataDatabaseId)) == false).Any())
                {
                    return false;
                }

                if (apiPeople.Where(p1 => existingPeople.Any(p2 => p2.OS2skoledataDatabaseId.Equals(p1.os2skoledataDatabaseId)) == false).Any())
                {
                    return false;
                }

                // if all the personIds match on both list we check if the personIds are different
                foreach (InstitutionPerson existingPerson in existingPeople)
                {
                    var stilPerson = apiPeople.Where(sp => sp.os2skoledataDatabaseId.Equals(existingPerson.OS2skoledataDatabaseId)).FirstOrDefault();

                    if (stilPerson != null && !existingPerson.ApiEquals(stilPerson))
                    {
                        return false;
                    }
                }
            }

            return true;
        }

        public void CopyFields(InstitutionDTO other)
        {
            if (other == null)
            {
                return;
            }

            this.InstitutionName = other.institutionName;
            this.InstitutionNumber = other.institutionNumber;
            this.Deleted = other.deleted;
            this.LastModified = other.lastModified;
            this.type = other.type;

            CopyGroups(other);
            CopyInstitutionPersons(other);
        }

        private void CopyGroups(InstitutionDTO other)
        {
            if (this.Groups == null)
            {
                this.Groups = new List<Group> { };
            }

            List<string> existingGroupIds = this.Groups.Select(g => g.GroupId).ToList();
            List<string> apiGroupIds = new List<string> { };
            List<GroupDTO> toBeAdded = new List<GroupDTO> { };
            List<GroupDTO> toBeUpdated = new List<GroupDTO> { };

            foreach (GroupDTO group in other.groups)
            {
                if (existingGroupIds.Contains(group.groupId))
                {
                    toBeUpdated.Add(group);
                }
                else
                {
                    toBeAdded.Add(group);
                }

                apiGroupIds.Add(group.groupId);
            }

            List<Group> toBeDeleted = this.Groups.Where(g => !apiGroupIds.Contains(g.GroupId)).ToList();

            // do update
            foreach (GroupDTO group in toBeUpdated)
            {
                var existingGroup = this.Groups.Where(g => g.GroupId.Equals(group.groupId)).FirstOrDefault();

                // existingGroup should never be null at this point
                existingGroup?.CopyFields(group);
            }

            // add new groups
            foreach (GroupDTO group in toBeAdded)
            {
                Group dbGroup = new Group();
                dbGroup.Institution = this;
                dbGroup.CopyFields(group);
                this.Groups.Add(dbGroup);
            }

            // remove groups
            this.Groups.RemoveAll(group => toBeDeleted.Contains(group));
        }

        private void CopyInstitutionPersons(InstitutionDTO other)
        {
            if (this.InstitutionPersons == null)
            {
                this.InstitutionPersons = new List<InstitutionPerson> { };
            }

            List<string> existingInstitutionPersonIds = this.InstitutionPersons.Select(g => g.LocalPersonId).ToList();
            List<int> existingInstitutionPersonDatabaseIds = this.InstitutionPersons.Select(g => g.OS2skoledataDatabaseId).ToList();
            List<int> apiDatabaseIds = new List<int> { };
            List<InstitutionPersonDTO> toBeAdded = new List<InstitutionPersonDTO> { };
            List<InstitutionPersonDTO> toBeUpdated = new List<InstitutionPersonDTO> { };

            foreach (InstitutionPersonDTO person in other.institutionPersons)
            {
                if (existingInstitutionPersonDatabaseIds.Contains(person.os2skoledataDatabaseId) || existingInstitutionPersonIds.Contains(person.localPersonId))
                {
                    toBeUpdated.Add(person);
                }
                else
                {
                    toBeAdded.Add(person);
                }

                apiDatabaseIds.Add(person.os2skoledataDatabaseId);
            }

            List<InstitutionPerson> toBeDeleted = this.InstitutionPersons.Where(g => g.OS2skoledataDatabaseId != 0 && !apiDatabaseIds.Contains(g.OS2skoledataDatabaseId)).ToList();

            // do update
            foreach (InstitutionPersonDTO person in toBeUpdated)
            {
                var existingInstitutionPerson = this.InstitutionPersons.Where(g => 
                (g.OS2skoledataDatabaseId != 0 && g.OS2skoledataDatabaseId == person.os2skoledataDatabaseId) ||  
                (g.LocalPersonId != null && g.LocalPersonId.Equals(person.localPersonId)))
                .FirstOrDefault();

                // existingInstitutionPerson should never be null at this point
                existingInstitutionPerson?.CopyFields(person);
            }

            // add new institutionPersons
            foreach (InstitutionPersonDTO person in toBeAdded)
            {
                InstitutionPerson dbInstitutionPerson = new InstitutionPerson();
                dbInstitutionPerson.Institution = this;
                dbInstitutionPerson.CopyFields(person);
                this.InstitutionPersons.Add(dbInstitutionPerson);
            }

            // remove institutionPersons
            this.InstitutionPersons.RemoveAll(group => toBeDeleted.Contains(group));
        }
    }
}
