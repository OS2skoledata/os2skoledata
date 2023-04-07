package dk.digitalidentity.os2skoledata.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionGoogleWorkspaceGroupMapping;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.os2skoledata.api.ReadApiController.GroupRecord;
import dk.digitalidentity.os2skoledata.api.ReadApiController.InstitutionPersonRecord;
import dk.digitalidentity.os2skoledata.api.ReadApiController.InstitutionRecord;
import dk.digitalidentity.os2skoledata.api.enums.PersonRole;
import dk.digitalidentity.os2skoledata.dao.ModificationHistoryDao;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.ModificationHistoryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SyncApiController {

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private ClientService clientService;

	// TODO: remove and replace with service
	@Autowired
	private ModificationHistoryDao modificationHistoryDao;

	@Autowired
	private ModificationHistoryService modificationHistoryService;

	@Autowired
	private SettingService settingService;
	
	@GetMapping("/api/head")
	public ResponseEntity<?> getHead() {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		return ResponseEntity.ok(modificationHistoryService.getHead());
	}
	
	record HeadRecord(long head) {}
	
	@PostMapping("/api/head")
	public ResponseEntity<?> setHead(@RequestBody HeadRecord body) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}
		
		if (body.head != 0 && body.head <= client.getModificationHistoryOffset()) {
			return new ResponseEntity<>("New head must be higher than current: " + client.getModificationHistoryOffset(), HttpStatus.BAD_REQUEST);
		}
		
		client.setModificationHistoryOffset(body.head);
		client.setLastActive(LocalDateTime.now());
		clientService.save(client);
		
		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/changes")
	public ResponseEntity<?> getChanges() {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		if (client.getModificationHistoryOffset() == 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Head has never been set on this client!");			
		}

		var changes = modificationHistoryDao.findByIdGreaterThan(client.getModificationHistoryOffset());
		if (changes.size() >= 10000) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Too many changes, perform a full sync instead");
		}

		return ResponseEntity.ok(changes);
	}

	record ChangesRequestRecord(List<Long> ids) {}
	
	@PostMapping("/api/changes/institutions")
	public ResponseEntity<?> getInstitution(@RequestBody ChangesRequestRecord body) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		List<InstitutionRecord> institutions = institutionService.findByIdIn(body.ids).stream()
				.map(i -> new InstitutionRecord(i.getId(), i.getInstitutionName(), i.getInstitutionNumber(), i.getGoogleWorkspaceId(), i.getAllDriveGoogleWorkspaceId(), i.getStudentDriveGoogleWorkspaceId(), i.getEmployeeDriveGoogleWorkspaceId(), i.getAllAzureSecurityGroupId(), i.getStudentAzureSecurityGroupId(), i.getEmployeeAzureSecurityGroupId(), i.getEmployeeGroupGoogleWorkspaceEmail(), i.getType(), institutionService.generateEmailMap(i)))
				.collect(Collectors.toList());

		return ResponseEntity.ok(institutions);
	}

	@PostMapping("/api/changes/persons")
	public ResponseEntity<?> getInstitutionPersons(@RequestBody ChangesRequestRecord body) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		List<DBGroup> groups = groupService.findAllNotDeleted();
		List<DBInstitutionPerson> persons = institutionPersonService.findByIdIn(body.ids);
		List<InstitutionPersonRecord> result = new ArrayList<>();
		for (DBInstitutionPerson person : persons) {
			PersonRole role = null;
			List<Long> groupIds = new ArrayList<>();
			List<Long> studentMainGroup = new ArrayList<>();
			List<String> studentMainGroupsWorkspace = new ArrayList<>();
			DBStudentRole studentRole = null;
			List<DBEmployeeRole> employeeRole = null;
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
					studentMainGroupStartYearForInstitution = groupService.getStartYear(mainGroupForInstitution.getGroupLevel(), settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + person.getInstitution().getInstitutionNumber()), mainGroupForInstitution.getId());
				}
			}
			else if (person.getEmployee() != null) {
				role = PersonRole.EMPLOYEE;
				for (DBEmployeeGroupId groupId : person.getEmployee().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				employeeRole = person.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole()).toList();
			}
			else {
				log.warn("Person with id " + person.getId() + " is not student, employee or extern. Not returning it through API.");
				continue;
			}

			// TODO: skal ikke genbruge records på tværs af metoder, det gør mig ked af det...
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

	@PostMapping("/api/changes/groups")
	public ResponseEntity<?> getGroups(@RequestBody ChangesRequestRecord body) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		// only main groups
		List<GroupRecord> groups = groupService.findByIdIn(body.ids).stream()
				.filter(g -> g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
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
						groupService.getStartYear(g.getGroupLevel(), settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + g.getInstitution().getInstitutionNumber()), g.getId()),
						g.getGroupGoogleWorkspaceEmail()
						))
				.collect(Collectors.toList());

		return ResponseEntity.ok(groups);
	}
}
