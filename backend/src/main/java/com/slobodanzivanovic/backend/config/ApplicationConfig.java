package com.slobodanzivanovic.backend.config;

import java.util.regex.Pattern;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.security.jwt.CustomUserDetails;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

	private final MessageService messageService;
	private final UserRepository userRepository;

	@Bean
	UserDetailsService userDetailsService() {
		return identifier -> {
			String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
			Pattern pattern = Pattern.compile(emailRegex);
			if (pattern.matcher(identifier).matches()) {
				return this.userRepository.findByEmail(identifier)
						.map(CustomUserDetails::new)
						.orElseThrow(() -> new UsernameNotFoundException(
								messageService.getMessage("error.username.not.found", identifier)));
			} else {
				return this.userRepository.findByUsername(identifier)
						.map(CustomUserDetails::new)
						.orElseThrow(() -> new UsernameNotFoundException(
								messageService.getMessage("error.username.not.found", identifier)));
			}
		};
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService());
		authenticationProvider.setPasswordEncoder(this.passwordEncoder());

		return authenticationProvider;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
