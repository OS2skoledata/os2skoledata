package dk.digitalidentity.os2skoledata.dao.model.enums;

import https.unilogin_dk.data.ImportGroupType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBImportGroupType {
	HOVEDGRUPPE, ÅRGANG, RETNING, HOLD, SFO, TEAM, ANDET, UNKNOWN;

	public static DBImportGroupType from(ImportGroupType groupType) {
		try {
			return DBImportGroupType.valueOf(groupType.name());
		}
		catch (Exception e) {
			log.error(DBImportGroupType.class.getName() + " enum doesn't contain value for: " + groupType);
		}
		
		return UNKNOWN;
	}
}
