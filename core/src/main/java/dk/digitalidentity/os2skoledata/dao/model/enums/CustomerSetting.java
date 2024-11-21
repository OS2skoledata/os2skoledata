package dk.digitalidentity.os2skoledata.dao.model.enums;
import lombok.Getter;

@Getter
public enum CustomerSetting {
	LAST_READ_REVISION("0"),
	IMPORT_SOURCE_SCHOOL_YEAR_(null),
	CURRENT_SCHOOL_YEAR_(null),
	LOCKED_INSTITUTION_("false"),
	GLOBAL_SCHOOL_YEAR(null),
	REQUEST_LOG_("false");

	private String defaultValue;

	private CustomerSetting(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
