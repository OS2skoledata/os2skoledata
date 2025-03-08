package dk.digitalidentity.task;

import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TeamsAndGroupsSync {

	@Autowired
	private SyncService syncService;

	@Autowired
	private OS2skoledataAzureADConfiguration os2skoledataAzureADConfiguration;

	// run daily at 01:10 unless something else is in the application.properties
	@Scheduled(cron = "${cron.teamsAndGroupsSync:0 10 1 * * ?}")
	public void sync() throws Exception {
		if (!os2skoledataAzureADConfiguration.getAzureAd().isTeamsAndGroupsOnly()) {
			return;
		}

		syncService.teamsAndGroupsSync();
	}
}
