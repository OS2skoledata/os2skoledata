package dk.digitalidentity.os2skoledata.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.api.model.GroupDTO;
import dk.digitalidentity.os2skoledata.api.model.InstitutionDTO;
import dk.digitalidentity.os2skoledata.api.model.InstitutionPersonDTO;
import dk.digitalidentity.os2skoledata.api.model.MiniGroupDTO;
import dk.digitalidentity.os2skoledata.api.model.enums.Action;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClientAccessRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.FolderOrGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.IntegrationType;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.Ghost;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.CprPasswordMappingService;
import dk.digitalidentity.os2skoledata.service.GhostService;
import dk.digitalidentity.os2skoledata.service.GoogleWorkspaceClassFolderOrGroupService;
import dk.digitalidentity.os2skoledata.service.model.ContactCardDTO;
import dk.digitalidentity.os2skoledata.service.model.NameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;

import dk.digitalidentity.os2skoledata.api.model.enums.PersonRole;
import dk.digitalidentity.os2skoledata.api.model.enums.SetFieldType;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionGroupIdentifierMapping;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBGender;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReadApiController {

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private GhostService ghostService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private CprPasswordMappingService cprPasswordMappingService;

	@Autowired
	private GoogleWorkspaceClassFolderOrGroupService groupOrFolderService;

	@GetMapping("/api/institutions")
	public ResponseEntity<?> getInstitutions() {
		List<InstitutionDTO> institutions = institutionService.findAll().stream()
				.map(i -> institutionService.getInstitutionDTO(i)).toList();
		return ResponseEntity.ok(institutions);
	}
	
	@GetMapping("/api/persons")
	public ResponseEntity<?> getInstitutionPersons(@RequestParam(name = "institutionNumber") String institutionNumber) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return ResponseEntity.badRequest().body("Institution with number: " + institutionNumber + " not found.");
		}

		boolean locked = settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institution.getInstitutionNumber());
		if (locked) {
			return ResponseEntity.badRequest().body("Institution with number: " + institutionNumber + " is locked due to school year change.");
		}

		int currentYear = settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionNumber);
		List<DBInstitutionPerson> persons = institutionPersonService.findByInstitution(institution);
		List<DBGroup> groups = groupService.findAllNotDeleted();
		List<InstitutionPersonDTO> result = new ArrayList<>();

		List<DBInstitutionPerson> allInstitutionPerson = institutionPersonService.findAllIncludingDeleted();

		Map<String, List<DBInstitutionPerson>> institutionPersonMap = allInstitutionPerson.stream().collect(Collectors.groupingBy(i -> i.getPerson().getCivilRegistrationNumber()));

		for (DBInstitutionPerson person : persons) {
			if (person.isDeleted()) {
				continue;
			}

			String cpr = person.getPerson().getCivilRegistrationNumber();
			List<ContactCardDTO> contactCardRecords = new ArrayList<>();
			List<Long> studentMainGroup = new ArrayList<>();
			List<MiniGroupDTO> studentMainGroupsAsObjects = new ArrayList<>();
			List<String> studentMainGroupsWorkspace = new ArrayList<>();
			List<DBGroup> studentMainGroupGroups = new ArrayList<>();
			String stilMainGroupCurrentInstitution = null;
			List<String> stilGroupsCurrentInstitution = new ArrayList<>();
			PersonRole role = null;
			List<Long> groupIds = new ArrayList<>();
			DBStudentRole studentRole = null;
			List<DBEmployeeRole> employeeRole = new ArrayList<>();
			DBExternalRoleType externalRole = null;
			int studentMainGroupStartYearForInstitution = 0;
			String studentMainGroupLevelForInstitution = null;
			// load all of them into memory, and then do lookup in-mem instead
			List<DBInstitutionPerson> matchingPeopleIncludingDeleted = institutionPersonMap.get(cpr);

			List<DBInstitutionPerson> matchingPeople = matchingPeopleIncludingDeleted.stream().filter(m -> !m.isDeleted()).collect(Collectors.toList());
			List<DBInstitutionPerson> matchingPeopleDeleted = matchingPeopleIncludingDeleted.stream().filter(m -> m.isDeleted()).collect(Collectors.toList());
			List<InstitutionDTO> institutions = new ArrayList<>(matchingPeople.stream().map(p -> institutionService.getInstitutionDTO(p.getInstitution())).toList());
			String stilUsername = null;
			String uniId = null;
			boolean setPasswordOnCreate = false;
			String password = null;
			DBInstitutionPerson primaryPerson = matchingPeople.stream().filter(p -> p.isPrimaryInstitution()).findAny().orElse(null);

			if (person.getEmployee() != null) {
				role = PersonRole.EMPLOYEE;
				stilGroupsCurrentInstitution.addAll(person.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).collect(Collectors.toList()));
				for (DBEmployeeGroupId groupId : person.getEmployee().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				employeeRole = person.getEmployee().getRoles().stream().map(DBRole::getEmployeeRole).toList();
			}
			else if (person.getStudent() != null) {
				role = PersonRole.STUDENT;
				stilGroupsCurrentInstitution.addAll(person.getStudent().getGroupIds().stream().map(DBStudentGroupId::getGroupId).collect(Collectors.toList()));
				for (DBStudentGroupId groupId : person.getStudent().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				for (DBInstitutionPerson matchingPerson : matchingPeople) {
					if (matchingPerson.getStudent() != null) {
						DBGroup group = groups.stream().filter(g -> g.getInstitution().getId() == matchingPerson.getInstitution().getId() && g.getGroupId().equals(matchingPerson.getStudent().getMainGroupId())).findAny().orElse(null);
						if (group != null) {
							studentMainGroupGroups.add(group);
						}
					}
				}
				studentRole = person.getStudent().getRole();
				DBGroup mainGroupForInstitution = groups.stream().filter(g -> g.getInstitution().getId() == person.getInstitution().getId() && g.getGroupId().equals(person.getStudent().getMainGroupId())).findAny().orElse(null);
				if (mainGroupForInstitution != null) {

					// check if student is in main group with a future date. If so, skip user
					if (configuration.isFilterOutGroupsWithFutureFromDate()) {
						LocalDate futureDate = LocalDate.now().plusDays(configuration.getCreateGroupsXDaysBeforeFromDate() + 1);
						if (mainGroupForInstitution.getFromDate() != null && mainGroupForInstitution.getFromDate().isAfter(futureDate)) {
							continue;
						}
					}

					studentMainGroupStartYearForInstitution = groupService.getStartYear(mainGroupForInstitution.getGroupLevel(), currentYear, mainGroupForInstitution.getId());
					studentMainGroupLevelForInstitution = mainGroupForInstitution.getGroupLevel();
					stilMainGroupCurrentInstitution = mainGroupForInstitution.getGroupId();
				}
			}
			else if (person.getExtern() != null) {
				stilGroupsCurrentInstitution.addAll(person.getExtern().getGroupIds().stream().map(DBExternGroupId::getGroupId).collect(Collectors.toList()));
				role = PersonRole.EXTERNAL;
				for (DBExternGroupId groupId : person.getExtern().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				externalRole = person.getExtern().getRole();
			}
			else {
				log.warn("Person with id " + person.getId() + " is not student, employee or extern. Not returning it through API.");
				continue;
			}

			groupService.sortAndAddStudentMainGroups(studentMainGroupGroups, studentMainGroup, studentMainGroupsWorkspace, studentMainGroupsAsObjects, currentYear, primaryPerson);

			if (person.getUniLogin() != null) {
				uniId = person.getUniLogin().getUserId();

				// length = 8
				// this is to sort out the new uniLogin userIds that could look something like this 1000846a98
				if (person.getUniLogin().getUserId().length() == 8) {
					stilUsername = person.getUniLogin().getUserId();
				}
			}

			// if username is null (institutionPerson is new) check if any other institutionPerson with same cpr has an username - if so use the same
			if (person.getUsername() == null) {

				// check for username on active people
				String usernameMatch = null;
				for (DBInstitutionPerson matchingPerson : matchingPeople) {
					if (matchingPerson.getUsername() != null) {
						usernameMatch = matchingPerson.getUsername();
						break;
					}
				}

				if (usernameMatch != null) {
					person.setUsername(usernameMatch);
					institutionPersonService.save(person);
				}
				else {
					// if no active people with username then check inactive people
					for (DBInstitutionPerson matchingPerson : matchingPeopleDeleted) {
						if (matchingPerson.getUsername() != null) {
							usernameMatch = matchingPerson.getUsername();
							break;
						}
					}

					if (usernameMatch != null) {
						person.setUsername(usernameMatch);
						institutionPersonService.save(person);
					}
				}
			}

			// contactCards in Google Workspace
			institutionPersonService.handleContactCard(matchingPeople, groups, contactCardRecords);

			// prioritise name from school institution over daycare if multiple institutions
			NameDTO calculatedName = institutionPersonService.calculateName(matchingPeople);

			// handle password
			if (SecurityUtil.hasRole("ROLE_API_" + ClientAccessRole.PASSWORD_ACCESS.toString())) {

				setPasswordOnCreate = configuration.getStudentAdministration().isSetIndskolingPasswordOnCreate() &&
						institutionPersonService.getLevel(person) != null && institutionPersonService.getLevel(person) <= 3 &&
						cprPasswordMappingService.exists(cpr);
				password = cprPasswordMappingService.getDecryptedPassword(cpr);
			}

			InstitutionPersonDTO personRecord = new InstitutionPersonDTO(
					person.getId(),
					person.getLocalPersonId(),
					person.getSource(),
					person.getLastModified(),
					calculatedName.getFirstname(),
					calculatedName.getSurname(),
					person.getPerson().getGender(),
					cpr,
					person.getUsername(),
					stilUsername,
					uniId,
					role,
					institutionPersonService.findGlobalRole(person, matchingPeople),
					groupIds,
					studentMainGroup,
					studentMainGroupsAsObjects,
					studentMainGroupsWorkspace,
					stilMainGroupCurrentInstitution,
					stilGroupsCurrentInstitution,
					institutions,
					person.getInstitution().getInstitutionNumber(),
					studentRole,
					institutionPersonService.findGlobalStudentRole(matchingPeople),
					employeeRole,
					institutionPersonService.findGlobalEmployeeRoles(matchingPeople),
					externalRole,
					institutionPersonService.findGlobalExternalRole(matchingPeople),
					institutionService.getInstitutionDTO(person.getInstitution()),
					studentMainGroupStartYearForInstitution,
					studentMainGroupLevelForInstitution,
					person.isDeleted(),
					institutionPersonService.findTotalRoles(matchingPeople),
					contactCardRecords,
					setPasswordOnCreate,
					password,
					person.getReservedUsername(),
					primaryPerson == null ? null : institutionService.getInstitutionDTO(primaryPerson.getInstitution())
			);

			result.add(personRecord);
		}

		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/api/groups")
	public ResponseEntity<?> getGroups(@RequestParam(name = "institutionNumber")  String institutionNumber, @RequestParam(name = "alltypes", required = false) boolean allTypes) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return ResponseEntity.badRequest().body("Institution with number: " + institutionNumber + " not found.");
		}

		int currentSchoolYear = settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionNumber);

		List<DBGroup> dbGroups;
		if (allTypes) {
			dbGroups = groupService.findByInstitution(institution).stream()
					.filter(g -> !g.isDeleted())
					.collect(Collectors.toList());
		} else {
			dbGroups = groupService.findByInstitution(institution).stream()
					.filter(g -> !g.isDeleted() && g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
					.collect(Collectors.toList());
		}

		if (configuration.isFilterOutGroupsWithFutureFromDate()) {
			LocalDate futureDate = LocalDate.now().plusDays(configuration.getCreateGroupsXDaysBeforeFromDate() + 1);
			dbGroups = dbGroups.stream()
					.filter(g -> g.getFromDate() == null || g.getFromDate().isBefore(futureDate))
					.collect(Collectors.toList());
		}

		// only main groups
		List<GroupDTO> groups = dbGroups.stream()
				.map(g -> new GroupDTO(
						g.getId(),
						g.getLastModified(),
						g.getFromDate(),
						g.getToDate(),
						g.getGroupId(),
						g.getGroupLevel(),
						g.getGroupName(),
						g.getGroupType(),
						g.getLine(),
						g.getInstitution().getInstitutionNumber(),
						g.getInstitution().getInstitutionName(),
						g.getInstitution().getAbbreviation(),
						settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + g.getInstitution().getInstitutionNumber()),
						g.getInstitution().getGoogleWorkspaceId(),
						g.getInstitution().getStudentInstitutionGoogleWorkspaceId(),
						g.getInstitution().getEmployeeInstitutionGoogleWorkspaceId(),
						g.getGoogleWorkspaceId(),
						g.getDriveGoogleWorkspaceId(),
						g.getAzureSecurityGroupId(),
						groupService.getStartYear(g.getGroupLevel(), currentSchoolYear, g.getId()),
						g.getGroupGoogleWorkspaceEmail(),
						g.getGroupOnlyStudentsGoogleWorkspaceEmail(),
						g.getAzureTeamId(),
						g.getCurrentYearGWGroupIdentifier(),
						g.getCurrentYearGWFolderIdentifier()
						))
				.collect(Collectors.toList());

		return ResponseEntity.ok(groups);
	}

	record UsernameRecord(String username, long personDatabaseId) {}

	@PostMapping("/api/person/username")
	public ResponseEntity<?> getInstitutionPersons(@RequestBody UsernameRecord usernameRecord) {
		DBInstitutionPerson institutionPerson = institutionPersonService.findByIdAndNotDeleted(usernameRecord.personDatabaseId());

		if (institutionPerson == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		else if (!StringUtils.hasLength(usernameRecord.username())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		institutionPerson.setUsername(usernameRecord.username());
		institutionPersonService.save(institutionPerson);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	record SetFieldRecord(long id, EntityType entityType, SetFieldType fieldType, String value) {}
	
	@PostMapping("/api/setfield")
	public ResponseEntity<?> setFields(@RequestBody SetFieldRecord setFieldRecord) {
		if (EntityType.INSTITUTION.equals(setFieldRecord.entityType)) {
			DBInstitution institution = institutionService.findById(setFieldRecord.id());
			if (institution == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			if (SetFieldType.INSTITUTION_ALL_DRIVE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setAllDriveGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_EMPLOYEE_DRIVE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setEmployeeDriveGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_STUDENT_DRIVE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setStudentDriveGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_ALL_AZURE_SECURITY_GROUP_ID.equals(setFieldRecord.fieldType)) {
				institution.setAllAzureSecurityGroupId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_STUDENT_AZURE_SECURITY_GROUP_ID.equals(setFieldRecord.fieldType)) {
				institution.setStudentAzureSecurityGroupId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_EMPLOYEE_AZURE_SECURITY_GROUP_ID.equals(setFieldRecord.fieldType)) {
				institution.setEmployeeAzureSecurityGroupId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_EMPLOYEE_GROUP_WORKSPACE_EMAIL.equals(setFieldRecord.fieldType)) {
				institution.setEmployeeGroupGoogleWorkspaceEmail(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_STUDENT_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setStudentInstitutionGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_EMPLOYEE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setEmployeeInstitutionGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_EMPLOYEE_AZURE_TEAM_ID.equals(setFieldRecord.fieldType)) {
				institution.setEmployeeAzureTeamId(setFieldRecord.value());
			}
			else if (SetFieldType.INSTITUTION_DRIVE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				institution.setInstitutionDriveGoogleWorkspaceId(setFieldRecord.value());
			}
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			institutionService.save(institution);
			return new ResponseEntity<>(HttpStatus.OK);

		}
		else if (EntityType.GROUP.equals(setFieldRecord.entityType)) {
			DBGroup group = groupService.findById(setFieldRecord.id());
			if (group == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			if (SetFieldType.GROUP_DRIVE_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				group.setDriveGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				group.setGoogleWorkspaceId(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_AZURE_SECURITY_GROUP_ID.equals(setFieldRecord.fieldType)) {
				group.setAzureSecurityGroupId(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_GROUP_WORKSPACE_EMAIL.equals(setFieldRecord.fieldType)) {
				group.setGroupGoogleWorkspaceEmail(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_ONLY_STUDENTS_GROUP_WORKSPACE_EMAIL.equals(setFieldRecord.fieldType)) {
				group.setGroupOnlyStudentsGoogleWorkspaceEmail(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_AZURE_TEAM_ID.equals(setFieldRecord.fieldType)) {
				group.setAzureTeamId(setFieldRecord.value());
			}
			else if (SetFieldType.GROUP_YEAR_GROUP_WORKSPACE_EMAIL.equals(setFieldRecord.fieldType)) {
				groupOrFolderService.create(FolderOrGroup.GROUP, group, setFieldRecord.value);
				group.setCurrentYearGWGroupIdentifier(setFieldRecord.value);
			}
			else if (SetFieldType.GROUP_YEAR_FOLDER_WORKSPACE_ID.equals(setFieldRecord.fieldType)) {
				groupOrFolderService.create(FolderOrGroup.FOLDER, group, setFieldRecord.value);
				group.setCurrentYearGWFolderIdentifier(setFieldRecord.value);
			}
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			groupService.save(group);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	record SetGroupIdentifierRecord(long institutionId, String groupKey, String groupEmail) {}

	@PostMapping("/api/setgroupemail")
	public ResponseEntity<?> setGroupEmails(@RequestBody SetGroupIdentifierRecord setGroupIdentifierRecord, @RequestParam(defaultValue = "GW") IntegrationType integration) {
		DBInstitution institution = institutionService.findById(setGroupIdentifierRecord.institutionId());
		if (institution == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}


		if (institution.getIntegrationGroupIdentifierMappings() == null) {
			institution.setIntegrationGroupIdentifierMappings(new ArrayList<>());
		}

		boolean found = false;
		for (InstitutionGroupIdentifierMapping mapping : institution.getIntegrationGroupIdentifierMappings()) {
			if (mapping.getGroupKey().equals(setGroupIdentifierRecord.groupKey()) && mapping.getIntegrationType().equals(integration)) {
				mapping.setGroupIdentifier(setGroupIdentifierRecord.groupEmail());
				found = true;
				break;
			}
		}

		if (!found) {
			InstitutionGroupIdentifierMapping mapping = new InstitutionGroupIdentifierMapping();
			mapping.setInstitution(institution);
			mapping.setGroupKey(setGroupIdentifierRecord.groupKey());
			mapping.setGroupIdentifier(setGroupIdentifierRecord.groupEmail());
			mapping.setIntegrationType(integration);
			institution.getIntegrationGroupIdentifierMappings().add(mapping);
		}

		institutionService.save(institution);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/locked/usernames")
	public ResponseEntity<?> getLockedUsernames() {
		Set<String> lockedUsernames = new HashSet<>();
		List<DBInstitution> lockedInstitutions = getLockedIntitutions();

		for (DBInstitution institution : lockedInstitutions) {
			List<DBInstitutionPerson> persons = institutionPersonService.findByInstitution(institution);
			lockedUsernames.addAll(persons.stream().filter(p -> p.getUsername() != null).map(p -> p.getUsername()).collect(Collectors.toSet()));
		}

		return ResponseEntity.ok(lockedUsernames);
	}

	@GetMapping("/api/locked/groups/workspace")
	public ResponseEntity<?> getLockedGroupEmails() {
		Set<String> groupEmails = new HashSet<>();
		List<DBInstitution> lockedInstitutions = getLockedIntitutions();

		for (DBInstitution institution : lockedInstitutions) {
			if (institution.getEmployeeGroupGoogleWorkspaceEmail() != null) {
				groupEmails.add(institution.getEmployeeGroupGoogleWorkspaceEmail());
			}
			groupEmails.addAll(institution.getIntegrationGroupIdentifierMappings().stream().map(g -> g.getGroupIdentifier()).collect(Collectors.toList()));
			groupEmails.addAll(institution.getGroups().stream().filter(g -> g.getGroupGoogleWorkspaceEmail() != null).map(DBGroup::getGroupGoogleWorkspaceEmail).collect(Collectors.toSet()));
		}

		groupEmails = groupEmails.stream().map(String::toLowerCase).collect(Collectors.toSet());

		return ResponseEntity.ok(groupEmails);
	}

	@GetMapping("/api/locked/groups/azure")
	public ResponseEntity<?> getLockedAzureGroupIds() {
		Set<String> groupIds = new HashSet<>();
		List<DBInstitution> lockedInstitutions = getLockedIntitutions();

		for (DBInstitution institution : lockedInstitutions) {
			if (institution.getAllAzureSecurityGroupId() != null) {
				groupIds.add(institution.getAllAzureSecurityGroupId());
			}
			if (institution.getEmployeeAzureSecurityGroupId() != null) {
				groupIds.add(institution.getEmployeeAzureSecurityGroupId());
			}
			if (institution.getStudentAzureSecurityGroupId() != null) {
				groupIds.add(institution.getStudentAzureSecurityGroupId());
			}

			groupIds.addAll(institution.getGroups().stream().filter(g -> g.getAzureSecurityGroupId() != null).map(DBGroup::getAzureSecurityGroupId).collect(Collectors.toSet()));
		}

		return ResponseEntity.ok(groupIds);
	}

	@GetMapping("/api/locked/teams/azure")
	public ResponseEntity<?> getLockedAzureTeamIds() {
		Set<String> teamIds = new HashSet<>();
		List<DBInstitution> lockedInstitutions = getLockedIntitutions();

		for (DBInstitution institution : lockedInstitutions) {
			if (institution.getEmployeeAzureTeamId() != null) {
				teamIds.add(institution.getEmployeeAzureTeamId());
			}

			teamIds.addAll(institution.getGroups().stream().filter(g -> g.getAzureTeamId() != null).map(DBGroup::getAzureTeamId).collect(Collectors.toSet()));
		}

		return ResponseEntity.ok(teamIds);
	}

	@GetMapping("/api/locked/drives/workspace")
	public ResponseEntity<?> getLockedDrives() {
		Set<String> driveIds = new HashSet<>();
		List<DBInstitution> lockedInstitutions = getLockedIntitutions();

		for (DBInstitution institution : lockedInstitutions) {
			if (institution.getAllDriveGoogleWorkspaceId() != null) {
				driveIds.add(institution.getAllDriveGoogleWorkspaceId());
			}
			if (institution.getEmployeeDriveGoogleWorkspaceId() != null) {
				driveIds.add(institution.getEmployeeDriveGoogleWorkspaceId());
			}
			if (institution.getStudentDriveGoogleWorkspaceId() != null) {
				driveIds.add(institution.getStudentDriveGoogleWorkspaceId());
			}
			if (institution.getInstitutionDriveGoogleWorkspaceId() != null) {
				driveIds.add(institution.getInstitutionDriveGoogleWorkspaceId());
			}

			driveIds.addAll(institution.getGroups().stream().filter(g -> g.getDriveGoogleWorkspaceId() != null).map(DBGroup::getDriveGoogleWorkspaceId).collect(Collectors.toSet()));
		}

		return ResponseEntity.ok(driveIds);
	}

	@GetMapping("/api/usernames/all")
	public ResponseEntity<?> getAllUsernames() {
		List<DBInstitutionPerson> institutionPeople = institutionPersonService.findAllIncludingDeleted();
		List<String> allUsernames = institutionPeople.stream().filter(i -> i.getUsername() != null).map(i -> i.getUsername()).collect(Collectors.toList());

		return ResponseEntity.ok(allUsernames);
	}

	record IntegrationAction(String username, IntegrationType integrationType, Action action) {}

	@PostMapping("/api/person/action")
	public ResponseEntity<?> setIntegrationActionDate(@RequestBody IntegrationAction integrationAction) {
		List<DBInstitutionPerson> institutionPersonList = institutionPersonService.findByUsername(integrationAction.username);

		if (institutionPersonList.isEmpty()) {
			log.warn("/api/person/action: failed to find person with username " + integrationAction.username() + " and action " + integrationAction.action + " and integration " + integrationAction.integrationType);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		for (DBInstitutionPerson institutionPerson : institutionPersonList) {
			switch (integrationAction.integrationType) {
				case AD:
					if (integrationAction.action.equals(Action.DEACTIVATE)) {
						institutionPerson.setAdDeactivated(LocalDateTime.now());
					} else if (integrationAction.action.equals(Action.CREATE) || integrationAction.action.equals(Action.REACTIVATE)) {
						if (institutionPerson.getAdCreated() == null ) {
							institutionPerson.setAdCreated(LocalDateTime.now());
						}
						institutionPerson.setAdDeactivated(null);
					}
					break;
				case GW:
					if (integrationAction.action.equals(Action.DEACTIVATE)) {
						institutionPerson.setGwDeactivated(LocalDateTime.now());
					} else if (integrationAction.action.equals(Action.CREATE) || integrationAction.action.equals(Action.REACTIVATE)) {
						if (institutionPerson.getGwCreated() == null ) {
							institutionPerson.setGwCreated(LocalDateTime.now());
						}
						institutionPerson.setGwDeactivated(null);
					}
					break;
				case AZURE:
					if (integrationAction.action.equals(Action.DEACTIVATE)) {
						institutionPerson.setAzureDeactivated(LocalDateTime.now());
					} else if (integrationAction.action.equals(Action.CREATE) || integrationAction.action.equals(Action.REACTIVATE)) {
						if (institutionPerson.getAzureCreated() == null ) {
							institutionPerson.setAzureCreated(LocalDateTime.now());
						}
						institutionPerson.setAzureDeactivated(null);
					}
					break;
			}

			institutionPersonService.save(institutionPerson);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/keepalive")
	public ResponseEntity<Set<String>> getKeepAliveUsernames() {
		Set<String> keepAliveUsernames = ghostService.getAllActive().stream().map(Ghost::getUsername).collect(Collectors.toSet());

		return ResponseEntity.ok(keepAliveUsernames);
	}

	@GetMapping("/api/yearchange")
	public ResponseEntity<?> isYearChange() {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		boolean perfomSetting = settingService.getBooleanValueByKey(CustomerSetting.PERFORM_YEAR_CHANGE_.toString() + client.getId());
		String yearSetting = settingService.getStringValueByKey(CustomerSetting.PERFORM_YEAR_CHANGE_YEAR.toString());
		int year;
		try {
			year = Integer.parseInt(yearSetting);
		} catch (Exception e) {
			year = 0;
		}
		if (perfomSetting && LocalDate.now().getYear() == year) {
			return ResponseEntity.ok(true);
		}
		return ResponseEntity.ok(false);
	}

	@PostMapping("/api/yearchange")
	public ResponseEntity<?> setYearChangePerformed(@RequestParam(name = "deletedfoldersandgroups", required = false) boolean deletedFoldersAndGroups) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		settingService.setValueForKey(CustomerSetting.PERFORM_YEAR_CHANGE_.toString() + client.getId(), false);
		if (deletedFoldersAndGroups) {
			groupOrFolderService.deleteGWClassFoldersAndGroupsPendingDeletion();
		}
		return ResponseEntity.ok().build();
	}
	record FolderOrGroupDTO(String googleWorkspaceId, FolderOrGroup type) {}
	@GetMapping("/api/yearchange/foldersandgroups/delete")
	public ResponseEntity<List<FolderOrGroupDTO>> getGroupsAndFoldersForDeletion() {
		return ResponseEntity.ok(groupOrFolderService.getGWClassFoldersAndGroupsForDeletion().stream()
				.map(g -> new FolderOrGroupDTO(g.getGoogleWorkspaceId(), g.getType()))
				.collect(Collectors.toList()));
	}
	@GetMapping("/api/yearchange/foldersandgroups/folders")
	public ResponseEntity<List<FolderOrGroupDTO>> getAllFolders() {
		return ResponseEntity.ok(groupOrFolderService.getAll().stream()
				.filter(g -> g.getType().equals(FolderOrGroup.FOLDER))
				.map(g -> new FolderOrGroupDTO(g.getGoogleWorkspaceId(), g.getType()))
				.collect(Collectors.toList()));
	}

	@GetMapping("/api/yearchange/foldersandgroups/groups")
	public ResponseEntity<List<FolderOrGroupDTO>> getAllGroup() {
		return ResponseEntity.ok(groupOrFolderService.getAll().stream()
				.filter(g -> g.getType().equals(FolderOrGroup.GROUP))
				.map(g -> new FolderOrGroupDTO(g.getGoogleWorkspaceId(), g.getType()))
				.collect(Collectors.toList()));
	}

	private List<DBInstitution> getLockedIntitutions() {
		List<DBInstitution> allInstitutions = institutionService.findAll();
		List<DBInstitution> lockedInstitutions = allInstitutions.stream().filter(i -> settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + i.getInstitutionNumber())).collect(Collectors.toList());
		return lockedInstitutions;
	}
}
