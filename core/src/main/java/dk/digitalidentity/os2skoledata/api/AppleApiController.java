package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.api.model.enums.PersonRole;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import dk.digitalidentity.os2skoledata.service.model.NameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apple")
public class AppleApiController {

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SettingService settingService;

	private record AppleFullLoadDto(List<AppleInstitutionDto> institutions, List<AppleGroupDto> groups, List<AppleUserDto> students, List<AppleUserDto> staff, List<AppleRosterDto> rosters) {}
	private record AppleInstitutionDto(long id, String name, boolean locked) {}
	private record AppleGroupDto(long id, String stilId, String groupName, long institutionId, Set<String> teacherUniIds) {}
	private record AppleUserDto(String UniId, String firstName, String lastName, String username, long primaryInstitutionId, Integer level) {}
	private record AppleRosterDto(String UniId, long groupId) {}
	@GetMapping("/full")
	public AppleFullLoadDto getAppleFullLoad() {
		// filter out non stil institutions as they have no groups
		List<DBInstitution> institutions = institutionService.findAllActive()
				.stream()
				.filter(i -> !i.isNonSTILInstitution())
				.collect(Collectors.toList());
		List<DBInstitutionPerson> dbInstitutionPeople = institutionPersonService.findAllNotDeleted()
				.stream()
				.filter(i -> !i.isApiOnly())
				.collect(Collectors.toList());
		List<DBGroup> dbMainGroups = groupService.findAllNotDeletedMainGroups();

		Map<Long, Set<String>> groupIdEmployeeUniIdMap = new HashMap<>();
		List<AppleRosterDto> rosterDtos = new ArrayList<>();
		List<AppleUserDto> studentDtos = new ArrayList<>();
		List<AppleUserDto> staffDtos = new ArrayList<>();
		buildUsersAndRosters(dbInstitutionPeople, dbMainGroups, groupIdEmployeeUniIdMap, rosterDtos, studentDtos, staffDtos);

		List<AppleGroupDto> groupDtos = dbMainGroups.stream()
				.map(g -> new AppleGroupDto(
						g.getId(),
						g.getGroupId(),
						g.getGroupName(),
						g.getInstitution().getId(),
						groupIdEmployeeUniIdMap.get(g.getId())
				))
				.toList();

		List<AppleInstitutionDto> institutionDtos = institutions.stream()
				.map(i -> new AppleInstitutionDto(
						i.getId(),
						i.getInstitutionName(),
						settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + i.getInstitutionNumber())
				)).toList();

		return new AppleFullLoadDto(institutionDtos, groupDtos, studentDtos, staffDtos, rosterDtos);
	}

	record InstitutionAndLevelDTO(Integer level, long institutionId) {}
	private InstitutionAndLevelDTO resolvePrimaryInstitutionAndLevel(List<DBInstitutionPerson> institutionPeople, DBInstitutionPerson defaultPerson, PersonRole personRole, List<DBGroup> dbMainGroups) {
		DBInstitutionPerson person = institutionPeople.stream()
										.filter(DBInstitutionPerson::isPrimaryInstitution)
										.findFirst()
										.or(() -> institutionPeople.stream()
											.filter(p -> p.getInstitution().getType() == InstitutionType.SCHOOL)
											.findFirst())
										.orElse(defaultPerson);

		DBGroup mainGroup = personRole.equals(PersonRole.EMPLOYEE) ? null : findMainGroup(person.getStudent().getMainGroupId(), dbMainGroups);

		return new InstitutionAndLevelDTO(mainGroup != null ? getGroupLevelAsInt(mainGroup) : null, person.getInstitution().getId());
	}

	private Integer getGroupLevelAsInt(DBGroup mainGroup) {
		try {
			return Integer.parseInt(mainGroup.getGroupLevel());
		}
		catch (Exception e) {
			return null;
		}
	}

	private DBGroup findMainGroup(String groupId, List<DBGroup> dbMainGroups) {
		return dbMainGroups.stream()
				.filter(g -> g.getGroupId().equals(groupId))
				.findFirst()
				.orElse(null);
	}

	private void buildUsersAndRosters(
			List<DBInstitutionPerson> dbInstitutionPeople,
			List<DBGroup> dbMainGroups,
			Map<Long, Set<String>> groupIdEmployeeUniIdMap,
			List<AppleRosterDto> rosterDtos,
			List<AppleUserDto> studentDtos,
			List<AppleUserDto> staffDtos
	) {
		Map<String, List<DBInstitutionPerson>> personsByUniLogin = dbInstitutionPeople.stream()
				.filter(p -> p.getUniLogin() != null && p.getUniLogin().getUserId() != null)
				.collect(Collectors.groupingBy(p -> p.getUniLogin().getUserId()));

		for (Map.Entry<String, List<DBInstitutionPerson>> entry : personsByUniLogin.entrySet()) {
			String uniId = entry.getKey();
			List<DBInstitutionPerson> matchingPeople = entry.getValue();
			DBInstitutionPerson defaultPerson = matchingPeople.get(0);

			if (!StringUtils.hasLength(defaultPerson.getUsername())) {
				continue;
			}

			PersonRole role = institutionPersonService.findGlobalRole(defaultPerson, matchingPeople);
			NameDTO calculatedName = institutionPersonService.calculateName(matchingPeople);

			if (PersonRole.EMPLOYEE.equals(role) || PersonRole.STUDENT.equals(role)) {
				if (PersonRole.STUDENT.equals(role) && defaultPerson.getStudent() != null && !defaultPerson.getStudent().getRole().equals(DBStudentRole.ELEV)) {
					continue;
				}

				InstitutionAndLevelDTO institutionAndLevelDTO = resolvePrimaryInstitutionAndLevel(matchingPeople, defaultPerson, role, dbMainGroups);
				AppleUserDto dto = new AppleUserDto(
						uniId,
						calculatedName.getFirstname(),
						calculatedName.getSurname(),
						defaultPerson.getUsername(),
						institutionAndLevelDTO.institutionId,
						institutionAndLevelDTO.level
				);

				if (role == PersonRole.STUDENT) {
					studentDtos.add(dto);
					matchingPeople.stream()
							.filter(s -> s.getStudent() != null)
							.map(p -> findMainGroup(p.getStudent().getMainGroupId(), dbMainGroups))
							.filter(Objects::nonNull)
							.forEach(g -> rosterDtos.add(new AppleRosterDto(uniId, g.getId())));
				} else {
					staffDtos.add(dto);
					matchingPeople.stream()
							.filter(e -> e.getEmployee() != null)
							.flatMap(p -> p.getEmployee().getGroupIds().stream())
							.map(id -> findMainGroup(id.getGroupId(), dbMainGroups))
							.filter(Objects::nonNull)
							.forEach(g -> groupIdEmployeeUniIdMap
									.computeIfAbsent(g.getId(), k -> new HashSet<>())
									.add(uniId));
				}
			}
		}
	}

}
