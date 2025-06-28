package dk.digitalidentity.os2skoledata.dao.model.enums;

import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportSubrole;
import https.unilogin.Ansatrolle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum DBEmployeeRole {
	LÆRER("lærer"),
	PÆDAGOG("pædagog"),
	VIKAR("vikar"),
	LEDER("Leder"),
	LEDELSE("Ledelse"),
	TAP("TAP"),
	KONSULENT("Konsulent"),
	UNKNOWN("Ukendt");

	private String message;
	DBEmployeeRole(String message) {
		this.message = message;
	}

	public static DBEmployeeRole from(Ansatrolle ansatrolle) {
		try {
			return DBEmployeeRole.valueOf(ansatrolle.name());
		}
		catch (Exception e) {
			log.error(DBEmployeeRole.class.getName() + " enum doesn't contain value for: " + ansatrolle);
		}

		return UNKNOWN;
	}

	public static DBEmployeeRole from(PossibleImportSubrole importSubrole) {
		try {
			return DBEmployeeRole.valueOf(importSubrole.name());
		}
		catch (Exception e) {
			log.error(DBEmployeeRole.class.getName() + " enum doesn't contain value for PossibleImportSubrole: " + importSubrole);
		}

		return UNKNOWN;
	}
}
