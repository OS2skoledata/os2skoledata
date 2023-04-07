package dk.digitalidentity.os2skoledata.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class ClientToken extends UsernamePasswordAuthenticationToken {

	@Getter
	@Setter
	private Client client;

	public ClientToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}