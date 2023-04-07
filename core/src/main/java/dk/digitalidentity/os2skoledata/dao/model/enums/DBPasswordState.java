package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin_dk.data.PasswordState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBPasswordState {
	VALID, CHANGED, UNKNOWN;

	public static DBPasswordState from(PasswordState passwordState) {
		try {
			return DBPasswordState.valueOf(passwordState.name());
		}
		catch (Exception e) {
			log.error(DBPasswordState.class.getName() + " enum doesn't contain value for: " + passwordState);
		}
		
		return UNKNOWN;
	}
}
