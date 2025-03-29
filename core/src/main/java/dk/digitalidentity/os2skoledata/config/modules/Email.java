package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {
	private String from;
	private String username;
	private String password;
	private String host;
}
