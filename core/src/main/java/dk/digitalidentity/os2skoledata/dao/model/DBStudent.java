package dk.digitalidentity.os2skoledata.dao.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.ContactPersonFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.StudentFullMyndighed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBStudent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String level;
	
	@Column
	private String location;

	@Column
	private String mainGroupId;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBStudentRole role;
	
	@Column
	private String studentNumber;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBContactPerson> contactPersons;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBStudentGroupId> groupIds;

	public boolean apiEquals(StudentFullMyndighed student, boolean requiredOnly) {
		if (student == null) {
			return false;
		}

		if (!Objects.equals(this.level, student.getLevel())) {
			log.debug("DBStudent: Not equals on 'level' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.location, student.getLocation())) {
			log.debug("DBStudent: Not equals on 'location' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.mainGroupId, student.getMainGroupId())) {
			log.debug("DBStudent: Not equals on 'mainGroupId' for " + this.id);
			return false;
		}

		if ((this.role == null && student.getRole() != null)
				|| (this.role != null && student.getRole() == null)
				|| (!Objects.equals(this.role.name(), student.getRole().name()))) {
			log.debug("DBStudent: Not equals on 'gender' for " + this.id);
			return false;
		}

		if (this.groupIds.size() != student.getGroupId().size()) {
			log.debug("DBStudent: Not equals on 'groupIds' for " + this.id + " different collection size.");
			return false;
		}
		else {
			List<String> dbGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = student.getGroupId();

			Collections.sort(dbGroupIds);
			Collections.sort(stilGroupIds);

			if (!dbGroupIds.equals(stilGroupIds)) {
				log.debug("DBStudent: Not equals on 'groupIds' for " + this.id + " different collection values.");
				return false;
			}
		}

		if (!requiredOnly) {
			if (this.contactPersons.size() != student.getContactPerson().size()) {
				log.debug("DBStudent: Not equals on 'contactPersons' for " + this.id + " different collection size.");
				return false;
			}
			else {
				// in this code we use firstName+familyName as a key
				List<String> existingContactPersons = this.contactPersons.stream().map(cp -> cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()).collect(Collectors.toList());
				List<String> stilContactPersons = student.getContactPerson().stream().map(cp -> cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()).collect(Collectors.toList());

				for (String contactPerson : stilContactPersons) {
					if (!existingContactPersons.contains(contactPerson)) {
						log.debug("DBStudent: Not equals on 'contactPersons' for " + this.id + " stil contactPerson not found in local.");
						return false;
					}
				}

				for (String contactPerson : existingContactPersons) {
					if (!stilContactPersons.contains(contactPerson)) {
						log.debug("DBStudent: Not equals on 'contactPersons' for " + this.id + " local contactPerson not found in stil.");
						return false;
					}
				}
			}
		}

		return true;
	}

	public void copyFields(StudentFullMyndighed student, boolean requiredOnly) {
		if (student == null) {
			return;
		}

		this.level = student.getLevel();
		this.location = student.getLocation();
		this.mainGroupId = student.getMainGroupId();

		if (student.getRole() != null) {
			this.role = DBStudentRole.from(student.getRole());
		}
		else {
			this.role = null;
		}

		this.studentNumber = student.getStudentNumber();

		// GroupIds
		if (this.groupIds == null && student.getGroupId() != null) {
			this.groupIds = new ArrayList<>();

			for (String groupId : student.getGroupId()) {
				DBStudentGroupId dbStudentGroupId = new DBStudentGroupId();
				dbStudentGroupId.setGroupId(groupId);
				dbStudentGroupId.setStudent(this);
				this.groupIds.add(dbStudentGroupId);
			}
		}
		else if (this.groupIds != null && student.getGroupId() != null) {
			List<String> existingGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = student.getGroupId();

			for (String groupId : stilGroupIds) {
				if (!existingGroupIds.contains(groupId)) {
					DBStudentGroupId newGroupIdMapping = new DBStudentGroupId();
					newGroupIdMapping.setStudent(this);
					newGroupIdMapping.setGroupId(groupId);
					this.groupIds.add(newGroupIdMapping);
				}
			}

			for (String groupId : existingGroupIds) {
				if (!stilGroupIds.contains(groupId)) {
					this.groupIds.removeIf(r -> r.getGroupId().equals(groupId));
				}
			}
		}
		else if (this.groupIds != null && student.getGroupId() == null) {
			this.groupIds.clear();
		}


		if (!requiredOnly) {
			// Contact persons
			if (this.contactPersons == null && student.getContactPerson() != null) {
				this.contactPersons = new ArrayList<>();

				for (ContactPersonFullMyndighed contactPerson : student.getContactPerson()) {
					DBContactPerson dbContactPerson = new DBContactPerson();
					dbContactPerson.setStudent(this);
					dbContactPerson.copyFields(contactPerson);
					this.contactPersons.add(dbContactPerson);
				}
			}
			else if (this.contactPersons != null && student.getContactPerson() != null) {
				// in this code we use firstName+familyName as a key
				List<String> existingContactPersons = this.contactPersons.stream().map(cp -> cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()).collect(Collectors.toList());
				List<String> stilContactPersons = student.getContactPerson().stream().map(cp -> cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()).collect(Collectors.toList());

				for (String contactPerson : stilContactPersons) {
					if (!existingContactPersons.contains(contactPerson)) {
						//Add new ContactPerson
						DBContactPerson dbContactPerson = new DBContactPerson();
						dbContactPerson.setStudent(this);
						dbContactPerson.copyFields(student.getContactPerson().stream().filter(cp -> Objects.equals(contactPerson, cp.getPerson().getFirstName() + cp.getPerson().getFamilyName())).findAny().get());
						this.contactPersons.add(dbContactPerson);
					}
				}

				for (String contactPerson : existingContactPersons) {
					if (!stilContactPersons.contains(contactPerson)) {
						//Delete ContactPerson
						this.contactPersons.removeIf(cp -> Objects.equals(contactPerson, cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()));
					}
				}

				//Update ContactPerson
				for (DBContactPerson dbContactPerson : this.contactPersons) {
					ContactPersonFullMyndighed stilContactPerson = student.getContactPerson().stream()
							.filter(cp -> Objects.equals(
									dbContactPerson.getPerson().getFirstName() + dbContactPerson.getPerson().getFamilyName(),
									cp.getPerson().getFirstName() + cp.getPerson().getFamilyName()))
							.findAny().orElse(null);

					if (stilContactPerson != null) {
						dbContactPerson.copyFields(stilContactPerson);
					}
				}
			}
			else if (this.contactPersons != null && student.getContactPerson() == null) {
				this.contactPersons.clear();
			}
		}
	}
}
