package dk.digitalidentity.os2skoledata.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import org.springframework.data.jpa.repository.Modifying;
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
    List<DBInstitutionPerson> findByUsername(String username);
    List<DBInstitutionPerson> findByStudentNotNullAndDeletedFalseAndInstitutionIdIn(List<Long> institutionIds);
	void deleteByDeletedTrueAndStilDeletedBefore(LocalDateTime before);
	List<DBInstitutionPerson> findByStudentNotNullAndDeletedFalseAndInstitution(DBInstitution institution);

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

	// for aud table deletion
	@Query(value = "select max(rev) from revinfo where revtstmp < unix_timestamp(current_timestamp - interval 5 year) * 1000;", nativeQuery = true)
	Integer getMaxRev5Years();

	@Modifying
	@Query(value = "delete from contact_person_aud where rev <= :rev", nativeQuery = true)
	void deleteContactPersonAud(int rev);

	@Modifying
	@Query(value = "delete from employee_groupid_aud where rev <= :rev", nativeQuery = true)
	void deleteEmployeeGroupAud(int rev);

	@Modifying
	@Query(value = "delete from role_aud where rev <= :rev", nativeQuery = true)
	void deleteRoleAud(int rev);

	@Modifying
	@Query(value = "delete from employee_aud where rev <= :rev", nativeQuery = true)
	void deleteEmployeeAud(int rev);

	@Modifying
	@Query(value = "delete from extern_groupid_aud where rev <= :rev", nativeQuery = true)
	void deleteExternGroupAud(int rev);

	@Modifying
	@Query(value = "delete from extern_aud where rev <= :rev", nativeQuery = true)
	void deleteExternAud(int rev);

	@Modifying
	@Query(value = "delete from phonenumber_aud where rev <= :rev", nativeQuery = true)
	void deletePhoneNumberAud(int rev);

	@Modifying
	@Query(value = "delete from address_aud where rev <= :rev", nativeQuery = true)
	void deleteAddressAud(int rev);

	@Modifying
	@Query(value = "delete from person_aud where rev <= :rev", nativeQuery = true)
	void deletePersonAud(int rev);

	@Modifying
	@Query(value = "delete from student_aud where rev <= :rev", nativeQuery = true)
	void deleteStudentAud(int rev);

	@Modifying
	@Query(value = "delete from student_groupid_aud where rev <= :rev", nativeQuery = true)
	void deleteStudentGroupAud(int rev);

	@Modifying
	@Query(value = "delete from unilogin_aud where rev <= :rev", nativeQuery = true)
	void deleteUniLoginAud(int rev);

	@Modifying
	@Query(value = "delete from institutionperson_aud where rev <= :rev", nativeQuery = true)
	void deleteInstitutionPersonAud(int rev);

}