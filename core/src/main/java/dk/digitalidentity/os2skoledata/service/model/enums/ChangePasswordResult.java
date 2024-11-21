package dk.digitalidentity.os2skoledata.service.model.enums;

import lombok.Getter;

@Getter
public enum ChangePasswordResult {

	// all is well, password changes
	OK(""),

	// technical errors
	TECHNICAL_MISSING_PERSON("Kan ikke finde en brugerkonto at skifte kodeord på"),
	TECHNICAL_NO_LEVEL("Kan ikke finde en årgang tilknyttet til eleven"),

	// validation errors
	TOO_SHORT("Det angivne kodeord er for kort"),
	NOT_COMPLEX("Det angivne kodeord opfylder ikke reglerne for et komplekst kodeord");

	private String message;

	private ChangePasswordResult(String message) {
		this.message = message;
	}
}
