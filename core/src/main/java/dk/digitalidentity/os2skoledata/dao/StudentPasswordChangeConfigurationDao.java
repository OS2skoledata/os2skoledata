package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPasswordChangeConfigurationDao extends JpaRepository<StudentPasswordChangeConfiguration, Long> {
	StudentPasswordChangeConfiguration findById(long id);
}
