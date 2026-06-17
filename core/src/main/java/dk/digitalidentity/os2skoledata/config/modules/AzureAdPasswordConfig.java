package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzureAdPasswordConfig {
	private String tenantId;
	private String clientId;
	private String clientSecret;
	private String upnSuffix; // fx @domain
}