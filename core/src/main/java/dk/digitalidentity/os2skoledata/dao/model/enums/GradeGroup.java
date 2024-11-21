package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum GradeGroup {
	YOUNGEST("Indskoling"),
	MIDDLE("Mellemtrin"),
	OLDEST("Udskoling");

	private String message;

	private GradeGroup(String message) {
		this.message = message;
	}
}
