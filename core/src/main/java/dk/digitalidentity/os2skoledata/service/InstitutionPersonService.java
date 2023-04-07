package dk.digitalidentity.os2skoledata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.InstitutionPersonDao;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;

@Service
public class InstitutionPersonService {

	@Autowired
	private InstitutionPersonDao institutionPersonDao;

	public DBInstitutionPerson save(DBInstitutionPerson dbInstitutionPerson) {
		return institutionPersonDao.save(dbInstitutionPerson);
	}

	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public DBInstitutionPerson findByLocalPersonIdIncludingDeleted(String localPersonId) {
		return institutionPersonDao.findByLocalPersonId(localPersonId);
	}
	
	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public DBInstitutionPerson findByLocalPersonId(String localPersonId) {
		return institutionPersonDao.findByLocalPersonIdAndDeletedFalse(localPersonId);
	}

	public List<DBInstitutionPerson> findByInstitution(DBInstitution institution) {
		return institutionPersonDao.findByInstitution(institution);
	}

	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public List<DBInstitutionPerson> findAllIncludingDeleted() {
		return institutionPersonDao.findAll();
	}

	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public List<DBInstitutionPerson>  findAll() {
		return institutionPersonDao.findByDeletedFalse();
	}

	public void saveAll(List<DBInstitutionPerson> entities) {
		institutionPersonDao.saveAll(entities);
	}

	public DBInstitutionPerson findById(long id) {
		return institutionPersonDao.findById(id);
	}

	public List<DBInstitutionPerson> findByIdIn(List<Long> ids) {
		return institutionPersonDao.findByIdIn(ids);
	}

	public List<DBInstitutionPerson> findByPersonCivilRegistrationNumber(String civilRegistrationNumber) {
		return institutionPersonDao.findByPersonCivilRegistrationNumber(civilRegistrationNumber);
	}

}
