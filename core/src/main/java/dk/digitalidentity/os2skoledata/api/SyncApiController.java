package dk.digitalidentity.os2skoledata.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.service.model.ContactCardDTO;
import dk.digitalidentity.os2skoledata.service.model.NameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.os2skoledata.api.ReadApiController.GroupRecord;
import dk.digitalidentity.os2skoledata.api.ReadApiController.InstitutionPersonRecord;
import dk.digitalidentity.os2skoledata.api.ReadApiController.InstitutionRecord;
import dk.digitalidentity.os2skoledata.api.ReadApiController.MiniGroupRecord;
import dk.digitalidentity.os2skoledata.api.enums.PersonRole;
import dk.digitalidentity.os2skoledata.dao.ModificationHistoryDao;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionModificationHistoryOffset;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.ModificationHistoryService;
import dk.digitalidentity.os2skoledata.service.SettingService;
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
	
	@PostMapping("/api/head/{institutionNumber}")
	public ResponseEntity<?> setHead(@PathVariable String institutionNumber, @RequestBody HeadRecord body) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			log.error("Could not find institution: " + institutionNumber);
			return new ResponseEntity<>("Unknown institution", HttpStatus.BAD_REQUEST);
		}

		InstitutionModificationHistoryOffset match = client.getInstitutionModificationHistoryOffsets().stream().filter(i -> i.getInstitution().getId() == institution.getId()).findAny().orElse(null);
		if (match == null) {
			match = new InstitutionModificationHistoryOffset();
			match.setClient(client);
			match.setInstitution(institution);
			match.setModificationHistoryOffset(body.head());
			client.getInstitutionModificationHistoryOffsets().add(match);
		}
		else {
			if (body.head != 0 && body.head < match.getModificationHistoryOffset()) {
				log.error("body.head = " + body.head + " but body.head >= modificationHistoryOffset which is " + match.getModificationHistoryOffset());

				return new ResponseEntity<>("New head for institution " + institution.getInstitutionNumber() + " must be higher than current: " + match.getModificationHistoryOffset(), HttpStatus.BAD_REQUEST);
			}
			
			match.setModificationHistoryOffset(body.head());
		}

		client.setLastActive(LocalDateTime.now());
		clientService.save(client);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/changes/{institutionNumber}")
	public ResponseEntity<?> getChanges(@PathVariable String institutionNumber) {
		Client client = SecurityUtil.getClient();
		if (client == null) {
			log.error("Could not extract client from request!");
			return new ResponseEntity<>("Unknown client", HttpStatus.FORBIDDEN);
		}

		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return new ResponseEntity<>("Unknown institution", HttpStatus.BAD_REQUEST);
		}

		InstitutionModificationHistoryOffset match = client.getInstitutionModificationHistoryOffsets().stream().filter(i -> i.getInstitution().getId() == institution.getId()).findAny().orElse(null);
		if (match == null || match.getModificationHistoryOffset() == 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Head has never been set on this client!");			
		}

		var changes = modificationHistoryDao.findByinstitutionIdAndIdGreaterThan(institution.getId(), match.getModificationHistoryOffset());
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
				.map(i -> new InstitutionRecord(i.getId(), i.getInstitutionName(), i.getInstitutionNumber(), settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + i.getInstitutionNumber()), i.getGoogleWorkspaceId(), i.getAllDriveGoogleWorkspaceId(), i.getStudentDriveGoogleWorkspaceId(), i.getEmployeeDriveGoogleWorkspaceId(), i.getAllAzureSecurityGroupId(), i.getStudentAzureSecurityGroupId(), i.getEmployeeAzureSecurityGroupId(), i.getEmployeeGroupGoogleWorkspaceEmail(), i.getStudentInstitutionGoogleWorkspaceId(), i.getEmployeeInstitutionGoogleWorkspaceId(), i.getType(), institutionService.generateEmailMap(i), institutionService.findCurrentSchoolYearForGWEmails(i), i.getEmployeeAzureTeamId(), i.getAzureEmployeeTeamAdmin() != null ? i.getAzureEmployeeTeamAdmin().getUsername() : null))
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
			List<ContactCardDTO> contactCardRecords = new ArrayList<>();
			PersonRole role = null;
			List<Long> groupIds = new ArrayList<>();
			List<Long> studentMainGroup = new ArrayList<>();
			List<MiniGroupRecord> studentMainGroupsAsObjects = new ArrayList<>();
			List<String> studentMainGroupsWorkspace = new ArrayList<>();
			List<DBGroup> studentMainGroupGroups = new ArrayList<>();
			String stilMainGroupCurrentInstitution = null;
			List<String> stilGroupsCurrentInstitution = new ArrayList<>();
			DBStudentRole studentRole = null;
			List<DBEmployeeRole> employeeRole = null;
			DBExternalRoleType externalRole = null;
			int studentMainGroupStartYearForInstitution = 0;
			String studentMainGroupLevelForInstitution = null;
			List<DBInstitutionPerson> matchingPeopleIncludingDeleted = institutionPersonService.findByPersonCivilRegistrationNumber(person.getPerson().getCivilRegistrationNumber());
			List<DBInstitutionPerson> matchingPeople = matchingPeopleIncludingDeleted.stream().filter(m -> !m.isDeleted()).collect(Collectors.toList());
			List<DBInstitutionPerson> matchingPeopleDeleted = matchingPeopleIncludingDeleted.stream().filter(m -> m.isDeleted()).collect(Collectors.toList());
			List<InstitutionRecord> institutions = new ArrayList<>(matchingPeople.stream().map(p -> new InstitutionRecord(p.getInstitution().getId(), p.getInstitution().getInstitutionName(), p.getInstitution().getInstitutionNumber(), settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + p.getInstitution().getInstitutionNumber()), p.getInstitution().getGoogleWorkspaceId(), p.getInstitution().getAllDriveGoogleWorkspaceId(), p.getInstitution().getStudentDriveGoogleWorkspaceId(), p.getInstitution().getEmployeeDriveGoogleWorkspaceId(), p.getInstitution().getAllAzureSecurityGroupId(), p.getInstitution().getStudentAzureSecurityGroupId(), p.getInstitution().getEmployeeAzureSecurityGroupId(), p.getInstitution().getEmployeeGroupGoogleWorkspaceEmail(), p.getInstitution().getStudentInstitutionGoogleWorkspaceId(), p.getInstitution().getEmployeeInstitutionGoogleWorkspaceId(), p.getInstitution().getType(), institutionService.generateEmailMap(p.getInstitution()), institutionService.findCurrentSchoolYearForGWEmails(person.getInstitution()), person.getInstitution().getEmployeeAzureTeamId(), person.getInstitution().getAzureEmployeeTeamAdmin() != null ? person.getInstitution().getAzureEmployeeTeamAdmin().getUsername() : null)).toList());
			String stilUsername = null;
			String uniId = null;
			if (person.getEmployee() != null) {
				stilGroupsCurrentInstitution.addAll(person.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).collect(Collectors.toList()));
				role = PersonRole.EMPLOYEE;
				for (DBEmployeeGroupId groupId : person.getEmployee().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				employeeRole = person.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole()).toList();
			}
			else if (person.getStudent() != null) {
				role = PersonRole.STUDENT;
				stilGroupsCurrentInstitution.addAll(person.getStudent().getGroupIds().stream().map(DBStudentGroupId::getGroupId).collect(Collectors.toList()));
				for (DBStudentGroupId groupId : person.getStudent().getGroupIds()) {
					groups.stream().filter(g -> g.getGroupId().equals(groupId.getGroupId()) && g.getInstitution().getId() == person.getInstitution().getId()).findAny().ifPresent(group -> groupIds.add(group.getId()));
				}
				for (DBInstitutionPerson matchingPerson : matchingPeople) {
					if (matchingPerson.getStudent() != null) {
						DBGroup group = groups.stream().filter(g ->  g.getInstitution().getId() == matchingPerson.getInstitution().getId() && g.getGroupId().equals(matchingPerson.getStudent().getMainGroupId())).findAny().orElse(null);
						if (group != null) {
							studentMainGroupGroups.add(group);
						}
					}
				}
				studentRole = person.getStudent().getRole();
				DBGroup mainGroupForInstitution = groups.stream().filter(g ->  g.getInstitution().getId() == person.getInstitution().getId() && g.getGroupId().equals(person.getStudent().getMainGroupId())).findAny().orElse(null);
				if (mainGroupForInstitution != null) {
					studentMainGroupStartYearForInstitution = groupService.getStartYear(mainGroupForInstitution.getGroupLevel(), settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + person.getInstitution().getInstitutionNumber()), mainGroupForInstitution.getId());
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

			int currentYear = settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + person.getInstitution());
			groupService.sortAndAddStudentMainGroups(studentMainGroupGroups, studentMainGroup, studentMainGroupsWorkspace, studentMainGroupsAsObjects, currentYear);

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
				} else {
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
			NameDTO calculatedName = institutionPersonService.calculateName(matchingPeopleIncludingDeleted);

			// TODO: skal ikke genbruge records på tværs af metoder, det gør mig ked af det...
			InstitutionPersonRecord personRecord = new InstitutionPersonRecord(
					person.getId(),
					person.getLocalPersonId(),
					person.getSource(),
					person.getLastModified(),
					calculatedName.getFirstname(),
					calculatedName.getSurname(),
					person.getPerson().getGender(),
					person.getPerson().getCivilRegistrationNumber(),
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
					new InstitutionRecord(person.getInstitution().getId(), person.getInstitution().getInstitutionName(), person.getInstitution().getInstitutionNumber(), settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + person.getInstitution().getInstitutionNumber()), person.getInstitution().getGoogleWorkspaceId(), person.getInstitution().getAllDriveGoogleWorkspaceId(), person.getInstitution().getStudentDriveGoogleWorkspaceId(), person.getInstitution().getEmployeeDriveGoogleWorkspaceId(), person.getInstitution().getAllAzureSecurityGroupId(), person.getInstitution().getStudentAzureSecurityGroupId(), person.getInstitution().getEmployeeAzureSecurityGroupId(), person.getInstitution().getEmployeeGroupGoogleWorkspaceEmail(), person.getInstitution().getStudentInstitutionGoogleWorkspaceId(), person.getInstitution().getEmployeeInstitutionGoogleWorkspaceId(), person.getInstitution().getType(), institutionService.generateEmailMap(person.getInstitution()), institutionService.findCurrentSchoolYearForGWEmails(person.getInstitution()), person.getInstitution().getEmployeeAzureTeamId(), person.getInstitution().getAzureEmployeeTeamAdmin() != null ? person.getInstitution().getAzureEmployeeTeamAdmin().getUsername() : null),
					studentMainGroupStartYearForInstitution,
					studentMainGroupLevelForInstitution,
					person.isDeleted(),
					institutionPersonService.findTotalRoles(matchingPeople),
					contactCardRecords
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
						settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + g.getInstitution().getInstitutionNumber()),
						g.getInstitution().getGoogleWorkspaceId(),
						g.getInstitution().getStudentInstitutionGoogleWorkspaceId(),
						g.getInstitution().getEmployeeInstitutionGoogleWorkspaceId(),
						g.getGoogleWorkspaceId(),
						g.getDriveGoogleWorkspaceId(),
						g.getAzureSecurityGroupId(),
						groupService.getStartYear(g.getGroupLevel(), settingService.getIntegerValueByKey(CustomerSetting.CURRENT_SCHOOL_YEAR_.toString() + g.getInstitution().getInstitutionNumber()), g.getId()),
						g.getGroupGoogleWorkspaceEmail(),
						g.getGroupOnlyStudentsGoogleWorkspaceEmail(),
						g.getAzureTeamId()
						))
				.collect(Collectors.toList());

		return ResponseEntity.ok(groups);
	}
}
