package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.ClassroomAdminDao;
import dk.digitalidentity.os2skoledata.dao.model.ClassroomAdmin;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassroomAdminService {

	@Autowired
	private ClassroomAdminDao classroomAdminDao;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	public List<ClassroomAdmin> getAll() {
		return classroomAdminDao.findAll();
	}

	public boolean isClassroomAdmin(String username) {
		if (username == null) {
			return false;
		}

		ClassroomAdmin classroomAdmin = classroomAdminDao.findByUsername(username);
		if (classroomAdmin != null) {
			return true;
		}

		Set<StudentPasswordChangerSTILRoles> roles = new HashSet<>();
		List<DBInstitutionPerson> people = institutionPersonService.findByUsernameAndDeletedFalse(username);
		for (DBInstitutionPerson person : people) {
			if (person.getEmployee() != null) {
				for (DBRole role : person.getEmployee().getRoles()) {
					roles.add(StudentPasswordChangerSTILRoles.getFromInstitutionPersonRoleAsString(role.toString()));
				}
			}
			else if (person.getExtern() != null) {
				roles.add(StudentPasswordChangerSTILRoles.getFromInstitutionPersonRoleAsString(person.getExtern().getRole().toString()));
			}
		}

		List<ClassroomAdmin> admins = classroomAdminDao.findByRoleIn(roles);
		return !admins.isEmpty();
	}

	public void delete(long id) {
		classroomAdminDao.deleteById(id);
	}

	public void save(ClassroomAdmin classroomAdmin) {
		classroomAdminDao.save(classroomAdmin);
	}
}
