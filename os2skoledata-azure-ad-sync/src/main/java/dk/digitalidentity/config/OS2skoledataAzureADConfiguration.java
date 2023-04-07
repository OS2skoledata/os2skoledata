package dk.digitalidentity.config;

import dk.digitalidentity.config.modules.AzureAd;
import dk.digitalidentity.config.modules.OS2skoledata;
import dk.digitalidentity.config.modules.SyncSettings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "skole")
public class OS2skoledataAzureADConfiguration {
	private AzureAd azureAd = new AzureAd();
	private OS2skoledata os2skoledata = new OS2skoledata();
	private SyncSettings syncSettings = new SyncSettings();
}
