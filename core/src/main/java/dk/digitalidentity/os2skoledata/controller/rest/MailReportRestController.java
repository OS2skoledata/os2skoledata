package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequireAdministratorRole
@RestController
public class MailReportRestController {

	@Autowired
	private SettingService settingService;

	record MailReportEmailsDTO(String emails) {}

	@PostMapping("/rest/mailreports/dailycreated/save")
	public ResponseEntity<HttpStatus> saveDailyCreatedEmails(@RequestBody MailReportEmailsDTO dto) {
		try {
			String emails = dto.emails() != null ? dto.emails().trim() : "";
			settingService.setValueForKey(CustomerSetting.DAILY_CREATED_REPORT_EMAILS.toString(), emails);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Failed to save daily created report emails", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}