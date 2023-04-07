package dk.digitalidentity.service.model;

import dk.digitalidentity.service.model.enums.EmployeeRole;
import dk.digitalidentity.service.model.enums.ExternalRole;
import dk.digitalidentity.service.model.enums.Role;
import dk.digitalidentity.service.model.enums.StudentRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DBUser {
	private long databaseId;
	private String localPersonId;
	private String cpr;
	private String firstName;
	private String familyName;
	private String username;
	private Role role;
	private List<String> groupIds;
	private List<Institution> institutions;
	private String currentInstitutionNumber;
	private List<String> studentMainGroups;
	private StudentRole studentRole;
	private List<EmployeeRole> employeeRoles;
	private ExternalRole externalRole;

	// not from api. Used to securityGroup sync in full sync
	private String azureId;
}
