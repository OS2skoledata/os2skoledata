package dk.digitalidentity.os2skoledata.security;

import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.samlmodule.model.TokenUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

	public static String getUserId() {
		TokenUser tokenUser = getTokenUser();
		if (tokenUser == null) {
			return null;
		}

		return tokenUser.getUsername();
	}

	public static TokenUser getTokenUser() {
		if (!isAuthenticated()) {
			return null;
		}

		return (TokenUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	public static Client getClient() {
		Client client = null;

		if (isClientLoggedIn()) {
			client = ((ClientToken) SecurityContextHolder.getContext().getAuthentication()).getClient();
		}

		return client;
	}

	public static boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().getDetails() != null &&
				SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof TokenUser;
	}

	public static boolean hasRole(String role) {
		boolean hasRole = false;
		if (isLoggedIn()) {
			for (GrantedAuthority grantedAuthority : (SecurityContextHolder.getContext().getAuthentication()).getAuthorities()) {
				if (grantedAuthority.getAuthority().equals(role)) {
					hasRole = true;
				}
			}
		}
		return hasRole;
	}

	public static boolean isClientLoggedIn() {
		if (isLoggedIn() && SecurityContextHolder.getContext().getAuthentication() instanceof ClientToken) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isLoggedIn() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return true;
		}

		return false;
	}

	public String getCpr() {
		if (!isAuthenticated()) {
			return null;
		}

		return (String) getTokenUser().getAttributes().getOrDefault(Constants.CPR_ATTRIBUTE_KEY, null);
	}
}
