package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class SettingsApiController {

	@Autowired
	private SettingService settingService;

	@GetMapping("/api/settings/azure/schemaId")
	public ResponseEntity<?> getAzureSchemaId() {
		String schemaId = settingService.getStringValueByKey(CustomerSetting.AZURE_SCHEMA_ID);
		return ResponseEntity.ok(schemaId);
	}

	@PostMapping("/api/settings/azure/schemaId")
	public ResponseEntity<?> setAzureSchemaId(@RequestBody String schemaId) {
		settingService.setValueForKey(CustomerSetting.AZURE_SCHEMA_ID.toString(), schemaId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
