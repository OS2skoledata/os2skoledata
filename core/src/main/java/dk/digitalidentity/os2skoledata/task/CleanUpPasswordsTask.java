package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.CprPasswordMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class CleanUpPasswordsTask {
	@Autowired
	private CprPasswordMappingService cprPasswordMappingService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	// run every night at 02:?? on Saturdays
	@Scheduled(cron = "#{new java.util.Random().nextInt(60)} #{new java.util.Random().nextInt(60)} 2 * * SUN")
	public void cleanUpPasswords() {
		if (configuration.getScheduled().isEnabled()) {
			log.info("Cleanup of old passwords started");
			cprPasswordMappingService.cleanupPasswords();
			log.info("Cleanup of old passwords completed");
		}
	}
}
