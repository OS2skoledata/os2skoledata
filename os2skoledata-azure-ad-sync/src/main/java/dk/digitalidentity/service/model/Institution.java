package dk.digitalidentity.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Institution {
	private long databaseId;
	@JsonProperty("number")
	private String institutionNumber;
	@JsonProperty("name")
	private String institutionName;
	private String abbreviation;
	private String allAzureSecurityGroupId;
	private String studentAzureSecurityGroupId;
	private String employeeAzureSecurityGroupId;
	private boolean locked;
	private String employeeAzureTeamId;
	private String teamAdminUsername;
	private Map<String, String> azureIdentifierMappings;
}
