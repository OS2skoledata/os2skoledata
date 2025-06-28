package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class MonitorClientsTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private ClientService clientService;

	@Scheduled(cron = "0 50 23 * * ?")
	public void processChanges() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("MonitorClientsTask: Scheduled jobs are disabled on this instance");
			return;
		}

		clientService.monitor();
	}
}
