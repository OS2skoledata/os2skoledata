package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Ansatrolle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBEmployeeRole {
	LÆRER, PÆDAGOG, VIKAR, LEDER, LEDELSE, TAP, KONSULENT, UNKNOWN;

	public static DBEmployeeRole from(Ansatrolle ansatrolle) {
		try {
			return DBEmployeeRole.valueOf(ansatrolle.name());
		}
		catch (Exception e) {
			log.error(DBEmployeeRole.class.getName() + " enum doesn't contain value for: " + ansatrolle);
		}

		return UNKNOWN;
	}
}
