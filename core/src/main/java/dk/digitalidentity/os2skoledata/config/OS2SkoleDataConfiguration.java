package dk.digitalidentity.os2skoledata.config;

import java.util.ArrayList;
import java.util.List;

import dk.digitalidentity.os2skoledata.config.modules.CprConfiguration;
import dk.digitalidentity.os2skoledata.config.modules.GhostAdministration;
import dk.digitalidentity.os2skoledata.config.modules.Idp;
import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import dk.digitalidentity.os2skoledata.config.modules.StudentAdministration;
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
	private boolean dev = false;
	private String stilUsername;
	private String stilPassword;
	private List<InstitutionDTO> institutions = new ArrayList<>();
	private StudentAdministration studentAdministration = new StudentAdministration();
	private CprConfiguration cpr = new CprConfiguration();
	private String cvr;
	private Idp idp = new Idp();
	private TeamAdminAdministration teamAdminAdministration = new TeamAdminAdministration();
	private boolean ignoreNameProtection = false;
	private GhostAdministration ghostAdministration = new GhostAdministration();
	private int deleteInstitutionPersonAfterMonths = 13;
	private boolean filterOutGroupsWithFutureFromDate = false;
	private int createGroupsXDaysBeforeFromDate = 60;

	private Scheduled scheduled = new Scheduled();
}
