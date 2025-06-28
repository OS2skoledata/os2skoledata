package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzureAd {
	private String clientID;
	private String clientSecret;
	private boolean usingPEMCertificate;
	private String certificatePath;
	private String tenantID;
	private boolean userDryRun;
	private boolean teamsAndGroupsOnly = false;
}
