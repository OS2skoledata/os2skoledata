package dk.digitalidentity.task;

import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
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

	@Autowired
	private OS2skoledataAzureADConfiguration os2skoledataAzureADConfiguration;

	@Scheduled(cron = "${cron.deltaSync:0 0/5 * * * ?}")
	public void sync() throws Exception {
		if (os2skoledataAzureADConfiguration.getAzureAd().isTeamsAndGroupsOnly()) {
			return;
		}

		syncService.deltaSync();
	}
}
