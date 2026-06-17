package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MailReportService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private EmailService emailService;

	@Transactional(readOnly = true)
	public void sendDailyCreatedReport() {
		String emails = settingService.getStringValueByKey(CustomerSetting.DAILY_CREATED_REPORT_EMAILS);
		if (!StringUtils.hasLength(emails)) {
			log.debug("No emails configured for daily created report - skipping");
			return;
		}

		LocalDateTime since = LocalDateTime.now().minusHours(24);
		List<DBInstitutionPerson> createdPersons = institutionPersonService.findCreatedAfter(since);

		if (createdPersons.isEmpty()) {
			log.info("No new persons created in the last 24 hours - skipping daily created report");
			return;
		}

		String body = buildDailyCreatedReportBody(createdPersons);

		emails = emails.replace(" ", "");
		String[] emailArray = emails.split(";");
		for (String email : emailArray) {
			if (StringUtils.hasLength(email)) {
				emailService.sendMessage(email.trim(), "OS2skoledata: Daglig rapport - nye brugere", body);
			}
		}

		log.info("Sent daily created report with {} new persons to {} recipients", createdPersons.size(), emailArray.length);
	}

	private String buildDailyCreatedReportBody(List<DBInstitutionPerson> createdPersons) {
		// Build a map of groupId -> (institutionId -> groupName) for looking up main group names
		List<DBGroup> allGroups = groupService.findAllNotDeleted();
		Map<String, Map<Long, String>> institutionGroupMap = allGroups.stream()
				.collect(Collectors.groupingBy(
						DBGroup::getGroupId,
						Collectors.toMap(g -> g.getInstitution().getId(), DBGroup::getGroupName, (a, b) -> a)
				));

		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; color: #333;'>");
		sb.append("<h2 style='color: #2c3e50;'>Daglig rapport &mdash; nye brugere</h2>");
		sb.append("<p>Oversigt over brugere oprettet i OS2skoledata det seneste d&oslash;gn.</p>");
		sb.append("<p><strong>Antal nye brugere:</strong> ").append(createdPersons.size());
		sb.append(" &nbsp;&bull;&nbsp; <strong>Rapport genereret:</strong> ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>");

		sb.append("<table style='border-collapse: collapse; width: 100%; margin-top: 16px;'>");
		sb.append("<thead><tr style='background-color: #3498db; color: #fff;'>");
		sb.append("<th style='padding: 10px 12px; text-align: left;'>Type</th>");
		sb.append("<th style='padding: 10px 12px; text-align: left;'>Navn</th>");
		sb.append("<th style='padding: 10px 12px; text-align: left;'>Institution</th>");
		sb.append("<th style='padding: 10px 12px; text-align: left;'>Klasse</th>");
		sb.append("<th style='padding: 10px 12px; text-align: left;'>Brugernavn</th>");
		sb.append("</tr></thead><tbody>");

		boolean even = false;
		for (DBInstitutionPerson person : createdPersons) {
			String rowColor = even ? "#f2f6fa" : "#ffffff";
			even = !even;

			String type = getPersonType(person);
			String name = getDisplayName(person);
			String institutionName = person.getInstitution() != null ? person.getInstitution().getInstitutionName() : "Ukendt";
			String username = StringUtils.hasLength(person.getUsername()) ? person.getUsername() : "";
			String mainGroup = "";

			if (person.getStudent() != null && person.getStudent().getMainGroupId() != null) {
				mainGroup = resolveGroupName(person.getStudent().getMainGroupId(), person.getInstitution().getId(), institutionGroupMap);
			}

			sb.append("<tr style='background-color: ").append(rowColor).append(";'>");
			sb.append("<td style='padding: 8px 12px; border-bottom: 1px solid #e0e0e0;'>").append(escapeHtml(type)).append("</td>");
			sb.append("<td style='padding: 8px 12px; border-bottom: 1px solid #e0e0e0;'>").append(escapeHtml(name)).append("</td>");
			sb.append("<td style='padding: 8px 12px; border-bottom: 1px solid #e0e0e0;'>").append(escapeHtml(institutionName)).append("</td>");
			sb.append("<td style='padding: 8px 12px; border-bottom: 1px solid #e0e0e0;'>").append(escapeHtml(mainGroup)).append("</td>");
			sb.append("<td style='padding: 8px 12px; border-bottom: 1px solid #e0e0e0;'>").append(escapeHtml(username)).append("</td>");
			sb.append("</tr>");
		}

		sb.append("</tbody></table>");
		sb.append("<p style='margin-top: 20px; font-size: 12px; color: #999;'>Denne mail er automatisk genereret af OS2skoledata.</p>");
		sb.append("</body></html>");

		return sb.toString();
	}

	private String resolveGroupName(String groupId, long institutionId, Map<String, Map<Long, String>> institutionGroupMap) {
		Map<Long, String> institutionMap = institutionGroupMap.get(groupId);
		if (institutionMap != null) {
			String name = institutionMap.get(institutionId);
			if (name != null) {
				return name;
			}
		}
		return groupId;
	}

	private String getDisplayName(DBInstitutionPerson person) {
		if (person.getPerson() == null) {
			return "Ukendt";
		}

		String firstName;
		String familyName;

		if (person.getPerson().isProtected()) {
			firstName = person.getPerson().getAliasFirstName() != null ? person.getPerson().getAliasFirstName() : person.getPerson().getFirstName();
			familyName = person.getPerson().getAliasFamilyName() != null ? person.getPerson().getAliasFamilyName() : person.getPerson().getFamilyName();
		} else {
			firstName = person.getPerson().getFirstName();
			familyName = person.getPerson().getFamilyName();
		}

		return (firstName != null ? firstName : "") + " " + (familyName != null ? familyName : "");
	}

	private String getPersonType(DBInstitutionPerson person) {
		if (person.getEmployee() != null) {
			return "Medarbejder";
		} else if (person.getStudent() != null) {
			return "Elev";
		} else if (person.getExtern() != null) {
			return "Ekstern";
		}
		return "Ukendt";
	}

	private String escapeHtml(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;");
	}
}