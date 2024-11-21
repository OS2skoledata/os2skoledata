package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogApiController {

	@Autowired
	private SettingService settingService;

	record ErrorRecord(String message) {}

	@PostMapping("/api/reporterror")
	public ResponseEntity<?> reportError(@RequestBody ErrorRecord errorRecord) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		log.error("Received error from Client with id " + client.getId() + " and name " + client.getName() + ". Message: " + errorRecord.message());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/uploadLog")
	public ResponseEntity<?> uploadLog() {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		boolean hasRequestedLog = false;
		Setting setting = settingService.getByKey(CustomerSetting.REQUEST_LOG_.toString() + client.getId());
		if (setting != null && setting.getValue().equalsIgnoreCase("true")) {
			log.info("Log has been requested for client with name " + client.getName() + " and id " + client.getId());
			hasRequestedLog = true;
			setting.setValue("false");
			settingService.save(setting);
		}

		return new ResponseEntity<>(hasRequestedLog, HttpStatus.OK);
	}
}
