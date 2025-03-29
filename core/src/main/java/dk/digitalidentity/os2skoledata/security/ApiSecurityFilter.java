package dk.digitalidentity.os2skoledata.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dk.digitalidentity.os2skoledata.dao.model.enums.ClientAccessRole;
import dk.digitalidentity.samlmodule.model.SamlGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.service.ClientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiSecurityFilter implements Filter {

	private static final String ROLE_API = "ROLE_API_";

	private ClientService clientService;

	public ApiSecurityFilter(ClientService clientService) {
		this.clientService = clientService;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String authHeader = request.getHeader("ApiKey");
		if (authHeader != null) {
			Client client = clientService.getClientByApiKey(authHeader);
			if (client == null) {
				unauthorized(response, "Invalid ApiKey header", authHeader);
				return;
			}

			if (client.isPaused()) {
				unauthorized(response, "Client is paused", authHeader);
				return;
			}

			ArrayList<GrantedAuthority> authorities = new ArrayList<>();
			switch (client.getAccessRole()) {
				case SYNC_API_ACCESS:
					authorities.add(new SamlGrantedAuthority(ROLE_API + ClientAccessRole.SYNC_API_ACCESS.toString()));
					break;
				case PASSWORD_ACCESS:
					authorities.add(new SamlGrantedAuthority(ROLE_API + ClientAccessRole.SYNC_API_ACCESS.toString()));
					authorities.add(new SamlGrantedAuthority(ROLE_API + ClientAccessRole.PASSWORD_ACCESS.toString()));
					break;
			}

			if (authorities.isEmpty()) {
				unauthorized(response, "Client is missing roles", authHeader);
			}

			ClientToken token = new ClientToken(client.getName(), client.getApiKey(), authorities);
			token.setClient(client);

			SecurityContextHolder.getContext().setAuthentication(token);
			filterChain.doFilter(servletRequest, servletResponse);
		}
		else {
			unauthorized(response, "Missing ApiKey header", authHeader);
		}
	}

	private static void unauthorized(HttpServletResponse response, String message, String authHeader) throws IOException {
		log.warn(message + " (authHeader = " + authHeader + ")");
		response.sendError(401, message);
	}

	@Override
	public void destroy() {
		;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		;
	}
}