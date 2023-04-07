package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scheduled {
	private boolean enabled = false;
	private ModificationHistoryCleanup modificationHistoryCleanup = new ModificationHistoryCleanup();
}
