package dk.digitalidentity.os2skoledata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Response containing CPR, UNI-Login ID and username for a person")
public class PersonLookupDTO {

	@Schema(description = "Civil registration number (CPR)", example = "1234567890")
	private String cpr;

	@Schema(description = "UNI-Login user ID", example = "10002001fd")
	private String uniId;

	@Schema(description = "Username", example = "anna1234")
	private String username;
}