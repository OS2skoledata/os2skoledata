package dk.digitalidentity.os2skoledata.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.digitalidentity.os2skoledata.dao.model.InstitutionGoogleWorkspaceGroupMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.InstitutionDao;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;

@Service
public class InstitutionService {

	@Autowired
	private InstitutionDao institutionDao;

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

	public Map<String, String> generateEmailMap(DBInstitution institution) {
		Map<String, String> result = new HashMap<>();
		if (institution.getGoogleWorkspaceGroupEmailMappings() != null) {
			for (InstitutionGoogleWorkspaceGroupMapping mapping : institution.getGoogleWorkspaceGroupEmailMappings()) {
				if (!result.containsKey(mapping.getGroupKey())) {
					result.put(mapping.getGroupKey(), mapping.getGroupEmail());
				}
			}
		}
		return result;
	}
}
