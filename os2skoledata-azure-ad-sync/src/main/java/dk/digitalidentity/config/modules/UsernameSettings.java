package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameSettings {
	private UsernameStandard usernameStandard;
	private String usernamePrefix;
}
