package dk.digitalidentity.service.model;

import dk.digitalidentity.service.model.enums.EmployeeRole;
import dk.digitalidentity.service.model.enums.ExternalRole;
import dk.digitalidentity.service.model.enums.Role;
import dk.digitalidentity.service.model.enums.StudentRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DBUser {
	private long databaseId;
	private String localPersonId;
	private String cpr;
	private String firstName;
	private String familyName;
	private String username;
	private String stilUsername;
	private Role role;
	private Role globalRole;
	private List<String> groupIds;
	private List<Institution> institutions;
	private String currentInstitutionNumber;
	private List<String> studentMainGroups;
	private StudentRole studentRole;
	private StudentRole globalStudentRole;
	private List<EmployeeRole> employeeRoles;
	private List<EmployeeRole> globalEmployeeRoles;
	private ExternalRole externalRole;
	private ExternalRole globalExternalRole;
	private boolean deleted;
	private String uniId;
	private String studentMainGroupLevelForInstitution;
	private boolean setPasswordOnCreate;
	private String password;

	// not from api. Used to securityGroup and team sync in full sync
	private String azureId;
}
