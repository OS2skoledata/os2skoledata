package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;

public interface GroupDao extends JpaRepository<DBGroup, Long> {
	DBGroup findById(long id);
	
	List<DBGroup> findAll();
	List<DBGroup> findByInstitution(DBInstitution institution);
	List<DBGroup> findByInstitutionIdInAndDeletedFalse(List<Long> institutionIds);
	List<DBGroup> findByDeletedFalse();
	List<DBGroup> findByIdIn(List<Long> ids);
	List<DBGroup> findByDeletedFalseAndGroupType(DBImportGroupType groupType);
}
