package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.YearChangeNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class YearChangeNotificationTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private YearChangeNotificationService yearChangeNotificationService;

	// Run daily at 7:00 to send year change notifications
	@Scheduled(cron = "0 0 7 * * *")
	public void sendDailyYearChangeNotifications() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("YearChangeNotificationTask: Scheduled jobs are disabled on this instance");
			return;
		}

		log.info("Running daily year change notifications task");

		try {
			yearChangeNotificationService.sendDailyYearChangeNotifications();
			log.info("Daily year change notifications task completed");
		} catch (Exception e) {
			log.error("Error during daily year change notifications task", e);
		}
	}

	// Run weekly on Mondays at 9:00 to send reminder notifications
	@Scheduled(cron = "0 0 9 * * MON")
	public void sendReminderNotifications() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("YearChangeNotificationTask: Scheduled jobs are disabled on this instance");
			return;
		}

		log.info("Running weekly reminder notifications task");

		try {
			yearChangeNotificationService.sendReminderNotifications();
			log.info("Weekly reminder notifications task completed");
		} catch (Exception e) {
			log.error("Error during weekly reminder notifications task", e);
		}
	}
}