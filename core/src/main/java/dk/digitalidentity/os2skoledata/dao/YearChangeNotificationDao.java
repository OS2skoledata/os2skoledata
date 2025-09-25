package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.YearChangeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface YearChangeNotificationDao extends JpaRepository<YearChangeNotification, Long> {

	Optional<YearChangeNotification> findByInstitutionNumberAndYearChangeDate(String institutionNumber, LocalDate yearChangeDate);

	List<YearChangeNotification> findByYearChangeDateAndInitialNotificationSentFalse(LocalDate yearChangeDate);

	@Query("SELECT yn FROM YearChangeNotification yn WHERE yn.yearChangeDate <= :cutoffDate AND yn.reminderSent = false AND yn.resolved = false AND yn.initialNotificationSent = true")
	List<YearChangeNotification> findUnresolvedOlderThan(@Param("cutoffDate") LocalDate cutoffDate);

	@Query("SELECT COUNT(yn) FROM YearChangeNotification yn WHERE yn.resolved = false")
	long countUnresolved();

	List<YearChangeNotification> findByResolvedFalseAndInstitutionNumber(String institutionNumber);
}