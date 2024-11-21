package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ReplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordChangeQueueDao extends JpaRepository<PasswordChangeQueue, Long> {
	List<PasswordChangeQueue> findByUsernameAndStatusNot(String username, ReplicationStatus replicationStatus);
	List<PasswordChangeQueue> findByStatus(ReplicationStatus status);
	List<PasswordChangeQueue> findByStatusNotIn(ReplicationStatus... replicationStatus);
}
