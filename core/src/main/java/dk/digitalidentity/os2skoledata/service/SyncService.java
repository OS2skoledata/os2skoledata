package dk.digitalidentity.os2skoledata.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import https.unilogin_dk.data.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.service.stil.StilService;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionPersonFullMyndighed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SyncService {

	@Autowired
	private StilService stilService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

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
			institutionService.save(dbInstitution);
		} else {
			// sanity check: too few people?
			if (dbInstitution != null && dbInstitution.getInstitutionPersons() != null && !dbInstitution.getInstitutionPersons().isEmpty()) {
				if (stilInstitution.getInstitutionPerson() == null || stilInstitution.getInstitutionPerson().isEmpty()) {
					log.error("Institution in db has people, but institution in STIL has no people - skipping: " + institutionDTO);
					return;
				}

				long dbCount = dbInstitution.getInstitutionPersons().stream().filter(p -> !p.isDeleted()).count();
				int stilCount = stilInstitution.getInstitutionPerson().size();

				if (stilCount <= dbCount/2) {
					log.error("Institution in db has " + dbCount + " people, but institution in STIL has " + stilCount + " people. Might be an error - skipping sync of: " + institutionDTO.getInstitutionNumber());
					return;
				}
			}
		}


		if (dbInstitution == null) {
			dbInstitution = new DBInstitution();
			dbInstitution.copyFields(stilInstitution);
			dbInstitution.setType(institutionDTO.getType());

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

			if (changes) {
				log.info("Updating core data on institution: " + stilInstitution.getInstitutionName() + " / " + stilInstitution.getInstitutionNumber());
				institutionService.save(dbInstitution);
			}
		}

		if (stilInstitution.getInstitutionPerson() != null && !stilInstitution.getInstitutionPerson().isEmpty()) {
			for (InstitutionPersonFullMyndighed stilInstitutionPerson : stilInstitution.getInstitutionPerson()) {
				DBInstitutionPerson dbInstitutionPerson = institutionPersonService.findByLocalPersonIdIncludingDeleted(stilInstitutionPerson.getLocalPersonId(), institutionDTO.getInstitutionNumber());

				if (dbInstitutionPerson == null) {
					log.info("Creating institutionPerson: " + stilInstitutionPerson.getLocalPersonId());

					dbInstitutionPerson = new DBInstitutionPerson();
					dbInstitutionPerson.copyFields(stilInstitutionPerson);
					dbInstitutionPerson.setInstitution(dbInstitution);
					dbInstitutionPerson.setStilCreated(LocalDateTime.now());
					institutionPersonService.save(dbInstitutionPerson);
				}
				else {
					boolean changes = false;

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

					if (!dbInstitutionPerson.apiEquals(stilInstitutionPerson)) {
						dbInstitutionPerson.copyFields(stilInstitutionPerson);
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
				.filter(dbInstitutionPerson -> !dbInstitutionPerson.isDeleted())
				.filter(dbInstitutionPerson -> stilInstitution.getInstitutionPerson().stream()
						.noneMatch(stilInstitutionPerson -> Objects.equals(stilInstitutionPerson.getLocalPersonId(), dbInstitutionPerson.getLocalPersonId())))
				.collect(Collectors.toList());

		toBeDeleted.forEach(person -> {
			log.info("Deleting institutionPerson: " + person.getId() + " " + person.getLocalPersonId());
			person.setDeleted(true);
			person.setStilDeleted(LocalDateTime.now());
		});

		if (toBeDeleted.size() > 0) {
			institutionPersonService.saveAll(toBeDeleted);
		}
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
}
