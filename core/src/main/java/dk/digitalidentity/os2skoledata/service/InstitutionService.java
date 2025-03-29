package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.InstitutionDao;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionGroupIdentifierMapping;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.IntegrationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InstitutionService {

	@Autowired
	private InstitutionDao institutionDao;

	@Autowired
	private SettingService settingService;

	// TODO this is used in api. should we filter deleted?
	public DBInstitution findByInstitutionNumber(String institutionNumber) {
		return institutionDao.findByInstitutionNumber(institutionNumber);
	}

	public DBInstitution save(DBInstitution dbInstitution) {
		return institutionDao.save(dbInstitution);
	}

	public DBInstitution findById(long id) {
		return institutionDao.findById(id);
	}

	public List<DBInstitution> findAll() {
		return institutionDao.findAll();
	}

	public List<DBInstitution> findByIdIn(List<Long> ids) {
		return institutionDao.findByIdIn(ids);
	}

	public Map<String, String> generateEmailMap(DBInstitution institution, IntegrationType integrationType) {
		Map<String, String> result = new HashMap<>();
		if (institution.getIntegrationGroupIdentifierMappings() != null) {
			for (InstitutionGroupIdentifierMapping mapping : institution.getIntegrationGroupIdentifierMappings()) {
				if (mapping.getIntegrationType().equals(integrationType) && !result.containsKey(mapping.getGroupKey())) {
					result.put(mapping.getGroupKey(), mapping.getGroupIdentifier());
				}
			}
		}
		return result;
	}

	public String findCurrentSchoolYearForGWEmails(DBInstitution institution) {
		Setting setting = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institution.getInstitutionNumber());
		if (setting == null || setting.getValue() == null) {
			log.error("Institution missing school year from STIL: " + institution.getInstitutionNumber());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution missing school year from STIL: " + institution.getInstitutionNumber());
		}

		String[] years = setting.getValue().split("-");
		return years[0].substring(2, 4) + "/" + years[1].substring(2, 4);
	}
}
