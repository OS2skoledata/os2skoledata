package dk.digitalidentity.os2skoledata.config;

import java.util.ArrayList;
import java.util.List;

import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.config.modules.Scheduled;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "os2skoledata")
public class OS2SkoleDataConfiguration {
	private String stilUsername;
	private String stilPassword;
	private List<InstitutionDTO> institutions = new ArrayList<>();

	private Scheduled scheduled = new Scheduled();
}
