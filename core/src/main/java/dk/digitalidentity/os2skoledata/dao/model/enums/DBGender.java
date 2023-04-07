package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin_dk.data.Gender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBGender {
	M, K, UNKNOWN;

	public static DBGender from(Gender gender) {
		try {
			return DBGender.valueOf(gender.name());
		}
		catch (Exception e) {
			log.error(DBGender.class.getName() + " enum doesn't contain value for: " + gender);
		}
		
		return UNKNOWN;
	}
}
