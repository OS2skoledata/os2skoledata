package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.CprPasswordMappingDao;
import dk.digitalidentity.os2skoledata.dao.model.CprPasswordMapping;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CprPasswordMappingService {

	@Autowired
	private CprPasswordMappingDao cprPasswordMappingDao;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	public String getDecryptedPassword(String cpr) {
		CprPasswordMapping mapping = cprPasswordMappingDao.findByCpr(cpr);
		if (mapping != null) {
			try {
				return passwordChangeQueueService.decryptPassword(mapping.getPassword());
			} catch (Exception e) {
				log.error("Failed to decrypt password", e);
				return null;
			}
		}
		return null;
	}

	public void setPassword(String cpr, String encryptedPassword) {
		CprPasswordMapping mapping = cprPasswordMappingDao.findByCpr(cpr);
		if (mapping == null) {
			mapping = new CprPasswordMapping();
			mapping.setCpr(cpr);
		}

		mapping.setPassword(encryptedPassword);

		cprPasswordMappingDao.save(mapping);
	}

	public boolean exists(String cpr) {
		CprPasswordMapping mapping = cprPasswordMappingDao.findByCpr(cpr);
		return mapping != null;
	}

	@Transactional(rollbackFor = Exception.class)
	public void cleanupPasswords() {
		List<DBInstitutionPerson> people = institutionPersonService.findAllIncludingDeleted();
		Set<String> cprs = people.stream().map(p -> p.getPerson().getCivilRegistrationNumber()).collect(Collectors.toSet());
		List<CprPasswordMapping> cprPasswordMappings = cprPasswordMappingDao.findAll();
		List<CprPasswordMapping> toDelete = cprPasswordMappings.stream().filter(c -> !cprs.contains(c.getCpr())).collect(Collectors.toList());
		cprPasswordMappingDao.deleteAll(toDelete);
	}

}
