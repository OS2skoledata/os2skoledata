package dk.digitalidentity.os2skoledata.controller.mvc.enums;

import lombok.Getter;

@Getter
public enum SchoolYear {
    ZERO("0. klasse", "0"),
    ONE("1. klasse", "1"),
    TWO("2. klasse", "2"),
    THREE("3. klasse", "3"),
    FOUR("4. klasse", "4"),
    FIVE("5. klasse", "5"),
    SIX("6. klasse", "6"),
    SEVEN("7. klasse", "7"),
    EIGHT("8. klasse", "8"),
    NINE("9. klasse", "9"),
    TEN("10. klasse", "10");

	private String message;
	private String level;

    private SchoolYear(String message, String level) {
        this.message = message;
        this.level = level;
    }
}
