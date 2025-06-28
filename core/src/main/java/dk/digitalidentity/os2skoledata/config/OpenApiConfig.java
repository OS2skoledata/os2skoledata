package dk.digitalidentity.os2skoledata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Import API").version("v1"))
				.addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("ApiKeyAuth",
								new SecurityScheme()
										.name("ApiKey")
										.type(SecurityScheme.Type.APIKEY)
										.in(SecurityScheme.In.HEADER)
						)
				);
	}
}
