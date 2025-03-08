package dk.digitalidentity.os2skoledata.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dk.digitalidentity.os2skoledata.dao.model.SecurityLog;

public interface SecurityLogDao extends CrudRepository<SecurityLog, Long> {

	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM security_log WHERE timestamp < ?1 LIMIT 25000")
	void deleteByTimestampBefore(Date before);

}