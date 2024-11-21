package dk.digitalidentity.os2skoledata.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.api.ReadApiController;
import dk.digitalidentity.os2skoledata.api.enums.PersonRole;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.service.model.ChildDTO;
import dk.digitalidentity.os2skoledata.service.model.ContactCardDTO;
import dk.digitalidentity.os2skoledata.service.model.CprLookupDTO;
import dk.digitalidentity.os2skoledata.controller.mvc.enums.SchoolYear;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordResponse;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordResponse.ADPasswordStatus;
import dk.digitalidentity.os2skoledata.service.model.NameDTO;
import dk.digitalidentity.os2skoledata.service.model.StudentPasswordChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.InstitutionPersonDao;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@Slf4j
@EnableCaching
@EnableScheduling
@Service
public class InstitutionPersonService {

	@Autowired
	private InstitutionPersonDao institutionPersonDao;

	@Autowired
	private StudentPasswordChangeConfigurationService studentPasswordChangeConfigurationService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	@Autowired
	private CprService cprService;

	public DBInstitutionPerson save(DBInstitutionPerson dbInstitutionPerson) {
		return institutionPersonDao.save(dbInstitutionPerson);
	}

	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public DBInstitutionPerson findByLocalPersonIdIncludingDeleted(String localPersonId, String institutionNumber) {
		return institutionPersonDao.findByLocalPersonIdAndInstitutionInstitutionNumber(localPersonId, institutionNumber);
	}
	
	// TODO: vil gerne at findAll() faktisk finder alle, men at man explicit kan bede om ikke at få slettede
	public DBInstitutionPerson findByLocalPersonIdAndInstitutionNumber(String localPersonId, String institutionNumber) {
		return institutionPersonDao.findByLocalPersonIdAndDeletedFalseAndInstitutionInstitutionNumber(localPersonId, institutionNumber);
	}

	public DBInstitutionPerson findByIdAndNotDeleted(long id) {
		return institutionPersonDao.findByIdAndDeletedFalse(id);
	}

	public List<DBInstitutionPerson> findByInstitution(DBInstitution institution) {
		return institutionPersonDao.findByInstitution(institution);
	}

	public List<DBInstitutionPerson> findAllIncludingDeleted() {
		return institutionPersonDao.findAll();
	}

	public List<DBInstitutionPerson> findAllNotDeleted() {
		return institutionPersonDao.findByDeletedFalse();
	}

	public void saveAll(List<DBInstitutionPerson> entities) {
		institutionPersonDao.saveAll(entities);
	}

	public DBInstitutionPerson findById(long id) {
		return institutionPersonDao.findById(id);
	}

	public List<DBInstitutionPerson> findByIdIn(List<Long> ids) {
		return institutionPersonDao.findByIdIn(ids);
	}

	public List<DBInstitutionPerson> findByPersonCivilRegistrationNumber(String civilRegistrationNumber) {
		return institutionPersonDao.findByPersonCivilRegistrationNumber(civilRegistrationNumber);
	}

	public PersonRole findGlobalRole(DBInstitutionPerson person, List<DBInstitutionPerson> matchingPeople) {
		if (person.getEmployee() != null) {
			return PersonRole.EMPLOYEE;
		}

		if (matchingPeople.stream().anyMatch(p -> p.getEmployee() != null)) {
			return PersonRole.EMPLOYEE;
		} else if (matchingPeople.stream().anyMatch(p -> p.getStudent() != null)) {
			return PersonRole.STUDENT;
		} else if (matchingPeople.stream().anyMatch(p -> p.getExtern() != null)) {
			return PersonRole.EXTERNAL;
		}

		// will never happen -> people with no roles are skipped above
		return null;
	}

	public DBStudentRole findGlobalStudentRole(List<DBInstitutionPerson> matchingPeople) {
		List<DBStudentRole> studentRoles = new ArrayList<>();
		for (DBInstitutionPerson matchingPerson : matchingPeople) {
			if (matchingPerson.getStudent() != null) {
				studentRoles.add(matchingPerson.getStudent().getRole());
			}
		}

		if (studentRoles.contains(DBStudentRole.STUDERENDE)) {
			return DBStudentRole.STUDERENDE;
		} else if (studentRoles.contains(DBStudentRole.ELEV)) {
			return DBStudentRole.ELEV;
		} else if (studentRoles.contains(DBStudentRole.BARN)) {
			return DBStudentRole.BARN;
		} else if (studentRoles.contains(DBStudentRole.UNKNOWN)) {
			return DBStudentRole.UNKNOWN;
		}
		return null;
	}

	public Set<DBEmployeeRole> findGlobalEmployeeRoles(List<DBInstitutionPerson> matchingPeople) {
		Set<DBEmployeeRole> employeeRoles = new HashSet<>();
		for (DBInstitutionPerson matchingPerson : matchingPeople) {
			if (matchingPerson.getEmployee() != null) {
				employeeRoles.addAll(matchingPerson.getEmployee().getRoles().stream().map(DBRole::getEmployeeRole).collect(Collectors.toSet()));
			}
		}

		return employeeRoles;
	}

	public DBExternalRoleType findGlobalExternalRole(List<DBInstitutionPerson> matchingPeople) {
		List<DBExternalRoleType> externalRoles = new ArrayList<>();
		for (DBInstitutionPerson matchingPerson : matchingPeople) {
			if (matchingPerson.getExtern() != null) {
				externalRoles.add(matchingPerson.getExtern().getRole());
			}
		}

		if (externalRoles.contains(DBExternalRoleType.EKSTERN)) {
			return DBExternalRoleType.EKSTERN;
		} else if (externalRoles.contains(DBExternalRoleType.PRAKTIKANT)) {
			return DBExternalRoleType.PRAKTIKANT;
		} else if (externalRoles.contains(DBExternalRoleType.UNKNOWN)) {
			return DBExternalRoleType.UNKNOWN;
		}
		return null;
	}

	public List<String> findTotalRoles(List<DBInstitutionPerson> matchingPeople) {
		Set<String> roles = new HashSet<>();
		for (DBInstitutionPerson matchingPerson : matchingPeople) {
			if (matchingPerson.getEmployee() != null) {
				roles.addAll(matchingPerson.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole().toString()).collect(Collectors.toList()));
			}
			if (matchingPerson.getExtern() != null) {
				roles.add(matchingPerson.getExtern().getRole().toString());
			}
			if (matchingPerson.getStudent() != null) {
				roles.add(matchingPerson.getStudent().getRole().toString());
			}
		}

		List<String> sortedList = new ArrayList<>(roles);
		Collections.sort(sortedList);
		return sortedList;
	}

    public List<DBInstitutionPerson> findByUsername(String username) {
		return institutionPersonDao.findByUsernameAndDeletedFalse(username);
    }

	public StudentPasswordChangeDTO getStudentIfAllowedPasswordChange(String username) {
		return getStudentsThatPasswordCanBeChangedOnByPerson().stream().filter(p -> p.getUsername().equals(username)).findAny().orElse(null);
	}

	public List<StudentPasswordChangeDTO> getStudentsThatPasswordCanBeChangedOnByPerson() {
		List<StudentPasswordChangeDTO> result = new ArrayList<>();
		String username = SecurityUtil.getUserId();
		List<DBInstitutionPerson> adultInstitutionPeople = findByUsername(username).stream()
				.filter(p -> p.getEmployee() != null || p.getExtern() != null)
				.collect(Collectors.toList());
		List<StudentPasswordChangeConfiguration> configurations = studentPasswordChangeConfigurationService.findAll();

		// no roles that allows changing password or affiliation to students - well, no go then
		if (adultInstitutionPeople.isEmpty()) {
			return result;
		}

		// find all groups from the same institutions that the teacher belongs to
		List<DBGroup> groups = groupService.findByInstitutionIdIn(adultInstitutionPeople.stream()
				.map(p -> p.getInstitution().getId())
				.collect(Collectors.toList()));

		// find all students from the same institutions that the teacher belongs to
		List<DBInstitutionPerson> students = institutionPersonDao.findByStudentNotNullAndDeletedFalseAndInstitutionIdIn(adultInstitutionPeople.stream().map(p -> p.getInstitution().getId()).collect(Collectors.toList()));
		Set<String> handledStudentUsernames = new HashSet<>();
		for (DBInstitutionPerson student : students) {

			// a student can be in multiple institutions, but we only want to show the student in the list once
			List<DBInstitutionPerson> studentsWithSameUsername = students.stream()
					.filter(s -> Objects.equals(s.getUsername(), student.getUsername()))
					.collect(Collectors.toList());
			List<DBGroup> allStudentGroups = new ArrayList<>();

			// if username == null we have not handled the user in AD yet
			if (!StringUtils.hasLength(student.getUsername()) || handledStudentUsernames.contains(student.getUsername())) {
				continue;
			}

			// iterate over all roles that the employee has, to check if one allows access to this student
			boolean canChangePassword = false;
			boolean shouldBeAdded = false;
			for (DBInstitutionPerson loggedInPersonInstitutionPerson : adultInstitutionPeople) {

				// groups for adult institution person
				List<String> groupIdsForEmployee = loggedInPersonInstitutionPerson.getEmployee() == null ? new ArrayList<>() : loggedInPersonInstitutionPerson.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).collect(Collectors.toList());
				List<String> groupIdsForExternal = loggedInPersonInstitutionPerson.getExtern() == null ? new ArrayList<>() : loggedInPersonInstitutionPerson.getExtern().getGroupIds().stream().map(DBExternGroupId::getGroupId).collect(Collectors.toList());
				List<DBGroup> adultInstitutionPersonGroups = groups.stream()
						.filter(g -> g.getInstitution().getId() == loggedInPersonInstitutionPerson.getInstitution().getId())
						.filter(g -> groupIdsForEmployee.contains(g.getGroupId()) || groupIdsForExternal.contains(g.getGroupId()))
						.collect(Collectors.toList());
				List<String> adultInstitutionPersonGroupIds = adultInstitutionPersonGroups.stream()
						.map(g -> g.getGroupId())
						.collect(Collectors.toList());

				List<String> allRolesForPerson = findAdultRoles(loggedInPersonInstitutionPerson);
				for (String role : allRolesForPerson) {

					// find the setting for this role
					StudentPasswordChangeConfiguration roleSetting = configurations.stream()
							.filter(r -> Objects.equals(r.getRole(), StudentPasswordChangerSTILRoles.getFromInstitutionPersonRoleAsString(role)))
							.findFirst()
							.orElse(null);

					if (roleSetting == null || (roleSetting != null && roleSetting.getType().equals(RoleSettingType.CANNOT_CHANGE_PASSWORD))) {
						continue;
					}

					// the role allows changing password on students - now check if THIS student matches the criteria
					for (DBInstitutionPerson institutionStudent : studentsWithSameUsername) {

						// check for same institution, otherwise not relevant (cross-institution is not allowed unless student is in multiple institutions)
						if (institutionStudent.getInstitution().getId() != loggedInPersonInstitutionPerson.getInstitution().getId()) {
							continue;
						}

						List<String> groupIdsForStudent = institutionStudent.getStudent().getGroupIds().stream()
								.map(DBStudentGroupId::getGroupId)
								.collect(Collectors.toList());
						groupIdsForStudent.add(institutionStudent.getStudent().getMainGroupId());
						List<DBGroup> studentGroups = groups.stream()
								.filter(g -> g.getInstitution().getId() == institutionStudent.getInstitution().getId())
								.filter(g -> groupIdsForStudent.contains(g.getGroupId()))
								.collect(Collectors.toList());
						allStudentGroups.addAll(studentGroups);

						switch (roleSetting.getType()) {
							case CAN_CHANGE_PASSWORD_ON_GROUP_MATCH:
								List<String> filterClassTypes = Arrays.asList(roleSetting.getFilter().split(",")).stream()
										.filter(t -> DBImportGroupType.fromPasswordFilter(t) != null)
										.map(t -> DBImportGroupType.fromPasswordFilter(t).toString())
										.collect(Collectors.toList());
								List<String> loggedInPersonSchoolRoleSchoolClassIds = adultInstitutionPersonGroups.stream()
										.filter(c -> filterClassTypes.contains(c.getGroupType().toString()))
										.map(c -> c.getGroupId())
										.collect(Collectors.toList());

								for (DBGroup group : studentGroups) {
									if (loggedInPersonSchoolRoleSchoolClassIds.contains(group.getGroupId())) {
										canChangePassword = true;
										shouldBeAdded = true;
										break;
									}
								}

								break;
							case CAN_CHANGE_PASSWORD_ON_LEVEL_MATCH:
								List<String> filterClassLevels = getLevelFiltersAsNumbers(roleSetting);
								for (DBGroup group : studentGroups) {

									if (group.getGroupLevel() != null && filterClassLevels.contains(group.getGroupLevel())) {
										canChangePassword = true;
										shouldBeAdded = true;
										break;
									}
								}

								break;
							case CANNOT_CHANGE_PASSWORD:
								break;
						}

						if (canChangePassword) {
							break;
						}


						for (DBGroup group : studentGroups) {
							if (adultInstitutionPersonGroupIds.contains(group.getGroupId())) {
								shouldBeAdded = true;
							}
						}
					}

					if (canChangePassword) {
						break;
					}
				}

				if (canChangePassword) {
					break;
				}
			}

			if (shouldBeAdded) {
				String firstname = student.getPerson().getAliasFirstName() != null ? student.getPerson().getAliasFirstName() : student.getPerson().getFirstName();
				String surname = student.getPerson().getAliasFamilyName() != null ? student.getPerson().getAliasFamilyName() : student.getPerson().getFamilyName();
				String uniLogin = student.getUniLogin() != null ? student.getUniLogin().getUserId() : "";
				String institutions = studentsWithSameUsername.stream()
						.map(s -> s.getInstitution().getInstitutionName())
						.collect(Collectors.joining(", "));
				String classes = allStudentGroups.stream()
						.map(DBGroup::getGroupName)
						.collect(Collectors.joining(", "));
				result.add(new StudentPasswordChangeDTO(firstname + " " + surname, student.getUsername(), uniLogin, institutions, classes, canChangePassword));

				handledStudentUsernames.add(student.getUsername());
			}

		}

		return result;
	}

	public Integer getLevel(String username) {
		Integer level = null;
		List<DBInstitutionPerson> people = findByUsername(username);
		if (people.isEmpty()) {
			return null;
		}

		List<DBGroup> groups = groupService.findByInstitutionIdIn(people.stream()
				.map(p -> p.getInstitution().getId())
				.collect(Collectors.toList()));
		for (DBInstitutionPerson person : people) {
			if (person.getStudent() != null) {
				DBGroup mainGroup = groups.stream()
						.filter(g -> g.getInstitution().getId() == person.getInstitution().getId())
						.filter(g -> Objects.equals(g.getGroupId(), person.getStudent().getMainGroupId()))
						.findAny().orElse(null);
				if (mainGroup != null && mainGroup.getGroupLevel() != null) {
					try {
						int currentLevel = Integer.parseInt(mainGroup.getGroupLevel());
						if (level == null || currentLevel > level) {
							level = currentLevel;
						}

					}
					catch (NumberFormatException ignored) {

					}
				}
			}
		}
		return level;
	}

	public ADPasswordResponse.ADPasswordStatus changePassword(String username, String newPassword) throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		ADPasswordStatus adPasswordStatus = ADPasswordStatus.NOOP;
		if (configuration.getStudentAdministration().isEnabled() && StringUtils.hasLength(username)) {
			adPasswordStatus = passwordChangeQueueService.attemptPasswordChangeFromUI(username, newPassword);
		}

		return adPasswordStatus;
	}

	public void handleContactCard(List<DBInstitutionPerson> matchingPeople, List<DBGroup> groups, List<ContactCardDTO> contactCardRecords) {
		for (DBInstitutionPerson matchingPerson : matchingPeople) {
			String rolesForCard = null;
			String groupsForCard = null;

			if (matchingPerson.getEmployee() != null) {
				Set<String> matchingPersonGroupIds = matchingPerson.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).collect(Collectors.toSet());
				groupsForCard = groups.stream().filter(g ->
								matchingPersonGroupIds.contains(g.getGroupId()) &&
										g.getInstitution().getId() == matchingPerson.getInstitution().getId() &&
										g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE)
						)
						.map(DBGroup::getGroupName)
						.collect(Collectors.joining(", "));
				rolesForCard = matchingPerson.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole().getMessage()).collect(Collectors.joining(", "));
			}
			else if (matchingPerson.getStudent() != null) {
				Set<String> matchingPersonGroupIds = matchingPerson.getStudent().getGroupIds().stream().map(DBStudentGroupId::getGroupId).collect(Collectors.toSet());
				Set<String> groupNames = groups.stream().filter(g ->
								matchingPersonGroupIds.contains(g.getGroupId()) &&
										g.getInstitution().getId() == matchingPerson.getInstitution().getId() &&
										g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE)
						)
						.map(DBGroup::getGroupName)
						.collect(Collectors.toSet());
				groupNames.add(groups.stream().filter(g ->  g.getInstitution().getId() == matchingPerson.getInstitution().getId() && g.getGroupId().equals(matchingPerson.getStudent().getMainGroupId())).map(DBGroup::getGroupName).findAny().orElse(null));

				groupsForCard = String.join(", ", groupNames);
				rolesForCard = matchingPerson.getStudent().getRole().getMessage();
			}
			else if (matchingPerson.getExtern() != null) {
				Set<String> matchingPersonGroupIds = matchingPerson.getExtern().getGroupIds().stream().map(DBExternGroupId::getGroupId).collect(Collectors.toSet());
				groupsForCard = groups.stream().filter(g ->
								matchingPersonGroupIds.contains(g.getGroupId()) &&
										g.getInstitution().getId() == matchingPerson.getInstitution().getId() &&
										g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE)
						)
						.map(DBGroup::getGroupName)
						.collect(Collectors.joining(", "));

				rolesForCard = matchingPerson.getExtern().getRole().getMessage();
			}
			else {
				log.warn("Person with id " + matchingPerson.getId() + " is not student, employee or extern. Not returning contact card.");
				continue;
			}

			contactCardRecords.add(new ContactCardDTO(matchingPerson.getInstitution().getGoogleWorkspaceId(), matchingPerson.getInstitution().getInstitutionName(), StringUtils.capitalize(rolesForCard), groupsForCard));

		}
	}


	public NameDTO calculateName(List<DBInstitutionPerson> matchingPeople) {
		boolean ignoreProtection = configuration.isIgnoreNameProtection();
		DBInstitutionPerson personFromSchool = matchingPeople.stream().filter(p -> p.getInstitution().getType().equals(InstitutionType.SCHOOL)).findFirst().orElse(null);
		if (personFromSchool != null) {
			String firstname = personFromSchool.getPerson().getFirstName();
			String surname = personFromSchool.getPerson().getFamilyName();
			if (!ignoreProtection) {
				firstname = personFromSchool.getPerson().getAliasFirstName() == null ? personFromSchool.getPerson().getFirstName() : personFromSchool.getPerson().getAliasFirstName();
				surname = personFromSchool.getPerson().getAliasFamilyName() == null ? personFromSchool.getPerson().getFamilyName() : personFromSchool.getPerson().getAliasFamilyName();
			}
			return new NameDTO(firstname, surname);
		}

		DBInstitutionPerson person = matchingPeople.get(0);
		String firstname = person.getPerson().getFirstName();
		String surname = person.getPerson().getFamilyName();
		if (!ignoreProtection) {
			firstname = person.getPerson().getAliasFirstName() == null ? person.getPerson().getFirstName() : person.getPerson().getAliasFirstName();
			surname = person.getPerson().getAliasFamilyName() == null ? person.getPerson().getFamilyName() : person.getPerson().getAliasFamilyName();
		}

		return new NameDTO(firstname, surname);
	}

	private List<String> getLevelFiltersAsNumbers(StudentPasswordChangeConfiguration setting) {
		List<String> levels = new ArrayList<>();
		if (setting.getFilter() != null && !setting.getFilter().isEmpty()) {
			List<String> filterYears = Arrays.asList(setting.getFilter().split(","));
			levels.addAll(filterYears.stream().map(f -> SchoolYear.valueOf(f).getLevel()).collect(Collectors.toList()));
		}

		return levels;
	}

	private List<String> findAdultRoles(DBInstitutionPerson loggedInPersonInstitutionPerson) {
		List<String> roles = new ArrayList<>();
		if (loggedInPersonInstitutionPerson.getEmployee() != null) {
			roles.addAll(loggedInPersonInstitutionPerson.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole().toString()).collect(Collectors.toList()));
		}
		if (loggedInPersonInstitutionPerson.getExtern() != null) {
			roles.add(loggedInPersonInstitutionPerson.getExtern().getRole().toString());
		}
		return roles;
	}

	@Cacheable("getChildren")
	public List<StudentPasswordChangeDTO> getChildrenPasswordAllowed(String cpr) {
		List<StudentPasswordChangeDTO> result = new ArrayList<>();
		Map<String, List<DBInstitutionPerson>> usernameInstitutionPersonMap = new HashMap<>();

		Future<CprLookupDTO> cprFuture = cprService.getByCpr(cpr);
		CprLookupDTO personLookup = null;

		try {
			personLookup = (cprFuture != null) ? cprFuture.get(5, TimeUnit.SECONDS) : null;
		}
		catch (InterruptedException | ExecutionException | TimeoutException ex) {
			log.warn("Got a timeout on lookup of children", ex);
			return result;
		}

		if (personLookup != null && personLookup.getChildren() != null && !personLookup.getChildren().isEmpty()) {
			for (ChildDTO child : personLookup.getChildren()) {
				List<DBInstitutionPerson> childPersons = findByPersonCivilRegistrationNumber(child.getCpr());
				for (DBInstitutionPerson person : childPersons) {
					if (isAdult(child.getCpr()) || person.getUsername() == null || person.getStudent() == null) {
						continue;
					}

					if (!usernameInstitutionPersonMap.containsKey(person.getUsername())) {
						usernameInstitutionPersonMap.put(person.getUsername(), new ArrayList<>());
					}

					usernameInstitutionPersonMap.get(person.getUsername()).add(person);
				}
			}
		}

		List<DBGroup> allGroups = groupService.findAllNotDeleted();
		for (Map.Entry<String, List<DBInstitutionPerson>> usernamePersonList : usernameInstitutionPersonMap.entrySet()) {
			String username = usernamePersonList.getKey();
			if (!usernamePersonList.getValue().isEmpty()) {
				DBInstitutionPerson student = usernamePersonList.getValue().get(0);
				String firstname = student.getPerson().getAliasFirstName() != null ? student.getPerson().getAliasFirstName() : student.getPerson().getFirstName();
				String surname = student.getPerson().getAliasFamilyName() != null ? student.getPerson().getAliasFamilyName() : student.getPerson().getFamilyName();
				String uniLogin = student.getUniLogin() != null ? student.getUniLogin().getUserId() : "";
				String institutions = usernamePersonList.getValue().stream()
						.map(s -> s.getInstitution().getInstitutionName())
						.collect(Collectors.joining(", "));
				String classes = findAllClasses(usernamePersonList.getValue(), allGroups).stream()
						.map(DBGroup::getGroupName)
						.collect(Collectors.joining(", "));
				result.add(new StudentPasswordChangeDTO(firstname + " " + surname, username, uniLogin, institutions, classes, true));
			}
		}

		return result;
	}

	private List<DBGroup> findAllClasses(List<DBInstitutionPerson> people, List<DBGroup> allGroups) {
		List<DBGroup> allStudentGroups = new ArrayList<>();
		for (DBInstitutionPerson person : people) {
			List<String> groupIdsForStudent = person.getStudent().getGroupIds().stream()
					.map(DBStudentGroupId::getGroupId)
					.collect(Collectors.toList());
			groupIdsForStudent.add(person.getStudent().getMainGroupId());
			List<DBGroup> studentGroups = allGroups.stream()
					.filter(g -> g.getInstitution().getId() == person.getInstitution().getId())
					.filter(g -> groupIdsForStudent.contains(g.getGroupId()))
					.collect(Collectors.toList());
			allStudentGroups.addAll(studentGroups);
		}

		return allStudentGroups;
	}

	private boolean isAdult(String cpr) {
		LocalDate birthday = getBirthdayFromCpr(cpr);
		return LocalDate.from(birthday).until(LocalDate.now(), ChronoUnit.YEARS) >= 16;
	}

	private LocalDate getBirthdayFromCpr(String cpr) {
		String day = cpr.substring(0, 2);
		String month = cpr.substring(2, 4);
		String yearString = cpr.substring(4, 6);
		int year = Integer.parseInt(yearString);

		switch (cpr.charAt(6)) {
			case '0':
			case '1':
			case '2':
			case '3':
				yearString = "19" + yearString;
				break;
			case '4':
			case '9':
				if (year <= 36) {
					yearString = "20" + yearString;
				}
				else {
					yearString = "19" + yearString;
				}
				break;
			case '5':
			case '6':
			case '7':
			case '8':
				if (year <= 57) {
					yearString = "20" + yearString;
				}
				else {
					yearString = "18" + yearString;
				}
				break;
			default:
				return null;
		}

		String dateString = yearString + "-" + month + "-" + day;
		LocalDate birthDate = null;
		try {
			birthDate = LocalDate.parse(dateString);
		}
		catch (Exception ex) {
			return null;
		}

		return birthDate;
	}

	@Scheduled(fixedRate = 1 * 60 * 1000)
	@CacheEvict(value = "getChildren", allEntries = true)
	public void clearGetChildrenCache() {
		// Clear cache
	}

	public List<DBInstitutionPerson> searchAllInstitutions(String term) {
		return institutionPersonDao.searchAllInstitutions(term);
	}

	public List<DBInstitutionPerson> searchByInstitutionNumber(String institutionNumber, String term) {
		return institutionPersonDao.searchByInstitutionNumber(institutionNumber, term);
	}
}
