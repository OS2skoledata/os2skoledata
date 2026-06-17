package dk.digitalidentity.os2skoledata.config;

import org.springframework.web.filter.UrlHandlerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrailingSlashConfig {

	@Bean
	public FilterRegistrationBean<UrlHandlerFilter> trailingSlashFilter() {
		// Silently rewrites /api/foo/ to /api/foo without a redirect
		UrlHandlerFilter filter = UrlHandlerFilter
				.trailingSlashHandler("/api/**")
				.wrapRequest()
				.build();

		FilterRegistrationBean<UrlHandlerFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}
}