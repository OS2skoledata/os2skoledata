package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.ClassroomAdmin;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ClassroomAdminDao extends JpaRepository<ClassroomAdmin, Long> {
	List<ClassroomAdmin> findAll();
	List<ClassroomAdmin> findByRoleIn(Set<StudentPasswordChangerSTILRoles> roles);
	ClassroomAdmin findByUsername(String name);
}
