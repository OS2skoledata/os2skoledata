package dk.digitalidentity.os2skoledata.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import dk.digitalidentity.os2skoledata.dao.model.Client;

public class SecurityUtil {

	public static String getUser() {
		String name = null;

		if (isUserLoggedIn()) {
			name = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}

		return name;
	}

	public static Client getClient() {
		Client client = null;

		if (isClientLoggedIn()) {
			client = ((ClientToken) SecurityContextHolder.getContext().getAuthentication()).getClient();
		}

		return client;
	}

	public static List<String> getUserRoles() {
		List<String> roles = new ArrayList<>();

		if (isUserLoggedIn()) {
			for (GrantedAuthority grantedAuthority : (SecurityContextHolder.getContext().getAuthentication()).getAuthorities()) {
				roles.add(grantedAuthority.getAuthority());
			}
		}

		return roles;
	}

	public static boolean isUserLoggedIn() {
		if (isLoggedIn() && SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
			return true;
		}
		
		return false;
	}

	public static boolean isClientLoggedIn() {
		if (isLoggedIn() && SecurityContextHolder.getContext().getAuthentication() instanceof ClientToken) {
			return true;
		}
		
		return false;
	}
	
	
	public static boolean hasRole(String role) {
		boolean hasRole = false;

		if (isUserLoggedIn()) {
			for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
				if (authority.getAuthority().equals(role)) {
					hasRole = true;
				}
			}
		}

		return hasRole;
	}
	
	private static boolean isLoggedIn() {
		if (SecurityContextHolder.getContext().getAuthentication() != null/* && SecurityContextHolder.getContext().getAuthentication() instanceof ClientToken*/) {
			return true;
		}

		return false;
	}
}
