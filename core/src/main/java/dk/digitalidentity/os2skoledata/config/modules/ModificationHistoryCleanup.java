package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificationHistoryCleanup {
	private int days = 90;
}
