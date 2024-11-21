package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Idp {
	private User employee = new User();
	private User parent = new User();
}
