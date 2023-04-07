package dk.digitalidentity.task;

import dk.digitalidentity.service.AzureADService;
import dk.digitalidentity.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class FullSync {

	@Autowired
	private SyncService syncService;

	// @Scheduled(fixedRate = 60 * 1000) // for testing
	// run daily at 01:10 unless something else is in the application.properties
	@Scheduled(cron = "${cron.fullSync:0 10 1 * * ?}")
	public void sync() throws Exception {
		syncService.fullSync();
	}
}
