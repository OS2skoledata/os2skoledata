package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;

public interface InstitutionPersonDao extends JpaRepository<DBInstitutionPerson, Long> {
	DBInstitutionPerson findByLocalPersonId(String localPersonId);
	DBInstitutionPerson findByLocalPersonIdAndDeletedFalse(String localPersonId);
	DBInstitutionPerson findById(long id);
	
	List<DBInstitutionPerson> findAll();
	List<DBInstitutionPerson> findByDeletedFalse();
	List<DBInstitutionPerson> findByInstitution(DBInstitution institution);
	List<DBInstitutionPerson> findByIdIn(List<Long> ids);
	List<DBInstitutionPerson> findByPersonCivilRegistrationNumber(String civilRegistrationNumber);
}