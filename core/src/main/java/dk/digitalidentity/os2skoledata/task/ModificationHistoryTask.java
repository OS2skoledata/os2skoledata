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
public class ModificationHistoryTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private ModificationHistoryService modificationHistoryService;

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void processChanges() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("ModificationHistoryTask: Scheduled jobs are disabled on this instance");
			return;
		}
		
		modificationHistoryService.processChanges();
	}
}
