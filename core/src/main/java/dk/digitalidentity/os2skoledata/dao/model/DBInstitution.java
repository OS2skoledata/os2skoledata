package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import https.unilogin_dk.data.Group;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "institution")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBInstitution {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@LastModifiedDate
	private LocalDateTime lastModified;

	@Column
	private boolean deleted;

	@Column
	private String institutionName;

	@Column
	private String institutionNumber;

	@Column
	private String googleWorkspaceId;

	@Column
	private String studentInstitutionGoogleWorkspaceId;

	@Column
	private String employeeInstitutionGoogleWorkspaceId;

	@Column
	private String allDriveGoogleWorkspaceId;

	@Column
	private String studentDriveGoogleWorkspaceId;

	@Column
	private String employeeDriveGoogleWorkspaceId;

	@Column
	private String allAzureSecurityGroupId;

	@Column
	private String studentAzureSecurityGroupId;

	@Column
	private String employeeAzureSecurityGroupId;

	@Column
	private String employeeGroupGoogleWorkspaceEmail;

	@Enumerated(EnumType.STRING)
	@Column
	private InstitutionType type;

	@Column
	private boolean bypassTooFewPeople;

	@Column
	private boolean ignoreEmptyError;

	@Column
	private String employeeAzureTeamId;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "azure_employee_team_admin_id")
	private DBInstitutionPerson azureEmployeeTeamAdmin;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
	private List<DBInstitutionPerson> institutionPersons;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBGroup> groups;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InstitutionGoogleWorkspaceGroupMapping> googleWorkspaceGroupEmailMappings;

	public boolean apiEquals(InstitutionFullMyndighed other) {
		if (other == null) {
			return false;
		}
		
		if (!Objects.equals(this.institutionName, other.getInstitutionName())) {
			log.debug("DBInstitution: Not equals on 'institutionName' for " + this.id);
			return false;
		}
		
		if (!Objects.equals(this.institutionNumber, other.getInstitutionNumber())) {
			log.debug("DBInstitution: Not equals on 'institutionNumber' for " + this.id);
			return false;
		}
		
		if (this.groups.size() != other.getGroup().size()) {
			log.debug("DBInstitution: Not equals on 'groups' for " + this.id + " different collection size.");
			return false;
		}
		else {
			List<DBGroup> existingGroups = this.groups;
			List<Group> stilGroups = other.getGroup();
			
			if (existingGroups.stream().filter(g1 -> stilGroups.stream().noneMatch(g2 -> g2.getGroupId().equals(g1.getGroupId()))).findAny().isPresent()) {
				log.debug("DBInstitution: Not equals on 'groups' for " + this.id);
				return false;
			}
			
			if (stilGroups.stream().filter(g1 -> existingGroups.stream().noneMatch(g2 -> g2.getGroupId().equals(g1.getGroupId()))).findAny().isPresent()) {
				log.debug("DBInstitution: Not equals on 'groups' for " + this.id);
				return false;
			}

			// if all the groupIds match on both list we check if the group fields are different
			for (DBGroup existingGroup : existingGroups) {
				var stilGroup = stilGroups.stream().filter(sg -> sg.getGroupId().equals(existingGroup.getGroupId())).findAny();
				
				if (stilGroup.isPresent() && !existingGroup.apiEquals(stilGroup.get())) {
					log.debug("DBInstitution: Not equals on 'groups' for " + this.id + " group: " + existingGroup.getGroupId() + " has been modified.");
					return false;
				}
			}
		}
		
		return true;
	}

	public void copyFields(InstitutionFullMyndighed other) {
		if (other == null) {
			return;
		}
		
		this.institutionName = other.getInstitutionName();
		this.institutionNumber = other.getInstitutionNumber();
		
		if (this.groups == null) {
			this.groups = new ArrayList<>();
		}

		List<String> existingGroupIds = this.groups.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
		List<String> stilGroupIds = new ArrayList<String>();
		List<Group> toBeAdded = new ArrayList<Group>();
		List<Group> toBeUpdated = new ArrayList<Group>();

		// to avoid duplicate names on groups in incoming data
		Map<String, Integer> counterMap = new HashMap<>();

		for (Group group : other.getGroup()) {

			// start by fixing name, in case there are duplicates in input
			
			int count = 0;
			
			if (counterMap.containsKey(group.getGroupName().toLowerCase())) {
				count = counterMap.get(group.getGroupName().toLowerCase());
			}
			
			counterMap.put(group.getGroupName().toLowerCase(), ++count);
			
			if (count > 1) {
				group.setGroupName(group.getGroupName() + " " + count);
			}

			// then split into update/create scenario

			if (existingGroupIds.contains(group.getGroupId())) {
				toBeUpdated.add(group);
			}
			else {
				toBeAdded.add(group);
			}

			stilGroupIds.add(group.getGroupId());
		}

		List<DBGroup> toBeDeleted = this.groups.stream().filter(g -> !stilGroupIds.contains(g.getGroupId())).collect(Collectors.toList());
		
		// do update
		for (Group group : toBeUpdated) {
			var existingGroup = this.groups.stream().filter(g -> g.getGroupId().equals(group.getGroupId())).findAny().orElse(null);
			
			// existingGroup should never be null at this point
			if (existingGroup != null) {
				existingGroup.setDeleted(false);
				existingGroup.copyFields(group);
			}
		}
		
		// add new groups
		for (Group group : toBeAdded) {
			DBGroup dbGroup = new DBGroup();
			dbGroup.setInstitution(this);
			dbGroup.setDeleted(false);
			dbGroup.copyFields(group);
			this.groups.add(dbGroup);
		}
		
		// remove groups
		for (DBGroup group : toBeDeleted) {
			group.setDeleted(true);
			group.setAzureSecurityGroupId(null);
		}
	}


	public void copyFieldsWithoutGroups(InstitutionFullMyndighed other) {
		if (other == null) {
			return;
		}

		this.institutionName = other.getInstitutionName();
		this.institutionNumber = other.getInstitutionNumber();
	}
}