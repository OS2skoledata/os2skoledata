package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Eksternrolle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum DBExternalRoleType {
	PRAKTIKANT("Praktikant"),
	EKSTERN("Ekstern"),
	UNKNOWN("Ukendt");

	private String message;
	DBExternalRoleType(String message) {
		this.message = message;
	}

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
