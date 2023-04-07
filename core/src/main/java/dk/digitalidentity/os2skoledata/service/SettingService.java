package dk.digitalidentity.os2skoledata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.SettingDao;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;

@Service
public class SettingService {

	@Autowired
	private SettingDao settingDao;

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
		Setting setting = getByKey(customerSetting);
		if (setting != null) {
			return Integer.parseInt(setting.getValue());
		}

		return 0;
	}

	public Setting getByKey(CustomerSetting key) {
		return getByKey(key.toString());
	}

	public Setting getByKey(String key) {
		return settingDao.findByKey(key);
	}
	

	public void save(Setting setting) {
		settingDao.save(setting);
	}

	
	public void setValueForKey(String key, boolean enabled) {
		Setting setting = settingDao.findByKey(key);
		if (setting == null) {
			setting = new Setting();
			setting.setKey(key);
		}

		setting.setValue(Boolean.toString(enabled));
		settingDao.save(setting);
	}
}