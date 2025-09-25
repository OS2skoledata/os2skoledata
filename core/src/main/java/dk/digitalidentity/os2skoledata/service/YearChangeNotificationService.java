package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.YearChangeNotificationDao;
import dk.digitalidentity.os2skoledata.dao.model.YearChangeNotification;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YearChangeNotificationService {

	@Autowired
	private YearChangeNotificationDao yearChangeNotificationDao;

	@Autowired
	private SettingService settingService;

	@Autowired
	private EmailService emailService;

	@Transactional
	public void recordYearChange(String institutionNumber, String institutionName, String oldSchoolYear, String newSchoolYear) {
		LocalDate today = LocalDate.now();

		// Check if we already have a record for this institution and date
		var existingRecord = yearChangeNotificationDao.findByInstitutionNumberAndYearChangeDate(institutionNumber, today);
		if (existingRecord.isEmpty()) {
			YearChangeNotification notification = new YearChangeNotification();
			notification.setInstitutionNumber(institutionNumber);
			notification.setInstitutionName(institutionName);
			notification.setYearChangeDate(today);
			notification.setYearChangeTimestamp(LocalDateTime.now());
			notification.setOldSchoolYear(oldSchoolYear);
			notification.setNewSchoolYear(newSchoolYear);

			yearChangeNotificationDao.save(notification);
		}
	}

	@Transactional
	public void sendDailyYearChangeNotifications() {
		LocalDate today = LocalDate.now();
		List<YearChangeNotification> pendingNotifications = yearChangeNotificationDao
				.findByYearChangeDateAndInitialNotificationSentFalse(today);

		if (pendingNotifications.isEmpty()) {
			return;
		}

		String emails = settingService.getStringValueByKey(CustomerSetting.STIL_CHANGE_EMAIL);
		if (!StringUtils.hasLength(emails)) {
			log.error("No email addresses configured for year change notifications. Notifications will not be sent. Yearchange for " +
					pendingNotifications.stream().map(YearChangeNotification::getInstitutionNumber).collect(Collectors.joining(",")));
			return;
		}

		String message = buildYearChangeNotificationMessage(pendingNotifications);
		String subject = "OS2skoledata: Årsrul gennemført - institutioner klar til oplåsning";

		emails = emails.replace(" ", "");
		String[] emailArray = emails.split(";");

		for (String email : emailArray) {
			emailService.sendMessage(email, subject, message);
		}

		// Mark notifications as sent
		for (YearChangeNotification notification : pendingNotifications) {
			notification.setInitialNotificationSent(true);
			notification.setInitialNotificationSentAt(LocalDateTime.now());
		}
		yearChangeNotificationDao.saveAll(pendingNotifications);

		log.info("Sent year change notifications for {} institutions to {} recipients",
				pendingNotifications.size(), emailArray.length);
	}

	@Transactional
	public void sendReminderNotifications() {
		LocalDate cutoffDate = LocalDate.now().minusMonths(1);
		List<YearChangeNotification> overdue = yearChangeNotificationDao
				.findUnresolvedOlderThan(cutoffDate);

		if (overdue.isEmpty()) {
			return;
		}

		String emails = settingService.getStringValueByKey(CustomerSetting.STIL_CHANGE_EMAIL);
		if (!StringUtils.hasLength(emails)) {
			log.warn("No email addresses configured for year change reminder notifications. Reminder for: " +
					overdue.stream().map(YearChangeNotification::getInstitutionNumber).collect(Collectors.joining(",")));
			return;
		}

		String message = buildReminderMessage(overdue);
		String subject = "OS2skoledata: Påmindelse - institutioner venter stadig på oplåsning efter årsrul";

		emails = emails.replace(" ", "");
		String[] emailArray = emails.split(";");

		for (String email : emailArray) {
			emailService.sendMessage(email, subject, message);
		}

		// Mark reminders as sent
		for (YearChangeNotification notification : overdue) {
			notification.setReminderSent(true);
			notification.setReminderSentAt(LocalDateTime.now());
		}
		yearChangeNotificationDao.saveAll(overdue);

		log.info("Sent reminder notifications for {} institutions to {} recipients",
				overdue.size(), emailArray.length);
	}

	@Transactional
	public void markInstitutionAsResolved(String institutionNumber) {
		List<YearChangeNotification> unresolved = yearChangeNotificationDao.findByResolvedFalseAndInstitutionNumber(institutionNumber);

		for (YearChangeNotification notification : unresolved) {
			notification.setResolved(true);
			notification.setResolvedAt(LocalDateTime.now());
		}

		if (!unresolved.isEmpty()) {
			yearChangeNotificationDao.saveAll(unresolved);
		}
	}

	private String buildYearChangeNotificationMessage(List<YearChangeNotification> notifications) {
		StringBuilder message = new StringBuilder();
		message.append("Følgende institutioner har gennemført årsrul i dag og er nu låst for opdateringer:\n\n");

		for (YearChangeNotification notification : notifications) {
			message.append("• ").append(notification.getInstitutionName())
					.append(" (").append(notification.getInstitutionNumber()).append(")")
					.append(" - Skiftet fra ").append(notification.getOldSchoolYear())
					.append(" til ").append(notification.getNewSchoolYear()).append("\n");
		}

		message.append("\nInstitutionerne er automatisk blevet låst for opdateringer. ");
		message.append("Når I er klar til at gennemføre årsrul på klienterne, skal I logge ind i OS2skoledata ");
		message.append("brugergrænsefladen og låse institutionerne op under siden \"Institutioner\".\n\n");
		message.append("Bemærk: Data for disse institutioner vil ikke blive opdateret i klienterne, før de er låst op.");

		return message.toString();
	}

	private String buildReminderMessage(List<YearChangeNotification> overdueNotifications) {
		StringBuilder message = new StringBuilder();
		message.append("Følgende institutioner har været låst i over en måned efter årsrul og venter stadig på oplåsning:\n\n");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		for (YearChangeNotification notification : overdueNotifications) {
			message.append("• ").append(notification.getInstitutionName())
					.append(" (").append(notification.getInstitutionNumber()).append(")")
					.append(" - Årsrul gennemført: ").append(notification.getYearChangeDate().format(formatter))
					.append(" (").append(notification.getOldSchoolYear())
					.append(" → ").append(notification.getNewSchoolYear()).append(")\n");
		}

		message.append("\nDisse institutioner har været låst for opdateringer i over en måned. ");
		message.append("Hvis årsrullet er færdigbehandlet, skal I logge ind i OS2skoledata ");
		message.append("brugergrænsefladen og låse institutionerne op under siden \"Institutioner\".\n\n");
		message.append("Data for disse institutioner opdateres ikke, så længe de forbliver låst.");

		return message.toString();
	}
}