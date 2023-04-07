package dk.digitalidentity.os2skoledata.service.model;

import lombok.Getter;

@Getter

public enum EventType {
	CREATE("Opret"),
	UPDATE("Opdater"),
	DELETE("Nedlæg");

	private String message;

	private EventType(String message) {
		this.message = message;
	}
}