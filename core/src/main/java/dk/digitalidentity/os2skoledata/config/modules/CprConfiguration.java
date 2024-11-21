package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CprConfiguration {
	private boolean enabled = true;
	private String url = "http://cprservice5.digital-identity.dk";
}
