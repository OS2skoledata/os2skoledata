package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Elevrolle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum DBStudentRole {
	BARN("Barn"),
	ELEV("Elev"),
	STUDERENDE("Studerende"),
	UNKNOWN("Ukendt");

	private String message;
	DBStudentRole(String message) {
		this.message = message;
	}


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
