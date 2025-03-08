package dk.digitalidentity.os2skoledata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.GroupDao;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import lombok.extern.slf4j.Slf4j;

import dk.digitalidentity.os2skoledata.api.ReadApiController.MiniGroupRecord;

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

	public List<DBGroup> findByInstitutionIdIn(List<Long> ids) {
		return groupDao.findByInstitutionIdInAndDeletedFalse(ids);
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
		}
		catch (Exception ignored) {
			log.debug("Failed to parse level to integer for group with database id " + classDatabaseId);
			return 0;
		}

		return currentYear - level;
	}

	public void sortAndAddStudentMainGroups(List<DBGroup> studentMainGroupGroups, List<Long> studentMainGroup, List<String> studentMainGroupsWorkspace, List<MiniGroupRecord> studentMainGroupsAsObjects, int currentYear) {
		sortByLevel(studentMainGroupGroups);
		studentMainGroup.addAll(studentMainGroupGroups.stream().map(DBGroup::getId).toList());
		studentMainGroupsAsObjects.addAll(studentMainGroupGroups.stream().map(g -> new MiniGroupRecord(g.getId(), getStartYear(g.getGroupLevel(), currentYear, g.getId()), g.getInstitution().getInstitutionName())).toList());
		studentMainGroupsWorkspace.addAll(studentMainGroupGroups.stream().map(DBGroup::getGoogleWorkspaceId).toList());
	}

	private void sortByLevel(List<DBGroup> studentMainGroupGroups) {
		studentMainGroupGroups.sort(
			(o1, o2) -> {
				Integer level1;
				Integer level2;
				
				try {
					level1 = Integer.parseInt(o1.getGroupLevel());
				}
				catch (Exception e) {
					level1 = Integer.MIN_VALUE;
				}
				
				try {
					level2 = Integer.parseInt(o2.getGroupLevel());
				}
				catch (Exception e) {
					level2 = Integer.MIN_VALUE;
				}

				return level2.compareTo(level1);
			}
		);
	}
}
