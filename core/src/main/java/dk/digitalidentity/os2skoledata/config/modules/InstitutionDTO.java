package dk.digitalidentity.os2skoledata.config.modules;

import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionDTO {
	private String institutionNumber;
	private InstitutionType type;
}
