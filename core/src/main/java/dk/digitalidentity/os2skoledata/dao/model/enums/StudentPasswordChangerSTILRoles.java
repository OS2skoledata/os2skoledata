package dk.digitalidentity.os2skoledata.dao.model.enums;

import lombok.Getter;

@Getter
public enum StudentPasswordChangerSTILRoles {

    TEACHER("Lærer"),
    PEDAGOGUE("Pædagog"),
    SUBSTITUTE("Vikar"),
    LEADER("Leder"),
    MANAGEMENT("Ledelse"),
    TAP("TAP"),
    CONSULTANT("Konsulent"),
    EXTERN("Ekstern"),
    TRAINEE("Praktikant");

    private String message;

    private StudentPasswordChangerSTILRoles(String message) {
        this.message = message;
    }

    public static StudentPasswordChangerSTILRoles getFromInstitutionPersonRoleAsString(String role) {
        return switch (role) {
            case "LÆRER" -> TEACHER;
            case "PÆDAGOG" -> PEDAGOGUE;
            case "VIKAR" -> SUBSTITUTE;
            case "LEDER" -> LEADER;
            case "LEDELSE" -> MANAGEMENT;
            case "TAP" -> TAP;
            case "KONSULENT" -> CONSULTANT;
            case "PRAKTIKANT" -> TRAINEE;
            case "EKSTERN" -> EXTERN;
            default -> null;
        };
    }
}
