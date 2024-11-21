package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstitutionPersonDao extends JpaRepository<DBInstitutionPerson, Long> {
	DBInstitutionPerson findByLocalPersonIdAndInstitutionInstitutionNumber(String localPersonId, String institutionNumber);
	DBInstitutionPerson findByLocalPersonIdAndDeletedFalseAndInstitutionInstitutionNumber(String localPersonId, String institutionNumber);
	DBInstitutionPerson findById(long id);
	DBInstitutionPerson findByIdAndDeletedFalse(long id);
	List<DBInstitutionPerson> findAll();
	List<DBInstitutionPerson> findByDeletedFalse();
	List<DBInstitutionPerson> findByInstitution(DBInstitution institution);
	List<DBInstitutionPerson> findByIdIn(List<Long> ids);
	List<DBInstitutionPerson> findByPersonCivilRegistrationNumber(String civilRegistrationNumber);
    List<DBInstitutionPerson> findByUsernameAndDeletedFalse(String username);
    List<DBInstitutionPerson> findByStudentNotNullAndDeletedFalseAndInstitutionIdIn(List<Long> institutionIds);

	// search in all institutions
	@Query("SELECT ip FROM DBInstitutionPerson ip JOIN ip.person p WHERE " +
			"ip.username IS NOT NULL AND " +
			"(:query IS NULL OR " +
			"LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(p.familyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(ip.username) LIKE LOWER(CONCAT('%', :query, '%')))")
	List<DBInstitutionPerson> searchAllInstitutions(@Param("query") String query);

	// search in specific institution
	@Query("SELECT ip FROM DBInstitutionPerson ip JOIN ip.institution i JOIN ip.person p WHERE " +
			"ip.username IS NOT NULL AND " +
			"i.institutionNumber = :institutionNumber AND " +
			"(:query IS NULL OR " +
			"LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(p.familyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(ip.username) LIKE LOWER(CONCAT('%', :query, '%')))")
	List<DBInstitutionPerson> searchByInstitutionNumber(@Param("institutionNumber") String institutionNumber, @Param("query") String query);

}