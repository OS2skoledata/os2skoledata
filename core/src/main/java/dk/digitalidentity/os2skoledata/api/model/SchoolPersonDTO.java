package dk.digitalidentity.os2skoledata.api.model;

import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportRole;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportSubrole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Represents a school person (employee, external or student) with metadata from various systems")
public class SchoolPersonDTO {
	public long databaseId;
	public String firstName;
	public String familyName;
	public String username;
	public String reservedUsername;
	public String cpr;
	public String institutionName;
	public String institutionNumber;
	private PossibleImportRole mainRole;
	private List<PossibleImportSubrole> roles;
	private String source;
	private String uniID;
	private List<String> groupIDs;
	private Boolean primary;
	private Boolean nameProtected;
	private String aliasFirstName;
	private String aliasFamilyName;
	public boolean deleted;
	public boolean apiOnly;
	public LocalDateTime createdInDB;
	public LocalDateTime deletedInDB;
	public LocalDateTime createdInAD;
	public LocalDateTime deletedInAD;
	public LocalDateTime createdInGW;
	public LocalDateTime deletedInGW;
	public LocalDateTime createdInAzure;
	public LocalDateTime deletedInAzure;
}
