package dk.digitalidentity.os2skoledata.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.security.SecurityLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class SecurityLogCleanupTask {

	@Autowired
	private SecurityLogger securityLogger;

	// run every night at 03:?? on Saturdays
	@Scheduled(cron = "#{new java.util.Random().nextInt(60)} #{new java.util.Random().nextInt(60)} 3 * * SAT")
	public void cleanupSecurityLogs() {
		log.info("Running scheduled job");

		securityLogger.cleanupLogs();
	}
}