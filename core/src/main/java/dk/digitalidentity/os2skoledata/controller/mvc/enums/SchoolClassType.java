package dk.digitalidentity.os2skoledata.controller.mvc.enums;

import lombok.Getter;

@Getter
public enum SchoolClassType {
    MAIN_GROUP("Hovedgruppe"), 
    YEAR("Årgang"),
    DIRECTION("Retning"),
    UNIT("Hold"),
    SFO("SFO"),
    TEAM("Team"),
    OTHER("Andet");
	
	private String message;

    private SchoolClassType(String message) {
        this.message = message;
    }
}
