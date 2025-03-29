package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum ClassroomAction {
	TRANSFER("Overdrag"),
	ARCHIVE("Arkiver");

	private String message;

	private ClassroomAction(String message) {
		this.message = message;
	}
}
