package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin.Kontaktpersonsrelation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBRelationType {
	MOR, FAR, ANDET, OFFICIELT_TILKNYTTET_PERSON, UNKNOWN;

	public static DBRelationType from(Kontaktpersonsrelation relation) {
		try {
			return DBRelationType.valueOf(relation.name());
		}
		catch (Exception e) {
			log.error(DBRelationType.class.getName() + " enum doesn't contain value for: " + relation);
		}
		
		return UNKNOWN;
	}
}
