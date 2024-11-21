package dk.digitalidentity.os2skoledata.dao;

import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordSettingDao extends JpaRepository<PasswordSetting, Long> {
	PasswordSetting findByGradeGroup(GradeGroup gradeGroup);
}
