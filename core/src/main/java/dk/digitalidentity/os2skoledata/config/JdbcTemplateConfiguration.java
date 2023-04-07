package dk.digitalidentity.os2skoledata.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfiguration {

	@Bean(name = "defaultTemplate")
	public JdbcTemplate defaultTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
