package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum EntityType {
	INSTITUTION("Institution"),
	INSTITUTION_PERSON("Person"),
	GROUP("Gruppe");

	private String message;

	private EntityType(String message) {
		this.message = message;
	}
}
