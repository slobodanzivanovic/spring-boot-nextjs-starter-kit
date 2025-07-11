package com.slobodanzivanovic.backend.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration class for JPA auditing
 *
 * @author Slobodan Zivanovic
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated() ||
					authentication instanceof AnonymousAuthenticationToken) {
				return Optional.of("starter-on-top"); // Default value when no authenticated user (admin)...
			}

			return Optional.of(authentication.getName());
		};
	}
}
