package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAdministration {
	private boolean enabled = false;
	private boolean classListsOnly = false;
	private String passwordSecret;
	private boolean parentPasswordChangeEnabled = false;
	// this setting shows the dropdown special for selecting a new password
	private boolean indskolingSpecialEnabled = false;
	private boolean setIndskolingPasswordOnCreate = false;
}
