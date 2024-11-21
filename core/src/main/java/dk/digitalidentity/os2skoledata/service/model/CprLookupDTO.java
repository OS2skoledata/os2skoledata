package dk.digitalidentity.os2skoledata.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CprLookupDTO {
	private String firstname;
	private String lastname;
	private String street;
	private String localname;
	private String postalCode;
	private String city;
	private String country;
	private boolean addressProtected;
	@JsonProperty(value = "isDead")
	private boolean dead;
	private boolean disenfranchised;
	private boolean doesNotExist;
	private List<ChildDTO> children;
}
