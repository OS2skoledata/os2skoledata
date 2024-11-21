package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.ModificationHistory;

public interface ModificationHistoryDao extends JpaRepository<ModificationHistory, Long> {
	DBInstitutionPerson findById(long id);
	
	List<ModificationHistory> findAll();

	List<ModificationHistory> findByIdGreaterThan(long offset);

	List<ModificationHistory> findByinstitutionIdAndIdGreaterThan(long institutionId, long offset);
}