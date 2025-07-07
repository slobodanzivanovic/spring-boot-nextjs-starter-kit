package com.slobodanzivanovic.backend.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.slobodanzivanovic.backend.security.jwt.JwtAuthenticationFilter;
import com.slobodanzivanovic.backend.security.oauth2.CustomOAuth2UserService;
import com.slobodanzivanovic.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import lombok.RequiredArgsConstructor;

/**
 * Conf class for Spring security settings
 *
 * @author Slobodan Zivanovic
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String CLASS_NAME = SecurityConfig.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authenticationProvider;

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> {
			authorize
					.requestMatchers("/api/v1/auth/**").permitAll()
					.requestMatchers("/login/oauth2/code/**").permitAll()

					// Swagger UI endpoints
					.requestMatchers("/api-docs/**").permitAll()
					.requestMatchers("/swagger-ui/**").permitAll()
					.requestMatchers("/swagger-ui.html").permitAll()

					// exzample
					.requestMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN")
					.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

					.anyRequest().authenticated();
		});

		http.cors(cors -> {
			cors.configurationSource(corsConfigurationSource());
		});

		http.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		http.csrf(AbstractHttpConfigurer::disable); // TODO: just for now

		http.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
						.baseUri("/api/v1/auth/oauth2/authorization"))
				.redirectionEndpoint(redirection -> redirection
						.baseUri("/login/oauth2/code/*"))
				.userInfoEndpoint(userInfo -> userInfo
						.userService(customOAuth2UserService))
				.successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler((request, response, exception) -> {
					LOGGER.error("OAuth authentication failed: {}", exception.getMessage());
					response.sendError(401,
							messageService.getMessage("error.oauth.authentication.failed", exception.getMessage()));
				}))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowCredentials(true);

		// TODO: GET THIS FROM APP PROPERTIES FILE...
		configuration.setAllowedOrigins(List.of(
				"http://localhost:8080",
				"http://localhost:3000"));

		configuration.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		configuration.setAllowedHeaders(List.of(
				"Authorization",
				"Content-Type",
				"X-Requested-With",
				"X-Forwarded-For",
				"X-Forwarded-Proto",
				"X-Forwarded-Host",
				"X-Forwarded-Port",
				"X-Forwarded-Prefix"));

		configuration.setExposedHeaders(List.of("Authorization"));

		configuration.setAllowCredentials(true);

		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
