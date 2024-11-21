package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AuditLogDao extends JpaRepository<AuditLog, Long> {
	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM auditlogs WHERE tts < ?1")
	void deleteByTtsBefore(LocalDateTime before);
}
