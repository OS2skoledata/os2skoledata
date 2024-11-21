package dk.digitalidentity.os2skoledata.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentPasswordChangeDTO {
	private String name;
	private String username;
	private String uniLogin;
	private String institutions;
	private String classes;
	private boolean canChangePassword;
}
