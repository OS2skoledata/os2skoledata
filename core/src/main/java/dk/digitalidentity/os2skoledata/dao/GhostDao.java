package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.Ghost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GhostDao extends JpaRepository<Ghost, Long> {
	List<Ghost> findByActiveUntilAfter(LocalDate date);
	Ghost findByUsername(String username);
	void deleteByActiveUntilBefore(LocalDate date);
}
