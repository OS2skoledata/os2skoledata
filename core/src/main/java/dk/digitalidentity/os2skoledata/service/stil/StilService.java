package dk.digitalidentity.os2skoledata.service.stil;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import https.wsieksport_unilogin_dk.eksport.ImportSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.ws.WsiEksportPortType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class StilService {

	@Autowired
	private WsiEksportPortType stilService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ClientService clientService;

	public InstitutionFullMyndighed getInstitution(String institutionCode) {
		try {
			var response = stilService.eksporterXmlFuldMyndighed(configuration.getStilUsername(), configuration.getStilPassword(), institutionCode);

			// for some reason there is more than one schoolYear
			// fx "2022-2023"
			List<String> importSourceSchoolYears = response.getUNILoginExportFullMyndighed().getImportSource().stream().map(ImportSource::getSchoolYear).toList();
			handleSchoolYears(importSourceSchoolYears, institutionCode);

			return response.getUNILoginExportFullMyndighed().getInstitution();
		}
		catch (Exception ex) {
			log.error("Failed to call with institutionCode " + institutionCode, ex);
			return null;
		}
	}

	private void handleSchoolYears(List<String> importSourceSchoolYears, String institutionCode) {
		int maxSchoolYear = 0;
		String maxImportSourceSchoolYear = null;
		for (String importSourceSchoolYear : importSourceSchoolYears) {
			if (importSourceSchoolYear.length() > 4) {
				try {
					int year = Integer.parseInt(importSourceSchoolYear.substring(0, 4));
					if (year > maxSchoolYear) {
						maxSchoolYear = year;
						maxImportSourceSchoolYear = importSourceSchoolYear;
					}
				} catch (Exception e) {
					continue;
				}
			}
		}

		Setting maxImportSourceSchoolYearDatabaseSetting = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institutionCode);
		if (maxImportSourceSchoolYearDatabaseSetting != null) {
			String maxImportSourceSchoolYearDatabaseValue = maxImportSourceSchoolYearDatabaseSetting.getValue();
			if (!maxImportSourceSchoolYearDatabaseValue.equals(maxImportSourceSchoolYear)) {
				log.error("The import source school year was different from the one in the database. Disabling all clients.");
				for (Client client : clientService.findAll()) {
					client.setPaused(true);
					clientService.save(client);
				}
			}
		} else {
			Setting settingCurrentYear = new Setting();
			settingCurrentYear.setValue(maxSchoolYear + "");
			settingCurrentYear.setKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionCode);
			settingService.save(settingCurrentYear);

			Setting settingImportSource = new Setting();
			settingImportSource.setValue(maxImportSourceSchoolYear);
			settingImportSource.setKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institutionCode);
			settingService.save(settingImportSource);
		}
	}
}
