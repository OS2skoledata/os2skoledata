package dk.digitalidentity.os2skoledata.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContactCardDTO {
	private String institutionGWId;
	private String institutionName;
	private String institutionRoles;
	private String institutionGroups;
}
