package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;

public interface InstitutionDao extends JpaRepository<DBInstitution, Long> {
	DBInstitution findByInstitutionNumber(String institutionNumber);
	DBInstitution findById(long id);

	List<DBInstitution> findAll();
	List<DBInstitution> findByIdIn(List<Long> ids);
}