package dk.digitalidentity.os2skoledata.service.api;

import dk.digitalidentity.os2skoledata.api.ImportApiController;
import dk.digitalidentity.os2skoledata.api.model.SchoolPersonDTO;
import dk.digitalidentity.os2skoledata.api.model.SchoolPersonImportDTO;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportRole;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportSubrole;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployee;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExtern;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.DBStudent;
import dk.digitalidentity.os2skoledata.dao.model.DBUniLogin;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBPasswordState;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImportApiService {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	public SchoolPersonDTO handleCreate(SchoolPersonImportDTO importDTO, DBInstitution institution, ImportApiController importApiController) {
		DBInstitutionPerson newPerson = new DBInstitutionPerson();

		DBPerson dbPerson = new DBPerson();
		dbPerson.setProtected(Boolean.TRUE.equals(importDTO.getNameProtected()));
		dbPerson.setAliasFirstName(importDTO.getAliasFirstName());
		dbPerson.setAliasFamilyName(importDTO.getAliasFamilyName());
		dbPerson.setCivilRegistrationNumber(importDTO.getCpr());
		dbPerson.setFirstName(importDTO.getFirstName());
		dbPerson.setFamilyName(importDTO.getFamilyName());
		dbPerson.setVerificationLevel(0);

		DBUniLogin dbUniLogin = new DBUniLogin();
		dbUniLogin.setCivilRegistrationNumber(importDTO.getCpr());
		dbUniLogin.setName(importDTO.getFirstName() + " " + importDTO.getFamilyName());
		dbUniLogin.setPasswordState(DBPasswordState.UNKNOWN);

		// if no unilogin user id generate uuid to make sure its unique
		dbUniLogin.setUserId(importDTO.getUniID() == null || importDTO.getUniID().isEmpty() ? UUID.randomUUID().toString() : importDTO.getUniID());

		if (importDTO.getMainRole().equals(PossibleImportRole.ANSAT)) {
			DBEmployee employee = new DBEmployee();

			employee.setRoles(new ArrayList<>());
			for (PossibleImportSubrole role : importDTO.getRoles()) {
				DBRole roleMapping = new DBRole();
				roleMapping.setEmployee(employee);
				roleMapping.setEmployeeRole(DBEmployeeRole.from(role));
				employee.getRoles().add(roleMapping);
			}

			employee.setGroupIds(new ArrayList<>());
			if (importDTO.getGroupIDs() != null) {
				for (String groupID : importDTO.getGroupIDs()) {
					DBEmployeeGroupId groupMapping = new DBEmployeeGroupId();
					groupMapping.setEmployee(employee);
					groupMapping.setGroupId(groupID);
					employee.getGroupIds().add(groupMapping);
				}
			}

			newPerson.setEmployee(employee);
		} else if (importDTO.getMainRole().equals(PossibleImportRole.EKSTERN)) {
			DBExtern extern = new DBExtern();
			extern.setRole(DBExternalRoleType.from(importDTO.getRoles().get(0)));

			extern.setGroupIds(new ArrayList<>());
			if (importDTO.getGroupIDs() != null) {
				for (String groupID : importDTO.getGroupIDs()) {
					DBExternGroupId groupMapping = new DBExternGroupId();
					groupMapping.setExtern(extern);
					groupMapping.setGroupId(groupID);
					extern.getGroupIds().add(groupMapping);
				}
			}

			newPerson.setExtern(extern);
		} else if (importDTO.getMainRole().equals(PossibleImportRole.ELEV)) {
			DBStudent student = new DBStudent();
			student.setRole(DBStudentRole.from(importDTO.getRoles().get(0)));
			newPerson.setStudent(student);
		}

		newPerson.setPerson(dbPerson);
		newPerson.setUniLogin(dbUniLogin);
		newPerson.setInstitution(institution);
		newPerson.setReservedUsername(importDTO.getReservedUsername());

		newPerson.setStilCreated(LocalDateTime.now());
		newPerson.setSource(importDTO.getSource());
		newPerson.setApiOnly(importDTO.getApiOnly() == null ? false : importDTO.getApiOnly());

		if (Boolean.TRUE.equals(importDTO.getPrimary())) {
			institutionPersonService.resetPrimary(importDTO.getCpr());
			newPerson.setPrimaryInstitution(true);
		}

		newPerson = institutionPersonService.save(newPerson);

		return toDto(newPerson);
	}

	public SchoolPersonDTO handlePatchUpdate(DBInstitutionPerson existingPerson, SchoolPersonImportDTO importDTO) {
		// hibernate handles checking for changes and saving
		DBPerson dbPerson = existingPerson.getPerson();
		if (importDTO.getNameProtected() != null) {
			dbPerson.setProtected(Boolean.TRUE.equals(importDTO.getNameProtected()));
		}
		if (importDTO.getAliasFirstName() != null) {
			dbPerson.setAliasFirstName(importDTO.getAliasFirstName());
		}
		if (importDTO.getAliasFamilyName() != null) {
			dbPerson.setAliasFamilyName(importDTO.getAliasFamilyName());
		}
		if (importDTO.getFirstName() != null) {
			dbPerson.setFirstName(importDTO.getFirstName());
		}
		if (importDTO.getFamilyName() != null) {
			dbPerson.setFamilyName(importDTO.getFamilyName());
		}

		DBUniLogin dbUniLogin = existingPerson.getUniLogin();
		if (importDTO.getFirstName() != null && importDTO.getFamilyName() != null) {
			String newName = importDTO.getFirstName() + " " + importDTO.getFamilyName();
			dbUniLogin.setName(newName);
		}
		if (importDTO.getUniID() != null) {
			dbUniLogin.setUserId(importDTO.getUniID());
		}

		if (importDTO.getMainRole() != null) {
			if (importDTO.getMainRole().equals(PossibleImportRole.ANSAT)) {
				DBEmployee employee = existingPerson.getEmployee();
				if (employee == null) {
					employee = new DBEmployee();
					employee.setGroupIds(new ArrayList<>());
					employee.setRoles(new ArrayList<>());
					existingPerson.setEmployee(employee);
					existingPerson.setExtern(null);
					existingPerson.setStudent(null);
				}
			} else if (importDTO.getMainRole().equals(PossibleImportRole.EKSTERN)) {
				DBExtern extern = existingPerson.getExtern();
				if (extern == null) {
					extern = new DBExtern();
					extern.setGroupIds(new ArrayList<>());
					existingPerson.setExtern(extern);
					existingPerson.setEmployee(null);
					existingPerson.setStudent(null);
				}
			} else if (importDTO.getMainRole().equals(PossibleImportRole.ELEV)) {
				DBStudent student = existingPerson.getStudent();
				if (student == null) {
					student = new DBStudent();
					existingPerson.setExtern(null);
					existingPerson.setEmployee(null);
					existingPerson.setStudent(student);
				}
			}
		}

		handleGroupsAndRoles(existingPerson, importDTO);

		if (importDTO.getSource() != null) {
			existingPerson.setSource(importDTO.getSource());
		}

		if (Boolean.TRUE.equals(importDTO.getPrimary())) {
			institutionPersonService.resetPrimary(importDTO.getCpr());
			existingPerson.setPrimaryInstitution(true);
		}

		if (importDTO.getApiOnly() != null) {
			existingPerson.setApiOnly(importDTO.getApiOnly());
		}

		existingPerson.setDeleted(false);
		existingPerson.setStilDeleted(null);

		existingPerson.setLastModified(LocalDateTime.now());

		return toDto(existingPerson);
	}

	private static void handleGroupsAndRoles(DBInstitutionPerson existingPerson, SchoolPersonImportDTO importDTO) {
		if (existingPerson.getEmployee() != null) {
			if (importDTO.getRoles() != null) {
				Set<DBEmployeeRole> existingRoles = existingPerson.getEmployee().getRoles().stream()
						.map(DBRole::getEmployeeRole)
						.collect(Collectors.toSet());
				Set<DBEmployeeRole> newRoles = importDTO.getRoles().stream()
						.map(DBEmployeeRole::from)
						.collect(Collectors.toSet());

				if (!existingRoles.equals(newRoles)) {
					// remove roles not in newRoles
					existingPerson.getEmployee().getRoles().removeIf(role -> !newRoles.contains(role.getEmployeeRole()));
					// add missing roles
					for (DBEmployeeRole role : newRoles) {
						if (existingPerson.getEmployee().getRoles().stream().noneMatch(r -> r.getEmployeeRole().equals(role))) {
							DBRole roleMapping = new DBRole();
							roleMapping.setEmployee(existingPerson.getEmployee());
							roleMapping.setEmployeeRole(role);
							existingPerson.getEmployee().getRoles().add(roleMapping);
						}
					}
				}
			}

			if (importDTO.getGroupIDs() != null) {
				Set<String> existingGroupIds = existingPerson.getEmployee().getGroupIds().stream()
						.map(DBEmployeeGroupId::getGroupId)
						.collect(Collectors.toSet());
				Set<String> newGroupIds = new HashSet<>(importDTO.getGroupIDs());

				if (!existingGroupIds.equals(newGroupIds)) {
					// remove groups not in newGroupIds
					existingPerson.getEmployee().getGroupIds().removeIf(group -> !newGroupIds.contains(group.getGroupId()));
					// add missing groups
					for (String groupId : newGroupIds) {
						if (existingPerson.getEmployee().getGroupIds().stream().noneMatch(g -> g.getGroupId().equals(groupId))) {
							DBEmployeeGroupId groupMapping = new DBEmployeeGroupId();
							groupMapping.setEmployee(existingPerson.getEmployee());
							groupMapping.setGroupId(groupId);
							existingPerson.getEmployee().getGroupIds().add(groupMapping);
						}
					}
				}
			}
		} else if (existingPerson.getExtern() != null) {
			if (importDTO.getRoles() != null && !importDTO.getRoles().isEmpty()) {
				DBExternalRoleType newRole = DBExternalRoleType.from(importDTO.getRoles().get(0));
				if (!newRole.equals(existingPerson.getExtern().getRole())) {
					existingPerson.getExtern().setRole(newRole);
				}
			}

			if (importDTO.getGroupIDs() != null) {
				Set<String> existingGroupIds = existingPerson.getExtern().getGroupIds().stream()
						.map(DBExternGroupId::getGroupId)
						.collect(Collectors.toSet());
				Set<String> newGroupIds = new HashSet<>(importDTO.getGroupIDs());

				if (!existingGroupIds.equals(newGroupIds)) {
					// remove groups not in newGroupIds
					existingPerson.getExtern().getGroupIds().removeIf(group -> !newGroupIds.contains(group.getGroupId()));
					// add missing groups
					for (String groupId : newGroupIds) {
						if (existingPerson.getExtern().getGroupIds().stream().noneMatch(g -> g.getGroupId().equals(groupId))) {
							DBExternGroupId groupMapping = new DBExternGroupId();
							groupMapping.setExtern(existingPerson.getExtern());
							groupMapping.setGroupId(groupId);
							existingPerson.getExtern().getGroupIds().add(groupMapping);
						}
					}
				}
			}
		} else if (existingPerson.getStudent() != null) {
			if (importDTO.getRoles() != null && !importDTO.getRoles().isEmpty()) {
				DBStudentRole newRole = DBStudentRole.from(importDTO.getRoles().get(0));
				if (!newRole.equals(existingPerson.getStudent().getRole())) {
					existingPerson.getStudent().setRole(newRole);
				}
			}

			// for now students from api can not be associated with groups
		}
	}

	public SchoolPersonDTO toDto(DBInstitutionPerson p) {
		return new SchoolPersonDTO(
				p.getId(),
				p.getPerson().getFirstName(),
				p.getPerson().getFamilyName(),
				p.getUsername(),
				p.getReservedUsername(),
				p.getPerson().getCivilRegistrationNumber(),
				p.getInstitution().getInstitutionName(),
				p.getInstitution().getInstitutionNumber(),
				getMainRole(p),
				getRoles(p),
				p.getSource(),
				p.getUniLogin().getUserId(),
				getGroupIds(p),
				p.isPrimaryInstitution(),
				p.getPerson().isProtected(),
				p.getPerson().getAliasFirstName(),
				p.getPerson().getAliasFamilyName(),
				p.isDeleted(),
				p.isApiOnly(),
				p.getStilCreated(),
				p.getStilDeleted(),
				p.getAdCreated(),
				p.getAdDeactivated(),
				p.getGwCreated(),
				p.getGwDeactivated(),
				p.getAzureCreated(),
				p.getAzureDeactivated());
	}

	private PossibleImportRole getMainRole(DBInstitutionPerson p) {
		if (p.getEmployee() != null) {
			return PossibleImportRole.ANSAT;
		}
		if (p.getExtern() != null) {
			return PossibleImportRole.EKSTERN;
		}
		return PossibleImportRole.ELEV;
	}

	private List<String> getGroupIds(DBInstitutionPerson p) {
		List<String> result = new ArrayList<>();
		if (p.getEmployee() != null) {
			result.addAll(p.getEmployee().getGroupIds().stream().map(g -> g.getGroupId()).collect(Collectors.toSet()));
		} else if (p.getExtern() != null) {
			result.addAll(p.getExtern().getGroupIds().stream().map(g -> g.getGroupId()).collect(Collectors.toSet()));
		}

		return result;
	}

	private List<PossibleImportSubrole> getRoles(DBInstitutionPerson p) {
		List<PossibleImportSubrole> result = new ArrayList<>();
		if (p.getEmployee() != null && p.getEmployee().getRoles() != null) {
			for (DBRole role : p.getEmployee().getRoles()) {
				PossibleImportSubrole importSubrole = PossibleImportSubrole.from(role.getEmployeeRole());
				if (importSubrole != null) {
					result.add(importSubrole);
				}
			}
		} else if (p.getExtern() != null) {
			PossibleImportSubrole importSubrole = PossibleImportSubrole.from(p.getExtern().getRole());
			if (importSubrole != null) {
				result.add(importSubrole);
			}
		}

		return result;
	}
}
