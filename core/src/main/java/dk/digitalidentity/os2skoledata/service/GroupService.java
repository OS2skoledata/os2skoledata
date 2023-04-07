package dk.digitalidentity.os2skoledata.service;

import java.util.Calendar;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.GroupDao;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;

@Slf4j
@Service
public class GroupService {

	@Autowired
	private GroupDao groupDao;

	public DBGroup findById(long id) {
		return groupDao.findById(id);
	}

	public List<DBGroup> findAll() {
		return groupDao.findAll();
	}

	// TODO deleted?
	public List<DBGroup> findByInstitution(DBInstitution institution) {
		return groupDao.findByInstitution(institution);
	}

	public List<DBGroup> findAllNotDeleted() {
		return groupDao.findByDeletedFalse();
	}

	public List<DBGroup> findByIdIn(List<Long> ids) {
		return groupDao.findByIdIn(ids);
	}

	public DBGroup save(DBGroup group) {
		return groupDao.save(group);
	}

	public int getStartYear(String levelString, int currentYear, long classDatabaseId) {
		if (currentYear == 0) {
			return 0;
		}

		int level;
		try {
			level = Integer.parseInt(levelString);
		} catch (Exception e) {
			log.warn("Failed to parse level to integer for group with database id " + classDatabaseId);
			return 0;
		}

		return currentYear - level;
	}
}
