package dk.digitalidentity.os2skoledata.api.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class InstitutionDTO {
	private long databaseId;
	private String name;
	private String number;
	private String abbreviation;
	private boolean locked;
	private String googleWorkspaceId;
	private String allDriveGoogleWorkspaceId;
	private String studentDriveGoogleWorkspaceId;
	private String employeeDriveGoogleWorkspaceId;
	private String institutionDriveGoogleWorkspaceId;
	private String allAzureSecurityGroupId;
	private String studentAzureSecurityGroupId;
	private String employeeAzureSecurityGroupId;
	private String employeeGroupGoogleWorkspaceEmail;
	private String studentInstitutionGoogleWorkspaceId;
	private String employeeInstitutionGoogleWorkspaceId;
	private InstitutionType type;
	private Map<String, String> googleWorkspaceEmailMappings;
	private String currentSchoolYear;
	private String employeeAzureTeamId;
	private String teamAdminUsername;
	private Map<String, String> azureIdentifierMappings;

}
