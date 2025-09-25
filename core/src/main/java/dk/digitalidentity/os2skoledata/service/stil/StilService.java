package dk.digitalidentity.os2skoledata.service.stil;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import dk.digitalidentity.os2skoledata.service.YearChangeNotificationService;
import https.wsieksport_unilogin_dk.eksport.ImportSource;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.ws.WsiEksportPortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

	@Autowired
	private YearChangeNotificationService yearChangeNotificationService;

	public record InstitutionYearChangeDTO(InstitutionFullMyndighed institutionFullMyndighed, boolean yearChange) {}
	public InstitutionYearChangeDTO getInstitution(String institutionCode) {
		try {
			var response = stilService.eksporterXmlFuldMyndighed(configuration.getStilUsername(), configuration.getStilPassword(), institutionCode);
			boolean yearChange = false;

			// for some reason there is more than one schoolYear
			// fx "2022-2023"
			List<String> importSourceSchoolYears = response.getUNILoginExportFullMyndighed().getImportSource().stream().map(ImportSource::getSchoolYear).toList();
			if (!importSourceSchoolYears.isEmpty()) {
				yearChange = handleSchoolYearsAndCheckForSchoolYearChange(importSourceSchoolYears, institutionCode, response.getUNILoginExportFullMyndighed().getInstitution());
			}

			return new InstitutionYearChangeDTO(response.getUNILoginExportFullMyndighed().getInstitution(), yearChange);
		}
		catch (Exception ex) {
			log.error("Failed to call with institutionCode " + institutionCode, ex);
			return null;
		}
	}

	private boolean handleSchoolYearsAndCheckForSchoolYearChange(List<String> importSourceSchoolYears, String institutionCode, InstitutionFullMyndighed institution) {
		boolean yearChange = false;
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
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		// detect year change and lock institution if year change
		Setting dbSchoolYearSetting = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institutionCode);
		if (dbSchoolYearSetting != null) {
			String dbSchoolYear = dbSchoolYearSetting.getValue();
			if (!Objects.equals(dbSchoolYear, maxImportSourceSchoolYear)) {
				yearChange = true;
				lockInstitution(institutionCode);

				// Record the year change for notification
				String institutionName = institution != null ? institution.getInstitutionName() : "Ukendt institution";
				yearChangeNotificationService.recordYearChange(institutionCode, institutionName, dbSchoolYear, maxImportSourceSchoolYear);
			}
		}

		updateDatabaseSettings(institutionCode, maxSchoolYear, maxImportSourceSchoolYear);

		return yearChange;
	}

	private void updateDatabaseSettings(String institutionCode, int maxSchoolYear, String maxImportSourceSchoolYear) {
		Setting currentYearSetting = settingService.getByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionCode);
		if (currentYearSetting == null) {
			currentYearSetting = new Setting();
			currentYearSetting.setKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionCode);
		}
		currentYearSetting.setValue(maxSchoolYear + "");
		settingService.save(currentYearSetting);

		Setting settingImportSource = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institutionCode);
		if (settingImportSource == null) {
			settingImportSource = new Setting();
			settingImportSource.setKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institutionCode);
		}
		settingImportSource.setValue(maxImportSourceSchoolYear);
		settingService.save(settingImportSource);
	}

	private void lockInstitution(String institutionCode) {
		boolean locked = settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institutionCode);
		if (!locked) {
			Setting lockedSetting = settingService.getByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institutionCode);
			if (lockedSetting == null) {
				lockedSetting = new Setting();
				lockedSetting.setValue("true");
				lockedSetting.setKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institutionCode);
			} else {
				lockedSetting.setValue("true");
			}

			settingService.save(lockedSetting);
		}
	}
}
