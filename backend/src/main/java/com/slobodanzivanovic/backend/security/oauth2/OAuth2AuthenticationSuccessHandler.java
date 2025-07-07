package com.slobodanzivanovic.backend.security.oauth2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.security.jwt.CustomUserDetails;
import com.slobodanzivanovic.backend.security.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final String CLASS_NAME = OAuth2AuthenticationSuccessHandler.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Value("${app.login.success.url}")
	private String loginSuccessUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		LOGGER.info("OAuth2 authentication successful, processing redirect with JWT");

		if (response.isCommitted()) {
			LOGGER.warn("Response has already been committed. Unable to redirect.");
			return;
		}

		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		LOGGER.debug("OAuth2 user authenticated: Email={}, Provider={}, UserId={}",
				oAuth2User.getEmail(), oAuth2User.getProvider(), oAuth2User.getUserId());

		String targetUrl = determineTargetUrl(request, oAuth2User);

		updateUserLogin(oAuth2User, request.getRemoteAddr());

		clearAuthenticationAttributes(request);
		LOGGER.info("Redirecting authenticated OAuth2 user to frontend with JWT token");
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, CustomOAuth2User oAuth2User) {
		LOGGER.debug("{}.determineTargetUrl() for user: {}", CLASS_NAME, oAuth2User.getEmail());

		Optional<String> redirectUri = Optional.ofNullable(request.getParameter("redirect_uri"));
		LOGGER.debug("Requested redirect URI: {}", redirectUri.orElse("None specified"));

		String targetUrl = redirectUri.orElse(loginSuccessUrl);
		LOGGER.debug("Using target URL: {}", targetUrl);

		// Generate JWT token
		Optional<UserEntity> userOptional = userRepository.findById(oAuth2User.getUserId());
		if (userOptional.isEmpty()) {
			LOGGER.error("User not found after OAuth2 authentication: {}", oAuth2User.getEmail());
			return UriComponentsBuilder.fromUriString(targetUrl)
					.queryParam("error", "user_not_found")
					.build().toUriString();
		}

		UserEntity user = userOptional.get();
		LOGGER.debug("Generating JWT token for OAuth2 user: {}", user.getUsername());

		CustomUserDetails userDetails = new CustomUserDetails(user);
		String token = jwtService.generateToken(userDetails);
		long tokenExpirationTime = jwtService.extractClaim(token,
				claims -> claims.getExpiration().getTime() - System.currentTimeMillis());

		LOGGER.debug("JWT token generated, expiration time: {} ms", tokenExpirationTime);

		return UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("token", token)
				.queryParam("expiresIn", tokenExpirationTime)
				.build().toUriString();
	}

	private void updateUserLogin(CustomOAuth2User oAuth2User, String remoteAddr) {
		userRepository.findById(oAuth2User.getUserId()).ifPresent(user -> {
			user.setLastLoginAt(LocalDateTime.now());
			user.setLastLoginIp(remoteAddr);
			userRepository.save(user);
		});
	}

}
