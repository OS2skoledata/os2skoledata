package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import dk.digitalidentity.os2skoledata.service.ADPasswordService;
import dk.digitalidentity.os2skoledata.service.PasswordChangeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
public class SyncPasswordToADTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private ADPasswordService adPasswordService;

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	// Every minute
	@Scheduled(fixedRate = 1000 * 60)
	public void processChanges() {
		if (configuration.getScheduled().isEnabled() && configuration.getStudentAdministration().isEnabled() && !configuration.getStudentAdministration().isClassListsOnly()) {
			List<PasswordChangeQueue> changes = passwordChangeQueueService.getUnsynchronized();
			if (!changes.isEmpty()) {
				log.debug("Sync passwords to AD via Websockets");
				adPasswordService.syncPasswordsToAD(changes);
			}
		}
	}
}
