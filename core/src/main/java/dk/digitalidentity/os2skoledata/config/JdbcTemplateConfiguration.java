package dk.digitalidentity.os2skoledata.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfiguration {

	@Bean(name = "defaultTemplate")
	public JdbcTemplate defaultTemplate(javax.sql.DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
