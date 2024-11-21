package dk.digitalidentity.os2skoledata.dao.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import https.unilogin.Ansatrolle;
import https.unilogin_dk.data.transitional.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBEmployee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String location;
	
	@Column
	private String occupation;

	@Column
	private String shortName;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBRole> roles;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBEmployeeGroupId> groupIds;

	public boolean apiEquals(Employee employee) {
		if (employee == null) {
			return false;
		}

		if (!Objects.equals(this.location, employee.getLocation())) {
			log.debug("DBEmployee: Not equals on 'location' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.occupation, employee.getOccupation())) {
			log.debug("DBEmployee: Not equals on 'occupation' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.shortName, employee.getShortName())) {
			log.debug("DBEmployee: Not equals on 'shortName' for " + this.id);
			return false;
		}

		if (this.roles.size() != employee.getRole().size()) {
			log.debug("DBEmployee: Not equals on 'roles' for " + this.id + " different collection size.");
			return false;
		}
		else {
			List<DBRole> existingRoles = this.roles;
			List<Ansatrolle> stilRoles = employee.getRole();
			
			if (existingRoles.stream().filter(r1 -> stilRoles.stream().noneMatch(r2 -> Objects.equals(r2.toString(), r1.getEmployeeRole().toString()))).findAny().isPresent()) {
				log.debug("DBEmployee: Not equals on 'roles' for " + this.id + " found role not present in stil.");
				return false;
			}
			
			if (stilRoles.stream().filter(r1 -> existingRoles.stream().noneMatch(r2 -> Objects.equals(r2.getEmployeeRole().toString(), r1.toString()))).findAny().isPresent()) {
				log.debug("DBEmployee: Not equals on 'roles' for " + this.id + " found role not present in local db");
				return false;
			}
		}

		if (this.groupIds.size() != employee.getGroupId().size()) {
			log.debug("DBEmployee: Not equals on 'groupIds' for " + this.id + " different collection size.");
			return false;
		}
		else {
			List<String> dbGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = employee.getGroupId();
			
			Collections.sort(dbGroupIds);
			Collections.sort(stilGroupIds);
			
			if (!dbGroupIds.equals(stilGroupIds)) {
				log.debug("DBEmployee: Not equals on 'groupIds' for " + this.id + " different collection values.");
				return false;
			}
		}
		
		return true;
	}

	public void copyFields(Employee employee) {
		if (employee == null) {
			return;
		}
		
		this.location = employee.getLocation();
		this.occupation = employee.getOccupation();
		this.shortName = employee.getShortName();

		// Roles
		
		if (this.roles == null && employee.getRole() != null) {
			this.roles = new ArrayList<>();
			
			for (Ansatrolle role : employee.getRole()) {
				DBRole dbRole = new DBRole(role);
				dbRole.setEmployee(this);
				this.roles.add(dbRole);
			}
		}
		else if (this.roles != null && employee.getRole() != null) {
			List<String> existingRoles = this.roles.stream().map(r -> r.getEmployeeRole().name()).collect(Collectors.toList());
			List<String> stilRoles = employee.getRole().stream().map(r -> r.name()).collect(Collectors.toList());
			
			for (String role : stilRoles) {
				if (!existingRoles.contains(role)) {
					DBRole newRole = new DBRole();
					newRole.setEmployee(this);
					newRole.setEmployeeRole(DBEmployeeRole.valueOf(role));
					this.roles.add(newRole);
				}
			}
			
			for (String role : existingRoles) {
				if (!stilRoles.contains(role)) {
					this.roles.removeIf(r -> r.getEmployeeRole().name().equals(role));
				}
			}
		}
		else if (this.roles != null && employee.getRole() == null) {
			this.roles.clear();
		}

		// GroupIds
		if (this.groupIds == null && employee.getGroupId()!= null) {
			this.groupIds = new ArrayList<>();
			
			for (String groupId : employee.getGroupId()) {
				DBEmployeeGroupId dbEmployeeGroupId = new DBEmployeeGroupId();
				dbEmployeeGroupId.setGroupId(groupId);
				dbEmployeeGroupId.setEmployee(this);
				this.groupIds.add(dbEmployeeGroupId);
			}
		}
		else if (this.groupIds != null && employee.getGroupId() != null) {
			List<String> existingGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = employee.getGroupId();
			
			for (String groupId : stilGroupIds) {
				if (!existingGroupIds.contains(groupId)) {
					DBEmployeeGroupId newGroupIdMapping = new DBEmployeeGroupId();
					newGroupIdMapping.setEmployee(this);
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
		else if (this.groupIds != null && employee.getGroupId() == null) {
			this.groupIds.clear();
		}
	}
}
