package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserSettings {
	private boolean enabled = false;
	private int daysBeforeDeletionStudent = 60;
	private int daysBeforeDeletionEmployee = 60;
	private int daysBeforeDeletionExternal = 60;
}
