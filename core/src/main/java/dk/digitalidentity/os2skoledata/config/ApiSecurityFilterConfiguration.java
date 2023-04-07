package dk.digitalidentity.os2skoledata.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dk.digitalidentity.os2skoledata.security.ApiSecurityFilter;
import dk.digitalidentity.os2skoledata.service.ClientService;

@Configuration
public class ApiSecurityFilterConfiguration {

	@Autowired
	private ClientService clientService;
	
	@Bean(name="ApiSecurityFilter")
	public FilterRegistrationBean<ApiSecurityFilter> apiSecurityFilter() {
		ApiSecurityFilter filter = new ApiSecurityFilter(clientService);

		FilterRegistrationBean<ApiSecurityFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
		filterRegistrationBean.addUrlPatterns("/api/*");
		filterRegistrationBean.setOrder(100);
		
		return filterRegistrationBean;
	}
}
