package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordAdminDao extends JpaRepository<PasswordAdmin, Long> {
	List<PasswordAdmin> findAll();
	PasswordAdmin findByUsername(String username);
}
