package dk.digitalidentity.os2skoledata.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class PersonCleanUpTask {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	// nightly at 03.xx.00 am
	@Scheduled(cron = "0 #{new java.util.Random().nextInt(59)} 3 * * ?")
	public void processChanges() {
		if (configuration.getScheduled().isEnabled()) {
			log.info("Cleanup of deleted institutionPersons started");
			institutionPersonService.cleanUp();
			log.info("Cleanup of deleted institutionPersons completed");
		}
	}
}
