package dk.digitalidentity.os2skoledata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.SettingDao;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;

@EnableScheduling
@Service
@EnableCaching
public class SettingService {

	@Autowired
	private SettingDao settingDao;

	@Autowired
	private SettingService self;
	
	public Long getLongValueByKey(CustomerSetting customerSetting) {
		Long value;

		Setting setting = getByKey(customerSetting);

		if (setting != null) {
			value = Long.parseLong(setting.getValue());
		}
		else {
			Setting newSetting = new Setting();
			newSetting.setKey(customerSetting.toString());
			newSetting.setValue(customerSetting.getDefaultValue());
			settingDao.save(newSetting);
			value = Long.parseLong(customerSetting.getDefaultValue());
		}

		return value;
	}

	public int getIntegerValueByKey(String customerSetting) {
		Setting setting = self.getByKey(customerSetting);
		if (setting != null) {
			return Integer.parseInt(setting.getValue());
		}

		return 0;
	}

	public boolean getBooleanValueByKey(String customerSetting) {
		Setting setting = self.getByKey(customerSetting);
		if (setting != null) {
			if (setting.getValue().equalsIgnoreCase("true")) {
				return true;
			}
		}

		return false;
	}

	public String getStringValueByKey(CustomerSetting customerSetting) {
		return getStringValueByKey(customerSetting.toString(), customerSetting.getDefaultValue());
	}

	public String getStringValueByKey(String customerSetting) {
		return getStringValueByKey(customerSetting, null);
	}

	public String getStringValueByKey(String customerSetting, String defaultValue) {
		Setting setting = self.getByKey(customerSetting);
		if (setting != null) {
			return setting.getValue();
		}

		return defaultValue;
	}

	public Setting getByKey(CustomerSetting key) {
		return self.getByKey(key.toString());
	}

	@Cacheable("settings")
	public Setting getByKey(String key) {
		return settingDao.findByKey(key);
	}

	@CacheEvict(value = "settings", allEntries = true)
	public void save(Setting setting) {
		settingDao.save(setting);
	}

	@Scheduled(fixedRate = 5 * 60 * 1000)
	@CacheEvict(value = "settings", allEntries = true)
	public void clearGetSettingCache() {
		// Clear cache
	}

	public void setValueForKey(String key, boolean enabled) {
		Setting setting = settingDao.findByKey(key);
		if (setting == null) {
			setting = new Setting();
			setting.setKey(key);
		}

		setting.setValue(Boolean.toString(enabled));
		self.save(setting);
	}

	public void setValueForKey(String key, String value) {
		Setting setting = settingDao.findByKey(key);
		if (setting == null) {
			setting = new Setting();
			setting.setKey(key);
		}

		setting.setValue(value);
		self.save(setting);
	}
}