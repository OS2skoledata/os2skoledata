package dk.digitalidentity.os2skoledata.service.model;

import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ADPasswordRequest {
	private String domain;
	private String userName;
	private String password;

	public ADPasswordRequest(String username, String password, String domain) {
		this.domain = domain;
		this.userName = username;
		this.password = password;
	}

	public ADPasswordRequest(PasswordChangeQueue change, String password, String domain) {
		this.domain = domain;
		this.userName = change.getUsername();
		this.password = password;
	}
}
