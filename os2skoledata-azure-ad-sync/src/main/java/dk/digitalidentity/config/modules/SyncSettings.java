package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncSettings {
	private String domain;
	private UsernameSettings usernameSettings = new UsernameSettings();
	private FilteringSettings filteringSettings = new FilteringSettings();
	private LicenseSettings licenseSettings = new LicenseSettings();
	private NameStandards nameStandards = new NameStandards();
}
