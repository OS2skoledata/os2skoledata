package dk.digitalidentity.task;

import dk.digitalidentity.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DeltaSync {

	@Autowired
	private SyncService syncService;

	@Scheduled(cron = "${cron.deltaSync:0 0/5 * * * ?}")
	public void sync() throws Exception {
		syncService.deltaSync();
	}
}
