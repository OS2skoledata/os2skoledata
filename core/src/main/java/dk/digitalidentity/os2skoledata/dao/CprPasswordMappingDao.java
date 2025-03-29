package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.CprPasswordMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CprPasswordMappingDao extends JpaRepository<CprPasswordMapping, Long> {
	CprPasswordMapping findByCpr(String cpr);
}
