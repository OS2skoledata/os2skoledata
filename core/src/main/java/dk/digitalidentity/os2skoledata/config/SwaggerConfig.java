package dk.digitalidentity.os2skoledata.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi importApi() {
		return GroupedOpenApi.builder()
				.group("import")
				.pathsToMatch("/api/import/**")
				.build();
	}

	@Bean
	public GroupedOpenApi lookupApi() {
		return GroupedOpenApi.builder()
				.group("lookup")
				.pathsToMatch("/api/lookup/**")
				.build();
	}
}