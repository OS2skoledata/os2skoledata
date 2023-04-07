package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Elevrolle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBStudentRole {
	BARN, ELEV, STUDERENDE, UNKNOWN;

	public static DBStudentRole from(Elevrolle role) {
		try {
			return DBStudentRole.valueOf(role.name());
		}
		catch (Exception e) {
			log.error(DBStudentRole.class.getName() + " enum doesn't contain value for: " + role);
		}
		
		return UNKNOWN;
	}
}
