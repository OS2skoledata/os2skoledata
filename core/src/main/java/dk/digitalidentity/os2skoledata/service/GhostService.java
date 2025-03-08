package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.GhostDao;
import dk.digitalidentity.os2skoledata.dao.model.Ghost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GhostService {

	@Autowired
	private GhostDao ghostDao;

	public List<Ghost> getAllActive() {
		return ghostDao.findByActiveUntilAfter(LocalDate.now().minusDays(1));
	}
	public Ghost findByUsername(String username) {
		return ghostDao.findByUsername(username);
	}

	public void save(Ghost ghost) {
		ghostDao.save(ghost);
	}

	public Ghost findById(long id) {
		return ghostDao.findById(id).orElse(null);
	}

	public void delete(Ghost ghost) {
		ghostDao.delete(ghost);
	}

	public void cleanUpNotActive() {
		ghostDao.deleteByActiveUntilBefore(LocalDate.now());
	}
}
