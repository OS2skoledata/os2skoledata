package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.ADPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class SyncQueueCleanupTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private ADPasswordService adPasswordService;

	// Nightly
	@Scheduled(cron = "0 #{new java.util.Random().nextInt(55)} 1 * * ?")
	public void processChanges() {
		if (configuration.getScheduled().isEnabled()) {
			log.debug("Delete synchronized passwords from the queue");

			adPasswordService.syncQueueCleanupTask();
		}
	}
}
