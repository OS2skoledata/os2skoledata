package dk.digitalidentity.os2skoledata.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionGoogleWorkspaceGroupMapping;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.SettingService;
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

import dk.digitalidentity.os2skoledata.api.enums.PersonRole;
import dk.digitalidentity.os2skoledata.api.enums.SetFieldType;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBGender;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
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

	record InstitutionRecord(long databaseId, String name, String number, String googleWorkspaceId, String allDriveGoogleWorkspaceId, String studentDriveGoogleWorkspaceId, String employeeDriveGoogleWorkspaceId, String allAzureSecurityGroupId, String studentAzureSecurityGroupId, String employeeAzureSecurityGroupId, String employeeGroupGoogleWorkspaceEmail, InstitutionType type, Map<String, String> googleWorkspaceEmailMappings) {}

	@GetMapping("/api/institutions")
	public ResponseEntity<?> getInstitutions() {
		List<InstitutionRecord> institutions = institutionService.findAll().stream()
				.map(i -> new InstitutionRecord(
						i.getId(),
						i.getInstitutionName(),
						i.getInstitutionNumber(),
						i.getGoogleWorkspaceId(),
						i.getAllDriveGoogleWorkspaceId(),
						i.getStudentDriveGoogleWorkspaceId(),
						i.getEmployeeDriveGoogleWorkspaceId(),
						i.getAllAzureSecurityGroupId(),
						i.getStudentAzureSecurityGroupId(),
						i.getEmployeeAzureSecurityGroupId(),
						i.getEmployeeGroupGoogleWorkspaceEmail(),
						i.getType(),
						institutionService.generateEmailMap(i)
				)).toList();
		return ResponseEntity.ok(institutions);
	}

	record InstitutionPersonRecord(long databaseId, String localPersonId, String source, @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime lastModified,
								   String firstName, String familyName, DBGender gender, String cpr, String username, PersonRole role, List<Long> groupIds,
								   List<Long> studentMainGroups, List<String> studentMainGroupsGoogleWorkspaceIds, List<InstitutionRecord> institutions, String currentInstitutionNumber, DBStudentRole studentRole,
								   List<DBEmployeeRole> employeeRoles, DBExternalRoleType externalRole, InstitutionRecord currentInstitution, int studentMainGroupStartYearForInstitution) {}

	@GetMapping("/api/persons")
	public ResponseEntity<?> getInstitutionPersons(@RequestParam(name = "institutionNumber") String institutionNumber) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return ResponseEntity.badRequest().body("Institution with number: " + institutionNumber + " not found.");
		}

		int currentYear = settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionNumber);
		List<DBInstitutionPerson> persons = institutionPersonService.findByInstitution(institution);
		List<DBGroup> groups = groupService.findAllNotDeleted();
		List<InstitutionPersonRecord> result = new ArrayList<>();
		for (DBInstitutionPerson person : persons) {
			if (person.isDeleted()) {
				continue;
			}

			List<Long> studentMainGroup = new ArrayList<>();
			List<String> studentMainGroupsWorkspace = new ArrayList<>();
			PersonRole role = null;
			List<Long> groupIds = new ArrayList<>();
			DBStudentRole studentRole = null;
			List<DBEmployeeRole> employeeRole = new ArrayList<>();
			DBExternalRoleType externalRole = null;
			int studentMainGroupStartYearForInstitution = 0;
			List<DBInstitutionPerson> matchingPeople = institutionPersonService.findByPersonCivilRegistrationNumber(person.getPerson().getCivilRegistrationNumber());
			List<InstitutionRecord> institutions = new ArrayList<>(matchingPeople.stream().map(p -> new InstitutionRecord(p.getInstitution().getId(), p.getInstitution().getInstitutionName(), p.getInstitution().getInstitutionNumber(), p.getInstitution().getGoogleWorkspaceId(), p.getInstitution().getAllDriveGoogleWorkspaceId(), p.getInstitution().getStudentDriveGoogleWorkspaceId(), p.getInstitution().getEmployeeDriveGoogleWorkspaceId(), p.getInstitution().getAllAzureSecurityGroupId(), p.getInstitution().getStudentAzureSecurityGroupId(), p.getInstitution().getEmployeeAzureSecurityGroupId(), p.getInstitution().getEmployeeGroupGoogleWorkspaceEmail(), p.getInstitution().getType(), institutionService.generateEmailMap(p.getInstitution()))).toList());
			if (person.getExtern() != null) {
				role = PersonRole.EXTERNAL;
				for (DBExternGroupId groupId : person.getExtern().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				externalRole = person.getExtern().getRole();
			}
			else if (person.getStudent() != null) {
				role = PersonRole.STUDENT;
				for (DBStudentGroupId groupId : person.getStudent().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				for (DBInstitutionPerson matchingPerson : matchingPeople) {
					if (matchingPerson.getStudent() != null) {
						DBGroup group = groups.stream().filter(g ->  g.getInstitution().getId() == matchingPerson.getInstitution().getId() && g.getGroupId().equals(matchingPerson.getStudent().getMainGroupId())).findAny().orElse(null);
						if (group != null) {
							studentMainGroup.add(group.getId());
							studentMainGroupsWorkspace.add(group.getGoogleWorkspaceId());
						}
					}
				}
				studentRole = person.getStudent().getRole();
				DBGroup mainGroupForInstitution = groups.stream().filter(g ->  g.getInstitution().getId() == person.getInstitution().getId() && g.getGroupId().equals(person.getStudent().getMainGroupId())).findAny().orElse(null);
				if (mainGroupForInstitution != null) {
					studentMainGroupStartYearForInstitution = groupService.getStartYear(mainGroupForInstitution.getGroupLevel(), currentYear, mainGroupForInstitution.getId());
				}
			}
			else if (person.getEmployee() != null) {
				role = PersonRole.EMPLOYEE;
				for (DBEmployeeGroupId groupId : person.getEmployee().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				employeeRole = person.getEmployee().getRoles().stream().map(DBRole::getEmployeeRole).toList();
			}
			else {
				log.warn("Person with id " + person.getId() + " is not student, employee or extern. Not returning it through API.");
				continue;
			}

			InstitutionPersonRecord personRecord = new InstitutionPersonRecord(
				person.getId(),
				person.getLocalPersonId(),
				person.getSource(),
				person.getLastModified(),
				person.getPerson().getFirstName(),
				person.getPerson().getFamilyName(),
				person.getPerson().getGender(),
				person.getPerson().getCivilRegistrationNumber(),
				person.getUsername(),
				role,
				groupIds,
				studentMainGroup,
				studentMainGroupsWorkspace,
				institutions,
				person.getInstitution().getInstitutionNumber(),
				studentRole,
				employeeRole,
				externalRole,
				new InstitutionRecord(person.getInstitution().getId(), person.getInstitution().getInstitutionName(), person.getInstitution().getInstitutionNumber(), person.getInstitution().getGoogleWorkspaceId(), person.getInstitution().getAllDriveGoogleWorkspaceId(), person.getInstitution().getStudentDriveGoogleWorkspaceId(), person.getInstitution().getEmployeeDriveGoogleWorkspaceId(), person.getInstitution().getAllAzureSecurityGroupId(), person.getInstitution().getStudentAzureSecurityGroupId(), person.getInstitution().getEmployeeAzureSecurityGroupId(), person.getInstitution().getEmployeeGroupGoogleWorkspaceEmail(), person.getInstitution().getType(), institutionService.generateEmailMap(person.getInstitution())),
				studentMainGroupStartYearForInstitution
			);

			result.add(personRecord);
		}

		return ResponseEntity.ok(result);
	}

	record GroupRecord(long databaseId, @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime lastModified, LocalDate fromDate, LocalDate toDate, String groupId,
			String groupLevel, String groupName, DBImportGroupType groupType, String line, String institutionNumber, String institutionName, String institutionGoogleWorkspaceId,
			String googleWorkspaceId, String driveGoogleWorkspaceId, String azureSecurityGroupId, int startYear, String groupGoogleWorkspaceEmail) {}
	
	@GetMapping("/api/groups")
	public ResponseEntity<?> getGroups(@RequestParam(name = "institutionNumber")  String institutionNumber) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return ResponseEntity.badRequest().body("Institution with number: " + institutionNumber + " not found.");
		}

		int currentYear = settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + institutionNumber);

		// only main groups
		List<GroupRecord> groups = groupService.findByInstitution(institution).stream()
				.filter(g -> !g.isDeleted() && g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
				.map(g -> new GroupRecord(
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
						g.getInstitution().getGoogleWorkspaceId(),
						g.getGoogleWorkspaceId(),
						g.getDriveGoogleWorkspaceId(),
						g.getAzureSecurityGroupId(),
						groupService.getStartYear(g.getGroupLevel(), currentYear, g.getId()),
						g.getGroupGoogleWorkspaceEmail()
						))
				.collect(Collectors.toList());

		return ResponseEntity.ok(groups);
	}

	record UsernameRecord(String username, String localPersonId) {}

	@PostMapping("/api/person/username")
	public ResponseEntity<?> getInstitutionPersons(@RequestBody UsernameRecord usernameRecord) {
		DBInstitutionPerson institutionPerson = institutionPersonService.findByLocalPersonId(usernameRecord.localPersonId());

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

	record ErrorRecord(String message) {}

	@PostMapping("/api/reporterror")
	public ResponseEntity<?> getInstitutionPersons(@RequestBody ErrorRecord errorRecord) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		log.error("Received error from Client with id " + client.getId() + " and name " + client.getName() + ". Error message: " + errorRecord.message());

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
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			groupService.save(group);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	record SetWorkspaceGroupEmailRecord(long institutionId, String groupKey, String groupEmail) {}

	@PostMapping("/api/setgroupemail")
	public ResponseEntity<?> setFields(@RequestBody SetWorkspaceGroupEmailRecord setWorkspaceGroupEmailRecord) {
		DBInstitution institution = institutionService.findById(setWorkspaceGroupEmailRecord.institutionId());
		if (institution == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (institution.getGoogleWorkspaceGroupEmailMappings() == null) {
			institution.setGoogleWorkspaceGroupEmailMappings(new ArrayList<>());
		}

		boolean found = false;
		for (InstitutionGoogleWorkspaceGroupMapping mapping : institution.getGoogleWorkspaceGroupEmailMappings()) {
			if (mapping.getGroupKey().equals(setWorkspaceGroupEmailRecord.groupKey())) {
				mapping.setGroupEmail(setWorkspaceGroupEmailRecord.groupEmail());
				found = true;
				break;
			}
		}

		if (!found) {
			InstitutionGoogleWorkspaceGroupMapping mapping = new InstitutionGoogleWorkspaceGroupMapping();
			mapping.setInstitution(institution);
			mapping.setGroupKey(setWorkspaceGroupEmailRecord.groupKey());
			mapping.setGroupEmail(setWorkspaceGroupEmailRecord.groupEmail());
			institution.getGoogleWorkspaceGroupEmailMappings().add(mapping);
		}

		institutionService.save(institution);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
