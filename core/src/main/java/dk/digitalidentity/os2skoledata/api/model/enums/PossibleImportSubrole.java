package dk.digitalidentity.os2skoledata.api.model.enums;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PossibleImportSubrole {
	LÆRER, PÆDAGOG, VIKAR, LEDER, LEDELSE, TAP, KONSULENT, PRAKTIKANT, EKSTERN, ELEV, BARN, STUDERENDE;

	public static PossibleImportSubrole from(DBEmployeeRole dbEmployeeRole) {
		try {
			return PossibleImportSubrole.valueOf(dbEmployeeRole.name());
		}
		catch (Exception e) {
			log.error(PossibleImportSubrole.class.getName() + " enum doesn't contain value for DBEmployeeRole: " + dbEmployeeRole);
		}

		return null;
	}

	public static PossibleImportSubrole from(DBExternalRoleType role) {
		try {
			return PossibleImportSubrole.valueOf(role.name());
		}
		catch (Exception e) {
			log.error(PossibleImportSubrole.class.getName() + " enum doesn't contain value for DBExternalRoleType: " + role);
		}

		return null;
	}

	public static PossibleImportSubrole from(DBStudentRole role) {
		try {
			return PossibleImportSubrole.valueOf(role.name());
		}
		catch (Exception e) {
			log.error(PossibleImportSubrole.class.getName() + " enum doesn't contain value for DBStudentRole: " + role);
		}

		return null;
	}
}
