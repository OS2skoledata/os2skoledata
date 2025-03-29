package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncSettings {
	private String domain;
	private UsernameSettings usernameSettings = new UsernameSettings();
	private FilteringSettings filteringSettings = new FilteringSettings();
	private NameStandards nameStandards = new NameStandards();
	private boolean useUsernameAsKey = false;
	private AzureField uniIdField = AzureField.NONE;
	private AzureTeamsSettings azureTeamsSettings = new AzureTeamsSettings();
	private DeleteUserSettings deleteUserSettings = new DeleteUserSettings();
}
