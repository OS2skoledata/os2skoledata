package dk.digitalidentity.os2skoledata.task;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.MailReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class MailReportTask {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private MailReportService mailReportService;

	// Default: every day at 07:00. Configurable via property.
	@Scheduled(cron = "${os2skoledata.scheduled.mail-report-cron:0 0 7 * * ?}")
	public void sendMailReports() {
		if (!configuration.getScheduled().isEnabled()) {
			log.debug("MailReportTask: Scheduled jobs are disabled on this instance");
			return;
		}

		log.info("Running mail report task");

		try {
			mailReportService.sendDailyCreatedReport();
		} catch (Exception e) {
			log.error("Failed to send daily created report", e);
		}

		log.info("Mail report task: Done");
	}
}