package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.SyncService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class SyncTask {
	
	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private SyncService syncService;

	// TODO: add default value with some fuzz
	@Scheduled(cron = "${os2skoledata.scheduled.cron}")
	public void sync() throws Exception {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("SyncTask: Scheduled jobs are disabled on this instance");
			return;
		}

		log.info("Running STIL synchronization");

		List<InstitutionDTO> institutionDTOList = configuration.getInstitutions();

		syncService.deleteInstitutions(institutionDTOList);

		for (InstitutionDTO institutionDTO : institutionDTOList) {
			syncService.sync(institutionDTO);
		}

		log.info("STIL synchronization: Done");
	}
}
