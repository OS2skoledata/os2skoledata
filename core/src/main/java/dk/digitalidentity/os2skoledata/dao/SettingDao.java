package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.Setting;

public interface SettingDao extends JpaRepository<Setting, Long> {
	Setting findById(long id);

	Setting findByKey(String key);

	List<Setting> findAll();
}
