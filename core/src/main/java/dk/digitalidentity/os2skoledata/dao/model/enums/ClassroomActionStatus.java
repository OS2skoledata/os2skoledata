package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum ClassroomActionStatus {
	WAITING("Afventer"),
	DONE("Udført"),
	FAILED("Fejlet");

	private String message;

	private ClassroomActionStatus(String message) {
		this.message = message;
	}
}
