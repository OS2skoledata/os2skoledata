package dk.digitalidentity.os2skoledata.api.model;

import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportRole;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportSubrole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPersonImportDTO {

	// required fields
	@NotBlank(message = "firstName required")
	private String firstName;
	@NotBlank(message = "familyName required")
	private String familyName;
	@NotBlank(message = "cpr required")
	private String cpr;
	@NotBlank(message = "institutionNumber required")
	private String institutionNumber;
	private PossibleImportRole mainRole;
	@NotEmpty(message = "Must contain at least one role")
	private List<PossibleImportSubrole> roles;
	@NotBlank(message = "source required")
	private String source;

	// optional fields
	private String uniID;
	private List<String> groupIDs;
	private Boolean primary;
	private String reservedUsername;
	private Boolean nameProtected;
	private String aliasFirstName;
	private String aliasFamilyName;
	private Boolean ApiOnly;
}
