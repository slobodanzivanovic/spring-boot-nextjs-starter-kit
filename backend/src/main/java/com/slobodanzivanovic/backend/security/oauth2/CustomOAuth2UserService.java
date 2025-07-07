package com.slobodanzivanovic.backend.security.oauth2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.slobodanzivanovic.backend.exception.OAuthProcessingException;
import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.repository.auth.RoleRepository;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private static final String CLASS_NAME = CustomOAuth2UserService.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final MessageService messageService;
	private final RestTemplate restTemplate;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		LOGGER.info("OAuth2 authentication request received from provider: {}",
				userRequest.getClientRegistration().getRegistrationId());

		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		LOGGER.debug("Processing OAuth2 authentication for provider: {}", registrationId);

		Map<String, Object> attributes = oAuth2User.getAttributes();
		LOGGER.debug("Received OAuth2 attributes with keys: {}", attributes.keySet());

		OAuth2UserInfo userInfo;
		try {
			if (registrationId.equalsIgnoreCase("github")) {
				LOGGER.debug("GitHub OAuth2 authentication - checking if email is available (not private)");
				userInfo = getGitHubUserInfo(userRequest, attributes);
			} else {
				userInfo = getOAuth2UserInfo(registrationId, attributes);
			}
			LOGGER.debug("OAuth2 user info extracted. Provider: {}, Id: {}, Email: {}",
					registrationId, userInfo.getId(), userInfo.getEmail());
		} catch (Exception e) {
			LOGGER.error("Failed to extract OAuth2 user info from provider {}: {}",
					registrationId, e.getMessage(), e);
			throw new OAuthProcessingException(
					messageService.getMessage("error.oauth.processing", e.getMessage()), e);
		}

		if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
			LOGGER.error("No email found for OAuth2 user. Provider: {}, Name: {}",
					registrationId, userInfo.getName());
			throw new OAuthProcessingException(
					messageService.getMessage("error.oauth.email.required"));
		}

		// we here find or create new user...
		UserEntity user = processOAuthUser(userInfo, registrationId);
		LOGGER.info("OAuth2 authentication successful for user: {}, Provider: {}",
				user.getEmail(), registrationId);

		return new CustomOAuth2User(oAuth2User, user.getId(), user.getEmail(), registrationId);
	}

	private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		LOGGER.debug("Creating OAuth2UserInfo for provider: {}", registrationId);
		if (registrationId.equalsIgnoreCase("google")) {
			return new GoogleOAuth2UserInfo(attributes);
		} else if (registrationId.equalsIgnoreCase("github")) {
			return new GithubOAuth2UserInfo(attributes);
		} else {
			LOGGER.warn("Unsupported OAuth2 provider: {}", registrationId);
			throw new OAuthProcessingException(
					messageService.getMessage("error.oauth.provider.unsupported", registrationId));
		}
	}

	/**
	 * Special handling for GitHub users to fetch private emails
	 * <a href=
	 * "https://stackoverflow.com/questions/75847321/how-to-implement-social-login-with-github-when-user-email-is-private">reference</a>
	 */
	private OAuth2UserInfo getGitHubUserInfo(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
		GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(attributes);

		// Check if GitHub didnt provide the email (which happens if its private)
		if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
			LOGGER.debug("No email provided in GitHub OAuth2 attributes, attempting to fetch private email");
			// Make an additional request to get the private emails
			String token = userRequest.getAccessToken().getTokenValue();
			String gitHubEmail = fetchGitHubEmail(token);

			if (gitHubEmail != null && !gitHubEmail.isEmpty()) {
				LOGGER.debug("Successfully retrieved private email from GitHub: {}", gitHubEmail);
				// Create a new mutable map with all the original attributes plus the email
				Map<String, Object> mutableAttributes = new HashMap<>(attributes);
				mutableAttributes.put("email", gitHubEmail);

				// Return a new instance with the updated attributes
				return new GithubOAuth2UserInfo(mutableAttributes);
			} else {
				LOGGER.warn("Could not retrieve GitHub email from user emails API");
				throw new OAuthProcessingException(
						messageService.getMessage("error.oauth.github.email"));
			}
		}

		return userInfo;
	}

	/**
	 * Fetch GitHub users email using the access token
	 */
	private String fetchGitHubEmail(String accessToken) {
		LOGGER.debug("Fetching GitHub user emails using access token");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "token " + accessToken);
			headers.set("Accept", "application/json");

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
					"https://api.github.com/user/emails",
					HttpMethod.GET,
					entity,
					new ParameterizedTypeReference<>() {
					});

			List<Map<String, Object>> emails = response.getBody();
			LOGGER.debug("GitHub API returned {} email entries", emails != null ? emails.size() : 0);

			if (emails != null && !emails.isEmpty()) {
				// Find the primary email
				for (Map<String, Object> emailObj : emails) {
					Boolean isPrimary = (Boolean) emailObj.get("primary");
					Boolean isVerified = (Boolean) emailObj.get("verified");
					String email = (String) emailObj.get("email");

					LOGGER.debug("Processing GitHub email: {}, Primary: {}, Verified: {}",
							email, isPrimary, isVerified);

					if (Boolean.TRUE.equals(isPrimary) && Boolean.TRUE.equals(isVerified)) {
						LOGGER.debug("Selected primary verified GitHub email: {}", email);
						return email;
					}
				}

				// If no primary email found, use the first verified email
				for (Map<String, Object> emailObj : emails) {
					Boolean isVerified = (Boolean) emailObj.get("verified");
					String email = (String) emailObj.get("email");

					if (Boolean.TRUE.equals(isVerified)) {
						LOGGER.debug("Selected first verified GitHub email: {}", email);
						return email;
					}
				}

				// If still no email found, use the first one
				if (!emails.isEmpty()) {
					String firstEmail = (String) emails.getFirst().get("email");
					LOGGER.debug("Using first available GitHub email (not verified): {}", firstEmail);
					return firstEmail;
				}
			}

			LOGGER.warn("No email found in GitHub user emails API response");
			return null;

		} catch (Exception e) {
			LOGGER.error("Error fetching GitHub user emails: {}", e.getMessage(), e);
			return null;
		}
	}

	@Transactional
	public UserEntity processOAuthUser(OAuth2UserInfo userInfo, String provider) {
		LOGGER.debug("{}.processOAuthUser({}, {})", CLASS_NAME, userInfo.getEmail(), provider);

		if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
			LOGGER.error("Email is required for OAuth2 authentication. Provider: {}", provider);
			throw new OAuthProcessingException(
					messageService.getMessage("error.oauth.email.required"));
		}

		Optional<UserEntity> existingUser = userRepository.findByEmail(userInfo.getEmail());

		if (existingUser.isPresent()) {
			LOGGER.debug("User already exists with email: {}", userInfo.getEmail());
			UserEntity user = existingUser.get();

			boolean userUpdated = false;

			// Only set profile image if user doesnt have one and OAuth2 provider supplies
			// one
			if ((user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty())
					&& userInfo.getImageUrl() != null && !userInfo.getImageUrl().isEmpty()) {
				LOGGER.info("Setting profile image from OAuth2 provider for existing user: {}", user.getEmail());
				user.setProfileImageUrl(userInfo.getImageUrl());
				userUpdated = true;
			}

			// Check if account is locked but lock period has expired
			// like in classic login
			if (!user.isAccountNonLocked() && user.canUnlockAccount()) {
				LOGGER.info("Account was locked but lock period expired. Unlocking account for: {}", user.getEmail());
				// Reset lock status and failed attempts
				user.setAccountNonLocked(true);
				user.setAccountLockedUntil(null);
				user.resetFailedLoginAttempts();
				userUpdated = true;
			} else if (!user.isAccountNonLocked()) {
				LOGGER.warn("Attempt to login via OAuth2 to locked account: {}", user.getEmail());
				throw new OAuthProcessingException(messageService.getMessage("error.login.account_locked"));
			}

			// Always enable the account when logging in with OAuth2 we dont send email for
			// confirm
			if (!user.isEnabled()) {
				LOGGER.info("Enabling previously disabled account through OAuth2 login: {}", user.getEmail());
				user.setEnabled(true);
				userUpdated = true;
			}

			if (userUpdated) {
				user = userRepository.save(user);
				LOGGER.debug("Updated user information during OAuth2 login: {}", user.getEmail());
			}

			return user;
		} else {
			// Create new user
			LOGGER.info("Creating new user account via OAuth2 ({}) registration: {}",
					provider, userInfo.getEmail());
			try {
				UserEntity user = UserEntity.builder()
						.email(userInfo.getEmail())
						.username(generateUsername(userInfo.getName(), userInfo.getEmail()))
						.firstName(userInfo.getFirstName())
						.lastName(userInfo.getLastName())
						.password(passwordEncoder.encode(UUID.randomUUID().toString()))
						.enabled(true)
						.createdBy("OAUTH_" + provider.toUpperCase())
						.build();

				// Set profile image from OAuth2 provider if available... I mean there will be
				// always some image at least default one
				if (userInfo.getImageUrl() != null && !userInfo.getImageUrl().isEmpty()) {
					LOGGER.info("Setting profile image from OAuth2 provider for new user: {}", user.getEmail());
					user.setProfileImageUrl(userInfo.getImageUrl());
				}

				// Add default role
				RoleEntity userRole = roleRepository.findByName("ROLE_USER")
						.orElseThrow(() -> {
							LOGGER.error("Default user role 'ROLE_USER' not found in database");
							return new OAuthProcessingException(
									messageService.getMessage("error.user.role"));
						});
				user.addRole(userRole);

				UserEntity savedUser = userRepository.save(user);
				LOGGER.info("Successfully created new user via OAuth2: Username={}, Email={}, Provider={}",
						savedUser.getUsername(), savedUser.getEmail(), provider);

				return savedUser;
			} catch (Exception e) {
				LOGGER.error("Failed to create user via OAuth2: {}", e.getMessage(), e);
				throw new OAuthProcessingException(
						messageService.getMessage("error.oauth.user.creation", e.getMessage()), e);
			}
		}
	}

	private String generateUsername(String name, String email) {
		LOGGER.debug("Generating username from name: '{}' and email: '{}'", name, email);

		String baseUsername;

		if (name != null && !name.isEmpty()) {
			// Convert to lowercase, remove spaces, and replace non-ASCII characters
			baseUsername = name.toLowerCase()
					.replaceAll("\\s+", "")
					.replaceAll("[^a-zA-Z0-9]", ""); // Remove non-alphanumeric characters
		} else {
			baseUsername = email.split("@")[0]
					.replaceAll("[^a-zA-Z0-9]", ""); // Remove non-alphanumeric characters
		}

		// If after sanitization the username is empty or too short, use part of theemail
		if (baseUsername.length() < 3) {
			baseUsername = email.split("@")[0]
					.replaceAll("[^a-zA-Z0-9]", "");
		}

		// Ensure the username is at least 3 characters
		if (baseUsername.length() < 3) {
			baseUsername = "user" + Math.abs(email.hashCode() % 1000);
		}

		// Make sure username is unique
		String username = baseUsername;
		int count = 1;

		while (userRepository.findByUsername(username).isPresent()) {
			username = baseUsername + count++;
		}

		LOGGER.debug("Final generated username: {}", username);
		return username;
	}

}
