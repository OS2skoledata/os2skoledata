package dk.digitalidentity.os2skoledata.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class RequestLogger extends OncePerRequestFilter {
	private SecurityLogger logger;

	public RequestLogger(SecurityLogger logger) {
		this.logger = logger;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		boolean failed = false;
		long start = System.currentTimeMillis();

		try {
			filterChain.doFilter(request, response);
		}
		catch (Exception ex) {
			// response.getStatus() is still 200 at this point, so for logging purposes, we catch the exception here and flag failed = true
			failed = true;
			throw ex;
		}
		finally {
			StringBuilder builder = new StringBuilder();
			builder.append(request.getRequestURI());

			String queryString = request.getQueryString();
			if (queryString != null) {
				builder.append('?').append(queryString);
			}

			logger.log(getClientIp(request), request.getMethod(), builder.toString(), (failed) ? 500 : response.getStatus(), System.currentTimeMillis() - start);
		}
	}
	
	private String getClientIp(HttpServletRequest request) {
		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}

		return remoteAddr;
	}
}
