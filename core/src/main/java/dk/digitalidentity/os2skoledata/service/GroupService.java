package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.api.model.MiniGroupDTO;
import dk.digitalidentity.os2skoledata.dao.GroupDao;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.GoogleWorkspaceClassFolderOrGroup;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.FolderOrGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.model.PrintGroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupService {

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private StudentPasswordChangeConfigurationService studentPasswordChangeConfigurationService;

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

	public void sortAndAddStudentMainGroups(List<DBGroup> studentMainGroupGroups, List<Long> studentMainGroup, List<String> studentMainGroupsWorkspace, List<MiniGroupDTO> studentMainGroupsAsObjects, int currentYear, DBInstitutionPerson primaryPerson) {
		sortByLevel(studentMainGroupGroups);
		studentMainGroup.addAll(studentMainGroupGroups.stream().map(DBGroup::getId).toList());
		studentMainGroupsAsObjects.addAll(studentMainGroupGroups.stream().map(g ->
				new MiniGroupDTO(g.getId(), getStartYear(g.getGroupLevel(), currentYear, g.getId()),
						         g.getInstitution().getInstitutionName(), g.getInstitution().getType(),
						         g.getGoogleWorkspaceId(), isPrimary(g, primaryPerson))).toList());
		studentMainGroupsWorkspace.addAll(studentMainGroupGroups.stream().map(DBGroup::getGoogleWorkspaceId).toList());
	}

	private boolean isPrimary(DBGroup group, DBInstitutionPerson primaryPerson) {
		if (primaryPerson == null) {
			return false;
		}

		if (primaryPerson.getInstitution().getId() == group.getInstitution().getId()) {
			return true;
		}

		return false;
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

	public List<PrintGroupDTO> getClassesForPrint() {
		List<PrintGroupDTO> result = new ArrayList<>();
		Set<Long> canChangePasswordIds = new HashSet<>();
		List<DBGroup> allGroupsForLoggedInPerson = new ArrayList<>();

		String username = SecurityUtil.getUserId();
		List<DBInstitutionPerson> adultInstitutionPeople = institutionPersonService.findByUsernameAndDeletedFalse(username).stream()
				.filter(p -> p.getEmployee() != null || p.getExtern() != null)
				.collect(Collectors.toList());
		List<StudentPasswordChangeConfiguration> configurations = studentPasswordChangeConfigurationService.findAll();

		// quick abort
		if (adultInstitutionPeople.isEmpty()) {
			return result;
		}

		// find all groups from the same institutions that the employee belongs to
		List<DBGroup> groups = findByInstitutionIdIn(adultInstitutionPeople.stream()
				.map(p -> p.getInstitution().getId())
				.collect(Collectors.toList()));

		for (DBInstitutionPerson loggedInPersonInstitutionPerson : adultInstitutionPeople) {

			// groups for adult institution person
			List<String> groupIdsForEmployee = loggedInPersonInstitutionPerson.getEmployee() == null ? new ArrayList<>() : loggedInPersonInstitutionPerson.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).collect(Collectors.toList());
			List<String> groupIdsForExternal = loggedInPersonInstitutionPerson.getExtern() == null ? new ArrayList<>() : loggedInPersonInstitutionPerson.getExtern().getGroupIds().stream().map(DBExternGroupId::getGroupId).collect(Collectors.toList());
			List<DBGroup> adultInstitutionPersonGroups = groups.stream()
					.filter(g -> g.getInstitution().getId() == loggedInPersonInstitutionPerson.getInstitution().getId())
					.filter(g -> groupIdsForEmployee.contains(g.getGroupId()) || groupIdsForExternal.contains(g.getGroupId()))
					.collect(Collectors.toList());
			allGroupsForLoggedInPerson.addAll(adultInstitutionPersonGroups);

			List<DBGroup> groupsForInstitution = groups.stream().filter(g -> g.getInstitution().getId() == loggedInPersonInstitutionPerson.getInstitution().getId()).collect(Collectors.toList());

			List<String> allRolesForPerson = institutionPersonService.findAdultRoles(loggedInPersonInstitutionPerson);
			for (String role : allRolesForPerson) {

				// find the setting for this role
				StudentPasswordChangeConfiguration roleSetting = configurations.stream()
						.filter(r -> Objects.equals(r.getRole(), StudentPasswordChangerSTILRoles.getFromInstitutionPersonRoleAsString(role)))
						.findFirst()
						.orElse(null);

				if (roleSetting == null || (roleSetting.getType().equals(RoleSettingType.CANNOT_CHANGE_PASSWORD))) {
					continue;
				}

				// find the groups where password change is allowed to find out if show password is possible (only level <= 3)
				switch (roleSetting.getType()) {
					case CAN_CHANGE_PASSWORD_ON_GROUP_MATCH:
						List<String> filterClassTypes = Arrays.asList(roleSetting.getFilter().split(",")).stream()
								.filter(t -> DBImportGroupType.fromPasswordFilter(t) != null)
								.map(t -> DBImportGroupType.fromPasswordFilter(t).toString())
								.collect(Collectors.toList());
						List<Long> loggedInPersonSchoolRoleSchoolClassIds = adultInstitutionPersonGroups.stream()
								.filter(c -> filterClassTypes.contains(c.getGroupType().toString()) && isYoungGroup(c))
								.map(DBGroup::getId)
								.collect(Collectors.toList());
						canChangePasswordIds.addAll(loggedInPersonSchoolRoleSchoolClassIds);

						break;
					case CAN_CHANGE_PASSWORD_ON_LEVEL_MATCH:
						List<String> filterClassLevels = institutionPersonService.getLevelFiltersAsNumbers(roleSetting);
						for (DBGroup group : groupsForInstitution) {
							if (group.getGroupLevel() != null && filterClassLevels.contains(group.getGroupLevel()) && isYoungGroup(group)) {
								canChangePasswordIds.add(group.getId());
							}
						}

						break;
					case CANNOT_CHANGE_PASSWORD:
						break;
				}
			}
		}

		for (DBGroup group : allGroupsForLoggedInPerson) {
			result.add(new PrintGroupDTO(group.getId(), group.getGroupName(), group.getGroupLevel(), group.getInstitution().getInstitutionName(), canChangePasswordIds.contains(group.getId())));
		}

		return result;
	}

	private boolean isYoungGroup(DBGroup group) {
		int level;
		try {
			level = Integer.parseInt(group.getGroupLevel());
		}
		catch (Exception ignored) {
			return false;
		}

		return level <= 3;
	}

	public void resetAllYearlyIds() {
		List<DBGroup> toSave = new ArrayList<>();
		for (DBGroup dbGroup : findAll()) {
			dbGroup.setCurrentYearGWFolderIdentifier(null);
			dbGroup.setCurrentYearGWGroupIdentifier(null);
			toSave.add(dbGroup);
		}
		groupDao.saveAll(toSave);
	}
}
