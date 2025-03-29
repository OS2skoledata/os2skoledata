package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum ClientAccessRole {
	SYNC_API_ACCESS("Adgang til API'erne"),
	PASSWORD_ACCESS("Adgang til API'erne inklusiv kodeord på brugere");

	private String message;

	private ClientAccessRole(String message) {
		this.message = message;
	}
}
