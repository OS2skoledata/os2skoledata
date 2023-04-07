package dk.digitalidentity.os2skoledata.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.ModificationHistoryService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class ModificationHistoryCleanupTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;
	
	@Autowired
	private ModificationHistoryService modificationHistoryService;

	// TODO: add some fuzz
    // At 00:25:00am, every 7 days starting on the 1st, every month
	@Scheduled(cron = "0 25 0 */7 * ?")
	public void processChanges() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("ModificationHistoryCleanupTask: Scheduled jobs are disabled on this instance");
			return;
		}

		log.info("Running ModificationHistoryCleanupTask");
		modificationHistoryService.removeModificationHistoryOlderThan(configuration.getScheduled().getModificationHistoryCleanup().getDays());
		log.info("ModificationHistoryCleanupTask done");
	}
}
