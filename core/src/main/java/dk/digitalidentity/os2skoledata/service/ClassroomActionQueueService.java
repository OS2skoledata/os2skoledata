package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.ClassroomActionQueueDao;
import dk.digitalidentity.os2skoledata.dao.model.ClassroomActionQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomActionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomActionQueueService {

	@Autowired
	private ClassroomActionQueueDao classroomActionQueueDao;

	public List<ClassroomActionQueue> getAll() {
		return classroomActionQueueDao.findAll();
	}

	public List<ClassroomActionQueue> getAllPending() {
		return classroomActionQueueDao.findByStatus(ClassroomActionStatus.WAITING);
	}


	public void save(ClassroomActionQueue queue) {
		classroomActionQueueDao.save(queue);
	}

	public ClassroomActionQueue getById(long id) {
		return classroomActionQueueDao.findById(id);
	}
}
