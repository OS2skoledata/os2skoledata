package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import dk.digitalidentity.os2skoledata.config.modules.SyncField;
import dk.digitalidentity.os2skoledata.config.modules.SyncFrom;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBUniLogin;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionChangeProposal;
import dk.digitalidentity.os2skoledata.dao.model.ProposedPersonChange;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBPasswordState;
import dk.digitalidentity.os2skoledata.dao.model.enums.PersonChangeType;
import dk.digitalidentity.os2skoledata.service.stil.StilService;
import https.unilogin_dk.data.Group;
import https.unilogin_dk.data.transitional.UniLoginFull;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionPersonFullMyndighed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SyncService {

	@Autowired
	private StilService stilService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private PasswordSettingService passwordSettingService;

	@Autowired
	private CprPasswordMappingService cprPasswordMappingService;

	@Autowired
	private YearChangeNotificationService yearChangeNotificationService;


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sync(InstitutionDTO institutionDTO) {
		log.info("Synchronizing institution: " + institutionDTO.getInstitutionNumber());
		
		StilService.InstitutionYearChangeDTO dto = stilService.getInstitution(institutionDTO.getInstitutionNumber());
		if (dto == null) {
			log.error("Got no institution / year change data - this institution might not be approved yet - skipping: " + institutionDTO.getInstitutionNumber());
			return;
		}

		boolean yearChange = dto.yearChange();
		InstitutionFullMyndighed stilInstitution = dto.institutionFullMyndighed();
		if (stilInstitution == null || ((stilInstitution.getInstitutionPerson() == null || stilInstitution.getInstitutionPerson().isEmpty()) && (stilInstitution.getGroup() == null || stilInstitution.getGroup().isEmpty()))) {
			DBInstitution dbInstitutionTemp = institutionService.findByInstitutionNumber(institutionDTO.getInstitutionNumber());
			if (dbInstitutionTemp == null || !dbInstitutionTemp.isIgnoreEmptyError()) {
				log.error("Got no institution data - consider if this institution should be deleted - skipping: " + institutionDTO.getInstitutionNumber());
			} else {
				log.warn("Got no institution data - consider if this institution should be deleted - skipping: " + institutionDTO.getInstitutionNumber());
			}
			return;
		}

		DBInstitution dbInstitution = institutionService.findByInstitutionNumber(stilInstitution.getInstitutionNumber());

		// bypass check once if flag is sat
		if (dbInstitution != null && dbInstitution.isBypassTooFewPeople()) {
			dbInstitution.setBypassTooFewPeople(false);
			dbInstitution.setTooFewPeopleErrorCount(0);
			dbInstitution.setChangeProposal(null);
			institutionService.save(dbInstitution);
		} else {
			// sanity check: too few people?
			if (dbInstitution != null && dbInstitution.getInstitutionPersons() != null && !dbInstitution.getInstitutionPersons().isEmpty()) {
				if (stilInstitution.getInstitutionPerson() == null || stilInstitution.getInstitutionPerson().isEmpty()) {
					log.error("Institution in db has people, but institution in STIL has no people - skipping: " + institutionDTO);
					return;
				}

				long dbCount = dbInstitution.getInstitutionPersons().stream().filter(p -> !p.isDeleted() && !p.isApiOnly()).count();
				int stilCount = stilInstitution.getInstitutionPerson().size();

				if (stilCount <= dbCount * configuration.getSyncSettings().getThresholdPercentage()) {
					InstitutionChangeProposal changeProposal = createChangeProposal(dbInstitution, stilInstitution, dbCount, stilCount);
					dbInstitution.setChangeProposal(changeProposal);
					dbInstitution.setTooFewPeopleErrorCount(dbInstitution.getTooFewPeopleErrorCount() + 1);
					institutionService.save(dbInstitution);

					if (dbInstitution.getTooFewPeopleErrorCount() >= 5) {
						log.error("Error for " + dbInstitution.getTooFewPeopleErrorCount() + " in a row: Institution in db has " + dbCount + " people, but institution in STIL has " + stilCount + " people. Might be an error - skipping sync of: " + institutionDTO.getInstitutionNumber());
					}

					String emails = settingService.getStringValueByKey(CustomerSetting.STIL_CHANGE_EMAIL);
					if (StringUtils.hasLength(emails)) {
						String message = "Der er sket en stor ændring i antallet af brugere i STIL for institutionen " + dbInstitution.getInstitutionName() + ".\n"
								+ changeProposal.getTooFewPeopleErrorMessage()
								+ "\nDu skal tjekke om ændringen er meningen, eller om der er tale om en fejl i STIL."
								+ "\nDu kan logge ind i OS2skoledata brugergrænsefladen og gå til siden \"Institutioner\", hvor du har mulighed for at se de specifikke ændringer og godkende."
								+ "\nData for denne institution vil ikke blive opdateret inden ændringen er rettet i STIL eller godkendt.";

						emails = emails.replace(" ", "");
						String[] emailArray = emails.split(";");
						for (String email : emailArray) {
							emailService.sendMessage(email, "OS2skoledata: STIL ændringer til godkendelse", message);
						}
					}

					return;
				} else {
					if (dbInstitution.getTooFewPeopleErrorCount() != 0) {
						dbInstitution.setTooFewPeopleErrorCount(0);
						dbInstitution.setChangeProposal(null);
						institutionService.save(dbInstitution);
					}
				}
			}
		}


		if (dbInstitution == null) {
			dbInstitution = new DBInstitution();
			dbInstitution.copyFields(stilInstitution);
			dbInstitution.setType(institutionDTO.getType());
			dbInstitution.setAbbreviation(institutionDTO.getAbbreviation());

			log.info("Creating new institution: " + stilInstitution.getInstitutionName() + " / " + stilInstitution.getInstitutionNumber());
			institutionService.save(dbInstitution);
		}
		else {
			boolean changes = false;

			if (dbInstitution.isDeleted()) {
				log.info("Undeleting institution: " + dbInstitution.getId() + " " + dbInstitution.getInstitutionName());
				dbInstitution.setDeleted(false);
				changes = true;
			}

			if (yearChange) {
				dbInstitution.copyFieldsWithoutGroups(stilInstitution);
				handleGroupsOnYearChange(dbInstitution, stilInstitution);
				changes = true;
			} else {
				if (!dbInstitution.apiEquals(stilInstitution)) {
					dbInstitution.copyFields(stilInstitution);
					changes = true;
				}
			}

			if (!dbInstitution.getType().equals(institutionDTO.getType())) {
				dbInstitution.setType(institutionDTO.getType());
				changes = true;
			}

			if (!Objects.equals(dbInstitution.getAbbreviation(), institutionDTO.getAbbreviation())) {
				dbInstitution.setAbbreviation(institutionDTO.getAbbreviation());
				changes = true;
			}

			if (changes) {
				log.info("Updating core data on institution: " + stilInstitution.getInstitutionName() + " / " + stilInstitution.getInstitutionNumber());
				institutionService.save(dbInstitution);
			}
		}

		if (stilInstitution.getInstitutionPerson() != null && !stilInstitution.getInstitutionPerson().isEmpty()) {
			for (InstitutionPersonFullMyndighed stilInstitutionPerson : stilInstitution.getInstitutionPerson()) {
				DBInstitutionPerson dbInstitutionPerson = null;
				if (configuration.getSyncSettings().getSyncFrom().equals(SyncFrom.API_AND_STIL)) {
					dbInstitutionPerson = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(stilInstitutionPerson.getPerson().getCivilRegistrationNumber(), dbInstitution)
							.stream().filter(p -> !p.isDeleted()).findFirst().orElse(null);
				} else {
					dbInstitutionPerson = institutionPersonService.findByLocalPersonIdIncludingDeleted(stilInstitutionPerson.getLocalPersonId(), institutionDTO.getInstitutionNumber());
				}

				if (dbInstitutionPerson == null) {
					// if syncFrom is API_AND_STIL we only create students. Creation of employees and externals is managed by the API and not STIL
					// unless transitionMode
					boolean create = true;
					if (!configuration.getSyncSettings().isTransitionMode() && configuration.getSyncSettings().getSyncFrom().equals(SyncFrom.API_AND_STIL)) {
						if (stilInstitutionPerson.getEmployee() != null || stilInstitutionPerson.getExtern() != null) {
							create = false;
						}
					}

					if (create) {
						log.info("Creating institutionPerson: " + stilInstitutionPerson.getLocalPersonId());

						dbInstitutionPerson = new DBInstitutionPerson();
						dbInstitutionPerson.copyFields(stilInstitutionPerson, configuration.getSyncSettings().isOnlySaveNeededPropertiesFromSTIL());
						dbInstitutionPerson.setInstitution(dbInstitution);
						dbInstitutionPerson.setStilCreated(LocalDateTime.now());

						// check if we should generate password
						if (configuration.getStudentAdministration().isSetIndskolingPasswordOnCreate() && dbInstitutionPerson.getStudent() != null) {

							// make sure the password has not been sat for this cpr before
							if (!cprPasswordMappingService.exists(dbInstitutionPerson.getPerson().getCivilRegistrationNumber())) {
								Integer level = institutionPersonService.getLevel(dbInstitutionPerson);
								if (level != null && level <= 3) {
									cprPasswordMappingService.setPassword(dbInstitutionPerson.getPerson().getCivilRegistrationNumber(), passwordSettingService.generateEncryptedPasswordForIndskolingStudent());
								}
							}
						}

						institutionPersonService.save(dbInstitutionPerson);
					}
				}
				else {
					boolean changes = false;
					boolean normalUpdate = true;
					if (configuration.getSyncSettings().getSyncFrom().equals(SyncFrom.API_AND_STIL)) {
						if (stilInstitutionPerson.getEmployee() != null || stilInstitutionPerson.getExtern() != null) {
							normalUpdate = false;
						}
					}

					if (dbInstitutionPerson.isApiOnly()) {
						// do nothing everything is handled by API
					} else if (normalUpdate) {
						if (dbInstitutionPerson.isDeleted()) {
							log.info("Undeleting institutionPerson: " + dbInstitutionPerson.getId() + " " + dbInstitutionPerson.getLocalPersonId());
							dbInstitutionPerson.setDeleted(false);
							dbInstitutionPerson.setStilDeleted(null);
							dbInstitutionPerson.setStilCreated(LocalDateTime.now());
							changes = true;
						}

						if (!dbInstitutionPerson.getInstitution().getInstitutionNumber().equals(dbInstitution.getInstitutionNumber())) {
							log.info("Moving institution for person with id: " + dbInstitutionPerson.getId() + ". From " + dbInstitutionPerson.getInstitution().getInstitutionName() + "(" + dbInstitutionPerson.getInstitution().getInstitutionNumber() + ") to " + dbInstitution.getInstitutionName() + "(" + dbInstitution.getInstitutionNumber() + ")");
							dbInstitutionPerson.setInstitution(dbInstitution);
							changes = true;
						}

						if (!dbInstitutionPerson.apiEquals(stilInstitutionPerson, configuration.getSyncSettings().isOnlySaveNeededPropertiesFromSTIL())) {
							dbInstitutionPerson.copyFields(stilInstitutionPerson, configuration.getSyncSettings().isOnlySaveNeededPropertiesFromSTIL());
							changes = true;
						}

					} else {
						changes = apiAndStilSpecialUpdate(stilInstitutionPerson, dbInstitutionPerson, changes);
					}

					if (changes) {
						log.info("Updating institutionPerson: " + dbInstitutionPerson.getId() + " " + dbInstitutionPerson.getLocalPersonId());
						institutionPersonService.save(dbInstitutionPerson);
					}
				}
			}
		}

		// find institution persons to be deleted (from within the same institution)
		List<DBInstitutionPerson> toBeDeleted = institutionPersonService.findByInstitution(dbInstitution).stream()
				.filter(dbInstitutionPerson -> !dbInstitutionPerson.isDeleted() && !dbInstitutionPerson.isApiOnly())
				.filter(dbInstitutionPerson -> stilInstitution.getInstitutionPerson().stream()
						.noneMatch(stilInstitutionPerson -> Objects.equals(stilInstitutionPerson.getLocalPersonId(), dbInstitutionPerson.getLocalPersonId())))
				.collect(Collectors.toList());

		// if syncFrom is API_AND_STIL we only delete students. Deletion of employees and externals is managed by the API and not STIL
		// if transitionMode we allow deletion of everyone unless people with local source
		if (configuration.getSyncSettings().getSyncFrom().equals(SyncFrom.API_AND_STIL)) {
			if (configuration.getSyncSettings().isTransitionMode()) {
				toBeDeleted = toBeDeleted.stream().filter(p -> !Objects.equals(p.getSource(), configuration.getSyncSettings().getLocalSource())).collect(Collectors.toList());
			} else {
				toBeDeleted = toBeDeleted.stream().filter(p -> p.getStudent() != null).collect(Collectors.toList());
			}
		}

		toBeDeleted.forEach(person -> {
			log.info("Deleting institutionPerson: " + person.getId() + " " + person.getLocalPersonId());
			person.setDeleted(true);
			person.setStilDeleted(LocalDateTime.now());
		});

		if (toBeDeleted.size() > 0) {
			institutionPersonService.saveAll(toBeDeleted);
		}

		// Check if institution was unlocked (resolved) after being locked due to year change
		boolean isCurrentlyLocked = settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institutionDTO.getInstitutionNumber());
		if (!yearChange && !isCurrentlyLocked) {
			// Institution is not locked, so mark any pending year change notifications as resolved
			yearChangeNotificationService.markInstitutionAsResolved(institutionDTO.getInstitutionNumber());
		}
	}

	private boolean apiAndStilSpecialUpdate(InstitutionPersonFullMyndighed stilInstitutionPerson, DBInstitutionPerson dbInstitutionPerson, boolean changes) {

		if (!Objects.equals(dbInstitutionPerson.getLocalPersonId(), stilInstitutionPerson.getLocalPersonId())) {
			dbInstitutionPerson.setLocalPersonId(stilInstitutionPerson.getLocalPersonId());
			changes = true;
		}

		for (SyncField syncField : configuration.getSyncSettings().getFieldsMaintainedBySTIL()) {
			switch (syncField) {
				case UNI_LOGIN -> {
					changes = checkForUniLoginChanges(stilInstitutionPerson, dbInstitutionPerson, changes);
				}
				case GROUP_IDS -> {
					if (dbInstitutionPerson.getEmployee() != null && stilInstitutionPerson.getEmployee() != null) {
						changes = checkForEmployeeGroupsChanges(stilInstitutionPerson, dbInstitutionPerson, changes);
					} else if (dbInstitutionPerson.getExtern() != null && stilInstitutionPerson.getExtern() != null) {
						changes = checkForExternalGroupsChanges(stilInstitutionPerson, dbInstitutionPerson, changes);
					}
				}
				case NAME_PROTECTED -> {
					if (!Objects.equals(dbInstitutionPerson.getPerson().isProtected(), stilInstitutionPerson.getPerson().isProtected())) {
						dbInstitutionPerson.getPerson().setProtected(stilInstitutionPerson.getPerson().isProtected());
						changes = true;
					}
				}
				case ALIAS_FIRST_NAME -> {
					if (!Objects.equals(dbInstitutionPerson.getPerson().getAliasFirstName(), stilInstitutionPerson.getPerson().getAliasFirstName())) {
						dbInstitutionPerson.getPerson().setAliasFirstName(stilInstitutionPerson.getPerson().getAliasFirstName());
						changes = true;
					}
				}
				case ALIAS_FAMILY_NAME -> {
					if (!Objects.equals(dbInstitutionPerson.getPerson().getAliasFamilyName(), stilInstitutionPerson.getPerson().getAliasFamilyName())) {
						dbInstitutionPerson.getPerson().setAliasFamilyName(stilInstitutionPerson.getPerson().getAliasFamilyName());
						changes = true;
					}
				}
			}
		}
		return changes;
	}

	private static boolean checkForExternalGroupsChanges(InstitutionPersonFullMyndighed stilInstitutionPerson, DBInstitutionPerson dbInstitutionPerson, boolean changes) {
		if (dbInstitutionPerson.getExtern().getGroupIds() == null && stilInstitutionPerson.getExtern().getGroupId()!= null) {
			dbInstitutionPerson.getExtern().setGroupIds(new ArrayList<>());

			for (String groupId : stilInstitutionPerson.getExtern().getGroupId()) {
				DBExternGroupId dbExternGroupId = new DBExternGroupId();
				dbExternGroupId.setGroupId(groupId);
				dbExternGroupId.setExtern(dbInstitutionPerson.getExtern());
				dbInstitutionPerson.getExtern().getGroupIds().add(dbExternGroupId);
			}
			changes = true;
		}
		else if (dbInstitutionPerson.getExtern().getGroupIds() != null && stilInstitutionPerson.getExtern().getGroupId() != null) {
			List<String> existingGroupIds = dbInstitutionPerson.getExtern().getGroupIds().stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = stilInstitutionPerson.getExtern().getGroupId();

			for (String groupId : stilGroupIds) {
				if (!existingGroupIds.contains(groupId)) {
					DBExternGroupId newGroupIdMapping = new DBExternGroupId();
					newGroupIdMapping.setExtern(dbInstitutionPerson.getExtern());
					newGroupIdMapping.setGroupId(groupId);
					dbInstitutionPerson.getExtern().getGroupIds().add(newGroupIdMapping);
					changes = true;
				}
			}

			for (String groupId : existingGroupIds) {
				if (!stilGroupIds.contains(groupId)) {
					dbInstitutionPerson.getExtern().getGroupIds().removeIf(r -> r.getGroupId().equals(groupId));
					changes = true;
				}
			}
		}
		else if (dbInstitutionPerson.getExtern().getGroupIds() != null && stilInstitutionPerson.getExtern().getGroupId() == null) {
			dbInstitutionPerson.getExtern().getGroupIds().clear();
			changes = true;
		}
		return changes;
	}

	private static boolean checkForEmployeeGroupsChanges(InstitutionPersonFullMyndighed stilInstitutionPerson, DBInstitutionPerson dbInstitutionPerson, boolean changes) {
		if (dbInstitutionPerson.getEmployee().getGroupIds() == null && stilInstitutionPerson.getEmployee().getGroupId() != null) {
			dbInstitutionPerson.getEmployee().setGroupIds(new ArrayList<>());

			for (String groupId : stilInstitutionPerson.getEmployee().getGroupId()) {
				DBEmployeeGroupId dbEmployeeGroupId = new DBEmployeeGroupId();
				dbEmployeeGroupId.setGroupId(groupId);
				dbEmployeeGroupId.setEmployee(dbInstitutionPerson.getEmployee());
				dbInstitutionPerson.getEmployee().getGroupIds().add(dbEmployeeGroupId);
			}

			changes = true;
		}
		else if (dbInstitutionPerson.getEmployee().getGroupIds() != null && stilInstitutionPerson.getEmployee().getGroupId() != null) {
			List<String> existingGroupIds = dbInstitutionPerson.getEmployee().getGroupIds().stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = stilInstitutionPerson.getEmployee().getGroupId();

			for (String groupId : stilGroupIds) {
				if (!existingGroupIds.contains(groupId)) {
					DBEmployeeGroupId newGroupIdMapping = new DBEmployeeGroupId();
					newGroupIdMapping.setEmployee(dbInstitutionPerson.getEmployee());
					newGroupIdMapping.setGroupId(groupId);
					dbInstitutionPerson.getEmployee().getGroupIds().add(newGroupIdMapping);
					changes = true;
				}
			}

			for (String groupId : existingGroupIds) {
				if (!stilGroupIds.contains(groupId)) {
					dbInstitutionPerson.getEmployee().getGroupIds().removeIf(r -> r.getGroupId().equals(groupId));
					changes = true;
				}
			}
		}
		else if (dbInstitutionPerson.getEmployee().getGroupIds() != null && stilInstitutionPerson.getEmployee().getGroupId() == null) {
			dbInstitutionPerson.getEmployee().getGroupIds().clear();
			changes = true;
		}
		return changes;
	}

	private static boolean checkForUniLoginChanges(InstitutionPersonFullMyndighed stilInstitutionPerson, DBInstitutionPerson dbInstitutionPerson, boolean changes) {
		UniLoginFull stilUNILogin = stilInstitutionPerson.getUNILogin();
		if (stilUNILogin != null) {
			if (dbInstitutionPerson.getUniLogin() == null) {
				dbInstitutionPerson.setUniLogin(new DBUniLogin());
				changes = true;
			}

			if (!Objects.equals(dbInstitutionPerson.getUniLogin().getUserId(), stilUNILogin.getUserId())) {
				dbInstitutionPerson.getUniLogin().setUserId(stilUNILogin.getUserId());
				changes = true;
			}
			if (!Objects.equals(dbInstitutionPerson.getUniLogin().getCivilRegistrationNumber(), stilUNILogin.getCivilRegistrationNumber())) {
				dbInstitutionPerson.getUniLogin().setCivilRegistrationNumber(stilUNILogin.getCivilRegistrationNumber());
				changes = true;
			}
			if (!Objects.equals(dbInstitutionPerson.getUniLogin().getInitialPassword(), stilUNILogin.getInitialPassword())) {
				dbInstitutionPerson.getUniLogin().setInitialPassword(stilUNILogin.getInitialPassword());
				changes = true;
			}
			if (!Objects.equals(dbInstitutionPerson.getUniLogin().getPasswordState().name(), stilUNILogin.getPasswordState().name())) {
				dbInstitutionPerson.getUniLogin().setPasswordState(DBPasswordState.from(stilUNILogin.getPasswordState()));
				changes = true;
			}
			if (!Objects.equals(dbInstitutionPerson.getUniLogin().getName(), stilUNILogin.getName())) {
				dbInstitutionPerson.getUniLogin().setName(stilUNILogin.getName());
				changes = true;
			}
		}
		return changes;
	}

	private void handleGroupsOnYearChange(DBInstitution dbInstitution, InstitutionFullMyndighed stilInstitution) {
		if (dbInstitution.getGroups() == null) {
			dbInstitution.setGroups(new ArrayList<>());
		}

		List<DBInstitutionPerson> studentsInInstitution = dbInstitution.getInstitutionPersons().stream().filter(i -> i.getStudent() != null).collect(Collectors.toList());

		// to avoid duplicate names on groups in incoming data
		Map<String, Integer> counterMap = new HashMap<>();
		setDuplicateNames(stilInstitution, counterMap);

		Map<Integer, List<Group>> stilGroupYearMap = generateStilGroupYearMap(stilInstitution);
		Map<Integer, List<DBGroup>> dbGroupYearMap = generateDbGroupYearMap(dbInstitution);

		// a map to exchange the old db groupId to the new one
		Map<String, DBGroup> stilGroupIdToDbMap = new HashMap<>();

		for (Map.Entry<Integer, List<Group>> entry : stilGroupYearMap.entrySet()) {
			int level = entry.getKey();
			if (level > 0){
				List<Group> newSchoolYear = entry.getValue();
				List<DBGroup> oldSchoolYear = dbGroupYearMap.get(entry.getKey() - 1) == null ? new ArrayList<>() : dbGroupYearMap.get(entry.getKey() - 1);

				// check if we can match all STIL groups to db groups on name fx 4a -> 5a
				boolean allMatchForYear = true;
				Map<String, DBGroup> stilGroupIdToDbMapTemp = new HashMap<>();
				for (Group newSchoolYearGroup : newSchoolYear) {
					DBGroup matchOnName = oldSchoolYear.stream().filter(o -> Objects.equals(o.getGroupLevel(), String.valueOf(level - 1)) && Objects.equals(o.getLine(), newSchoolYearGroup.getLine())).findAny().orElse(null);
					if (matchOnName == null) {
						allMatchForYear = false;
						break;
					} else {
						stilGroupIdToDbMapTemp.put(newSchoolYearGroup.getGroupId(), matchOnName);
					}
				}

				if (allMatchForYear) {
					// if all classes for this level can be matched - > add to stilId:dbgroup map
					stilGroupIdToDbMap.putAll(stilGroupIdToDbMapTemp);
				} else {
					// check for match on students instead
					for (Group group : newSchoolYear) {
						List<InstitutionPersonFullMyndighed> studentsInNewGroup = stilInstitution.getInstitutionPerson().stream().filter(i -> i.getStudent() != null && i.getStudent().getGroupId().contains(group.getGroupId())).collect(Collectors.toList());
						Set<String> cprsInNewGroup = studentsInNewGroup.stream().map(s -> s.getPerson().getCivilRegistrationNumber()).collect(Collectors.toSet());
						List<DBGroup> dbGroupsOnLevelBellow = oldSchoolYear.stream().filter(o -> Objects.equals(o.getGroupLevel(), String.valueOf(level - 1))).collect(Collectors.toList());
						for (DBGroup dbGroup : dbGroupsOnLevelBellow) {
							List<DBInstitutionPerson> studentsInDBGroup = getDBStudentsInGroup(dbGroup, studentsInInstitution);
							List<DBInstitutionPerson> alsoInNewGroup = studentsInDBGroup.stream().filter(s -> cprsInNewGroup.contains(s.getPerson().getCivilRegistrationNumber())).collect(Collectors.toList());

							if (studentsInNewGroup.size() == alsoInNewGroup.size()) {
								// all students match = we have a group match
								stilGroupIdToDbMap.put(group.getGroupId(), dbGroup);
							} else {
								double seventyPercent = 0.7 * studentsInDBGroup.size();
								if (alsoInNewGroup.size() >= seventyPercent) {
									// at least 70% of the students in studentsInDBGroup is present in alsoInNewGroup = we have a group match
									stilGroupIdToDbMap.put(group.getGroupId(), dbGroup);
								} else {
									// we can't find a match - update the normal way
								}
							}
						}
					}
				}

				updateAndCreateGroups(entry.getValue(), dbInstitution, stilGroupIdToDbMap);
			} else {
				// group with no level, grade 0 or above grade 9 - update the normal way
				updateAndCreateGroups(entry.getValue(), dbInstitution, null);
			}
		}

		// remove groups old groups
		Set<String> stilGroupIds = stilInstitution.getGroup().stream().map(Group::getGroupId).collect(Collectors.toSet());
		List<DBGroup> toBeDeleted = dbInstitution.getGroups().stream().filter(g -> !stilGroupIds.contains(g.getGroupId())).collect(Collectors.toList());
		toBeDeleted.forEach(group -> group.setDeleted(true));
	}

	private static List<DBInstitutionPerson> getDBStudentsInGroup(DBGroup dbGroup, List<DBInstitutionPerson> studentsInInstitution) {
		List<DBInstitutionPerson> studentsInDBGroup = new ArrayList<>();
		for (DBInstitutionPerson student : studentsInInstitution) {
			List<String> groupIds = student.getStudent().getGroupIds().stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			if (groupIds.contains(dbGroup.getGroupId())) {
				studentsInDBGroup.add(student);
			}
		}
		return studentsInDBGroup;
	}

	private static void setDuplicateNames(InstitutionFullMyndighed stilInstitution, Map<String, Integer> counterMap) {
		for (Group group : stilInstitution.getGroup()) {

			// start by fixing name, in case there are duplicates in input

			int count = 0;

			if (counterMap.containsKey(group.getGroupName().toLowerCase())) {
				count = counterMap.get(group.getGroupName().toLowerCase());
			}

			counterMap.put(group.getGroupName().toLowerCase(), ++count);

			if (count > 1) {
				group.setGroupName(group.getGroupName() + " " + count);
			}
		}
	}

	private void updateAndCreateGroups(List<Group> stilGroups, DBInstitution dbInstitution, Map<String, DBGroup> stilToDbMap) {
		for (Group group : stilGroups) {
			DBGroup existingGroup = null;
			if (stilToDbMap != null) {
				existingGroup = stilToDbMap.get(group.getGroupId());
			}

			if (existingGroup == null) {
				existingGroup = dbInstitution.getGroups().stream().filter(g -> g.getGroupId().equals(group.getGroupId())).findAny().orElse(null);
			}

			if (existingGroup == null) {
				// create group for institution
				DBGroup dbGroup = new DBGroup();
				dbGroup.setInstitution(dbInstitution);
				dbGroup.setDeleted(false);
				dbGroup.copyFields(group);
				dbInstitution.getGroups().add(dbGroup);
			} else {
				// update group
				existingGroup.setDeleted(false);
				existingGroup.copyFields(group);
			}
		}
	}

	private Map<Integer, List<Group>> generateStilGroupYearMap(InstitutionFullMyndighed stilInstitution) {
		Map<Integer, List<Group>> result = new HashMap<>();
		for (Group group : stilInstitution.getGroup()) {
			int level = getLevel(group.getGroupLevel());

			if (!result.containsKey(level)) {
				result.put(level, new ArrayList<>());
			}

			result.get(level).add(group);
		}

		return result;
	}

	private Map<Integer, List<DBGroup>> generateDbGroupYearMap(DBInstitution dbInstitution) {
		Map<Integer, List<DBGroup>> result = new HashMap<>();
		for (DBGroup group : dbInstitution.getGroups()) {
			int level = getLevel(group.getGroupLevel());

			if (!result.containsKey(level)) {
				result.put(level, new ArrayList<>());
			}

			result.get(level).add(group);
		}

		return result;
	}

	private int getLevel(String levelString) {
		int level;
		try {
			level = Integer.parseInt(levelString);

			// 10. klasser should always result in a new group = update like normal
			if (level > 9) {
				level = -1;
			}
		} catch (Exception e) {
			// -1 means a group with no level
			level = -1;
		}
		return level;
	}

	private InstitutionChangeProposal createChangeProposal(DBInstitution dbInstitution,
			InstitutionFullMyndighed stilInstitution,
			long dbCount, long stilCount) {

		InstitutionChangeProposal changeProposal = new InstitutionChangeProposal();
		changeProposal.setInstitution(dbInstitution);
		changeProposal.setCreatedDate(LocalDateTime.now());

		String errorMessage = "Institutionen har " + dbCount + " brugere i databasen, men i STIL har institutionen kun " + stilCount + " brugere.";
		changeProposal.setTooFewPeopleErrorMessage(errorMessage);

		List<ProposedPersonChange> proposedChanges = new ArrayList<>();

		List<DBInstitutionPerson> dbPersons = dbInstitution.getInstitutionPersons().stream()
				.filter(p -> !p.isDeleted())
				.collect(Collectors.toList());

		List<InstitutionPersonFullMyndighed> stilPersons = stilInstitution.getInstitutionPerson() != null ?
				stilInstitution.getInstitutionPerson() : new ArrayList<>();

		// Find deletions (in DB but not in STIL)
		for (DBInstitutionPerson dbPerson : dbPersons) {
			if (dbPerson.isApiOnly()) {
				continue;
			}

			String dbCpr = dbPerson.getPerson().getCivilRegistrationNumber();

			boolean foundInStil = stilPersons.stream()
					.anyMatch(stilPerson -> Objects.equals(stilPerson.getPerson().getCivilRegistrationNumber(), dbCpr));

			if (!foundInStil) {
				ProposedPersonChange personChange = createProposedPersonChange(
						changeProposal,
						PersonChangeType.DELETE,
						dbPerson.getPerson().isProtected() ? dbPerson.getPerson().getAliasFirstName() : dbPerson.getPerson().getFirstName(),
						dbPerson.getPerson().isProtected() ? dbPerson.getPerson().getAliasFamilyName() : dbPerson.getPerson().getFamilyName(),
						dbPerson.getUniLogin().getUserId(),
						getPersonType(dbPerson)
				);
				proposedChanges.add(personChange);
			}
		}

		// Find additions (in STIL but not in DB)
		for (InstitutionPersonFullMyndighed stilPerson : stilPersons) {
			String stilCpr = stilPerson.getPerson().getCivilRegistrationNumber();

			boolean foundInDb = dbPersons.stream()
					.anyMatch(dbPerson -> Objects.equals(dbPerson.getPerson().getCivilRegistrationNumber(), stilCpr));

			if (!foundInDb) {
				ProposedPersonChange personChange = createProposedPersonChange(
						changeProposal,
						PersonChangeType.CREATE,
						stilPerson.getPerson().isProtected() ? stilPerson.getPerson().getAliasFirstName() : stilPerson.getPerson().getFirstName(),
						stilPerson.getPerson().isProtected() ? stilPerson.getPerson().getAliasFamilyName() : stilPerson.getPerson().getFamilyName(),
						stilPerson.getUNILogin().getUserId(),
						getPersonType(stilPerson)
				);
				proposedChanges.add(personChange);
			}
		}

		changeProposal.setProposedPersonChanges(proposedChanges);

		return changeProposal;
	}

	private ProposedPersonChange createProposedPersonChange(InstitutionChangeProposal changeProposal,
			PersonChangeType changeType, String firstName, String surname, String uniLogin, String personType) {
		ProposedPersonChange personChange = new ProposedPersonChange();
		personChange.setChangeProposal(changeProposal);
		personChange.setChangeType(changeType);
		personChange.setFirstName(firstName);
		personChange.setFamilyName(surname);
		personChange.setUniLoginUserId(uniLogin);
		personChange.setPersonType(personType);

		return personChange;
	}

	private String getPersonType(DBInstitutionPerson dbPerson) {
		if (dbPerson.getEmployee() != null) {
			return "Medarbejder";
		} else if (dbPerson.getStudent() != null) {
			return "Elev";
		} else if (dbPerson.getExtern() != null) {
			return "Ekstern";
		}
		return "UNKNOWN";
	}

	private String getPersonType(InstitutionPersonFullMyndighed stilPerson) {
		if (stilPerson.getEmployee() != null) {
			return "Medarbejder";
		} else if (stilPerson.getStudent() != null) {
			return "Elev";
		} else if (stilPerson.getExtern() != null) {
			return "Ekstern";
		}
		return "UNKNOWN";
	}

	@Transactional
	public void deleteInstitutions(List<InstitutionDTO> institutionDTOList) {
		for (DBInstitution dbInstitution : institutionService.findAll()) {
			if (dbInstitution.isNonSTILInstitution()) {
				continue;
			}

			if (!dbInstitution.isDeleted() && institutionDTOList.stream().noneMatch(i -> i.getInstitutionNumber().equals(dbInstitution.getInstitutionNumber()))) {
				dbInstitution.setDeleted(true);
				for (DBGroup group : dbInstitution.getGroups()) {
					if (!group.isDeleted()) {
						group.setDeleted(true);
					}
				}
				for (DBInstitutionPerson institutionPerson : dbInstitution.getInstitutionPersons()) {
					if (!institutionPerson.isDeleted()) {
						institutionPerson.setStilDeleted(LocalDateTime.now());
						institutionPerson.setDeleted(true);
					}
				}

				institutionService.save(dbInstitution);
			}
		}
	}
}
