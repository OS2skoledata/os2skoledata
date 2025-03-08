package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

// feature to keep users alive after they are deleted in STIL
@Getter
@Setter
public class GhostAdministration {
	private boolean enabled;
}
