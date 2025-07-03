package com.slobodanzivanovic.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Swagger config
 *
 * @author Slobodan Zivanovic
 */
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Starter Kit API Docs")
						.description("API Docs for Starter Kit")
						.version("v0.0.1")
						.contact(new Contact()
								.name("Slobodan Zivanovic")
								.email("slobodan.zivanovic@tuta.com")
								.url("https://github.com/slobodanzivanovic")));
	}

}
