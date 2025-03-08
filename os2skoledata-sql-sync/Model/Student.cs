using os2skoledata_sql_sync.Model.Enums;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;

namespace os2skoledata_sql_sync.Model
{
    public class Student
    {
        [Key]
        public int Id { get; set; }
        [MaxLength(255)]
        public string Level { get; set; }
        [MaxLength(255)]
        public string Location { get; set; }
        [MaxLength(255)]
        public string MainGroupId { get; set; }
        [MaxLength(255)]
        public StudentRole Role { get; set; }
        [MaxLength(255)]
        public string StudentNumber { get; set; }
        public List<ContactPerson> ContactPersons { get; set; }
        public List<StudentGroupId> GroupIds { get; set; }

        public bool ApiEquals(StudentDTO student)
        {
            if (student == null)
            {
                return false;
            }

            if (this.Location != student.location)
            {
                return false;
            }

            if (this.Level != student.level)
            {
                return false;
            }

            if (this.MainGroupId != student.mainGroupId)
            {
                return false;
            }

            if (this.Role != student.role)
            {
                return false;
            }

            if (this.StudentNumber != student.studentNumber)
            {
                return false;
            }

            if (this.ContactPersons?.Count != student.contactPersons?.Count)
            {
                return false;
            }
            else
            {
                // in this code we use firstName+familyName as a key
                List<string> existingContactPersons = this.ContactPersons.Select(cp => cp.Person.FirstName + cp.Person.FamilyName).ToList();
                List<string> stilContactPersons = student.contactPersons.Select(cp => cp.person.firstName + cp.person.familyName).ToList();


                foreach (string contactPerson in stilContactPersons)
                {
                    if (!existingContactPersons.Contains(contactPerson))
                    {
                        return false;
                    }
                }

                foreach (string contactPerson in existingContactPersons)
                {
                    if (!stilContactPersons.Contains(contactPerson))
                    {
                        return false;
                    }
                }

                // if all the groupIds match on both list we check if the group fields are different
                foreach (ContactPerson contactPerson in this.ContactPersons)
                {
                    var stilContactPerson = student.contactPersons.Where(sc => contactPerson.Person.ApiEquals(sc.person)).FirstOrDefault();

                    if (stilContactPerson != null && !contactPerson.ApiEquals(stilContactPerson))
                    {
                        return false;
                    }
                }
            }

            if (this.GroupIds?.Count != student.groupIds?.Count)
            {
                return false;
            }
            else
            {
                List<string> dbGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = student.groupIds;

                dbGroupIds.Sort();
                stilGroupIds.Sort();

                if (!dbGroupIds.SequenceEqual(stilGroupIds))
                {
                    return false;
                }
            }

            return true;
        }

        public void CopyFields(StudentDTO student)
        {
            if (student == null)
            {
                return;
            }

            this.Level = student.level;
            this.Location = student.location;
            this.MainGroupId = student.mainGroupId;
            this.Role = student.role;
            this.StudentNumber = student.studentNumber;

            // Contact persons
            if (this.ContactPersons == null && student.contactPersons != null)
            {
                this.ContactPersons = new List<ContactPerson>();

                foreach (ContactPersonDTO contactPerson in student.contactPersons)
                {
                    ContactPerson dbContactPerson = new ContactPerson();
                    dbContactPerson.Student = this;
                    dbContactPerson.CopyFields(contactPerson);
                    this.ContactPersons.Add(dbContactPerson);
                }
            }
            else if (this.ContactPersons != null && student.contactPersons != null)
            {
                // in this code we use firstName+familyName as a key
                List<string> existingContactPersons = this.ContactPersons.Select(cp => cp.Person.FirstName + cp.Person.FamilyName).ToList();
                List<string> stilContactPersons = student.contactPersons.Select(cp => cp.person.firstName + cp.person.familyName).ToList();

                foreach (string contactPerson in stilContactPersons)
                {
                    if (!existingContactPersons.Contains(contactPerson))
                    {
                        //Add new ContactPerson
                        ContactPerson dbContactPerson = new ContactPerson();
                        dbContactPerson.Student = this;
                        dbContactPerson.CopyFields(student.contactPersons.Where(cp => contactPerson == cp.person.firstName + cp.person.familyName).FirstOrDefault());
                        this.ContactPersons.Add(dbContactPerson);
                    }
                }

                foreach (string contactPerson in existingContactPersons)
                {
                    if (!stilContactPersons.Contains(contactPerson))
                    {
                        //Delete ContactPerson
                        this.ContactPersons.RemoveAll(cp => contactPerson == cp.Person.FirstName + cp.Person.FamilyName);
                    }
                }

                //Update ContactPerson
                foreach (ContactPerson dbContactPerson in this.ContactPersons)
                {
                    var stilContactPerson = student.contactPersons
                        .Where(cp =>
                                    dbContactPerson.Person.FirstName + dbContactPerson.Person.FamilyName ==
                                    cp.person.firstName + cp.person.familyName)
                            .FirstOrDefault();

                    if (stilContactPerson != null)
                    {
                        dbContactPerson.CopyFields(stilContactPerson);
                    }
                }
            }
            else if (this.ContactPersons != null && student.contactPersons == null)
            {
                this.ContactPersons.Clear();
            }

            // GroupIds
            if (this.GroupIds == null && student.groupIds != null)
            {
                this.GroupIds = new List<StudentGroupId>();

                foreach (string groupId in student.groupIds)
                {
                    StudentGroupId dbStudentGroupId = new StudentGroupId();
                    dbStudentGroupId.GroupId = groupId;
                    dbStudentGroupId.Student = this;
                    this.GroupIds.Add(dbStudentGroupId);
                }
            }
            else if (this.GroupIds != null && student.groupIds != null)
            {
                List<string> existingGroupIds = this.GroupIds.Select(g => g.GroupId).ToList();
                List<string> stilGroupIds = student.groupIds;

                foreach (string groupId in stilGroupIds)
                {
                    if (!existingGroupIds.Contains(groupId))
                    {
                        StudentGroupId newGroupIdMapping = new StudentGroupId();
                        newGroupIdMapping.Student = this;
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
            else if (this.GroupIds != null && student.groupIds == null)
            {
                this.GroupIds.Clear();
            }
        }
    }

}
