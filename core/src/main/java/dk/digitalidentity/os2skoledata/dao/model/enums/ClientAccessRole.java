package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum ClientAccessRole {
	SYNC_API_ACCESS("Adgang til API'erne bortset fra import API'et"),
	PASSWORD_ACCESS("Adgang til API'erne inklusiv kodeord på brugere bortset fra import API'et"),
	IMPORT_ACCESS("Adgang udelukkende til import API'et"),
	LOOKUP_ACCESS("Adgang udelukkende til lookup API'et");

	private String message;

	private ClientAccessRole(String message) {
		this.message = message;
	}
}
