package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.ClassroomActionQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomActionQueueDao  extends JpaRepository<ClassroomActionQueue, Long> {
	List<ClassroomActionQueue> findByStatus(ClassroomActionStatus status);
	ClassroomActionQueue findById(long id);
}
