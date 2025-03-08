package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class AuditLogCleanupTask {

	@Autowired
	private AuditLogger auditLogger;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	// nightly at 02.xx.00 am
	@Scheduled(cron = "0 #{new java.util.Random().nextInt(59)} 2 * * ?")
	public void processChanges() {
		if (configuration.getScheduled().isEnabled()) {
			log.info("Cleanup of old logs started");
			auditLogger.cleanupLogs();
			log.info("Cleanup of old logs completed");
		}
	}
}
