package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzureTeamsSettings {
	private boolean handleTeams = false;
	private int readOnlyPeriod = 90;
	private String classTeamTemplate = "educationClass";
	private String employeeTeamTemplate = "standard";
}
