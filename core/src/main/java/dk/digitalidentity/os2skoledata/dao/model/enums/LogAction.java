package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum LogAction {
	CHANGE_PASSWORD("enum.logaction.changePassword"),
	CHANGE_PASSWORD_FAILED("enum.logaction.changePasswordFailed"),
	LOGIN_PARENT("enum.logaction.loginParent");

	private String message;

	private LogAction(String message) {
		this.message = message;
	}
}
