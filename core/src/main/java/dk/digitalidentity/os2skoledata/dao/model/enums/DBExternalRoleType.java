package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Eksternrolle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBExternalRoleType {
	PRAKTIKANT, EKSTERN, UNKNOWN;

	public static DBExternalRoleType from(Eksternrolle role) {
		try {
			return DBExternalRoleType.valueOf(role.name());
		}
		catch (Exception e) {
			log.error(DBExternalRoleType.class.getName() + " enum doesn't contain value for: " + role);
		}
		
		return UNKNOWN;
	}
}
