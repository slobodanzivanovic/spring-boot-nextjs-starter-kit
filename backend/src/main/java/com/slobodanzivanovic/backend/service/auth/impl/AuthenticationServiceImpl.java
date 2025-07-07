package com.slobodanzivanovic.backend.service.auth.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.slobodanzivanovic.backend.exception.AuthenticationException;
import com.slobodanzivanovic.backend.exception.BusinessException;
import com.slobodanzivanovic.backend.exception.ExternalServiceException;
import com.slobodanzivanovic.backend.exception.ResourceAlreadyExistsException;
import com.slobodanzivanovic.backend.exception.ResourceNotFoundException;
import com.slobodanzivanovic.backend.exception.TokenException;
import com.slobodanzivanovic.backend.exception.ValidationException;
import com.slobodanzivanovic.backend.model.auth.dto.request.LoginRequest;
import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.dto.response.LoginResponse;
import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.auth.mapper.RequestMapper;
import com.slobodanzivanovic.backend.repository.auth.RoleRepository;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.security.jwt.CustomUserDetails;
import com.slobodanzivanovic.backend.security.jwt.JwtService;
import com.slobodanzivanovic.backend.security.jwt.TokenBlacklistService;
import com.slobodanzivanovic.backend.service.auth.AuthenticationService;
import com.slobodanzivanovic.backend.service.email.EmailService;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Slobodan Zivanovic
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final String CLASS_NAME = AuthenticationServiceImpl.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RequestMapper requestMapper;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final TokenBlacklistService tokenBlacklistService;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthenticationServiceImpl(
			MessageService messageService,
			UserRepository userRepository,
			RoleRepository roleRepository,
			RequestMapper requestMapper,
			EmailService emailService,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			TokenBlacklistService tokenBlacklistService,
			BCryptPasswordEncoder passwordEncoder) {
		this.messageService = messageService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.requestMapper = requestMapper;
		this.emailService = emailService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.tokenBlacklistService = tokenBlacklistService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void register(RegisterRequest registerRequest) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.register({})", CLASS_NAME, registerRequest);
		}

		if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
			throw new ResourceAlreadyExistsException(messageService.getMessage("error.user.exists"));
		}

		if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
			throw new ResourceAlreadyExistsException(messageService.getMessage("error.user.exists"));
		}

		// TODO: validate here (myb some validation service)
		UserEntity newUser = requestMapper.map(registerRequest);

		RoleEntity userRole = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("error.user.role")));
		newUser.addRole(userRole);

		String verificationCode = generateAlphanumericCode();
		newUser.setVerificationCode(verificationCode);
		newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
		newUser.setCreatedBy(registerRequest.username());

		UserEntity savedUser = userRepository.save(newUser);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					sendVerificationEmail(savedUser);
					LOGGER.info("User registered successfully: {}", savedUser.getUsername());
				} catch (Exception e) {
					LOGGER.error("Failed to send verification email: {}", e.getMessage(), e);
					// NOTE: consider retry mechanism...
				}
			}
		});
	}

	@Override
	public void verifyUser(String email, String verificationCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.verifyUser({})", CLASS_NAME, email);
		}

		UserEntity user = getUserByEmailOrThrow(email);

		if (user.isEnabled()) {
			throw new BusinessException(messageService.getMessage("error.user.already_verified"));
		}

		if (user.isVerificationCodeExpired()) {
			throw new BusinessException(messageService.getMessage("error.user.verification_expired"));
		}

		if (!user.getVerificationCode().equals(verificationCode)) {
			throw new ValidationException(messageService.getMessage("error.user.verification_code"));
		}

		user.setEnabled(true);
		user.setVerificationCode(null);
		user.setVerificationCodeExpiresAt(null);
		userRepository.save(user);

		LOGGER.info("User account verified: {}", email);
	}

	@Override
	@Transactional
	public void resendVerificationCode(String email) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.resendVerificationCode({})", CLASS_NAME, email);
		}

		UserEntity user = getUserByEmailOrThrow(email);

		if (user.isEnabled()) {
			throw new BusinessException(messageService.getMessage("error.user.already_verified"));
		}

		// TODO: add some way of preventing spam
		String verificationCode = generateAlphanumericCode();
		user.setVerificationCode(verificationCode);
		user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));

		userRepository.save(user);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					sendVerificationEmail(user);
					LOGGER.info("Verification code resent to: {}", email);
				} catch (Exception e) {
					LOGGER.error("Failed to send verification email: {}", e.getMessage(), e);
					// here tho a retry mechanism
				}
			}
		});
	}

	// TODO: ADD CHECK IF ACCOUNT IS PREVIOUSLY DELETED account.disabled it too generic
	@Override
	@Transactional(noRollbackFor = AuthenticationException.class)
	public LoginResponse login(LoginRequest loginRequest) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.login({})", CLASS_NAME, loginRequest);
		}

		if (loginRequest == null || loginRequest.identifier() == null || loginRequest.password() == null) {
			throw new ValidationException(messageService.getMessage("error.login.null"));
		}

		UserEntity user = userRepository.findByUsername(loginRequest.identifier())
				.or(() -> userRepository.findByEmail(loginRequest.identifier()))
				.orElseThrow(() -> new AuthenticationException(messageService.getMessage("error.login.invalid")));

		if (!user.isAccountNonLocked()) {
			if (user.canUnlockAccount()) {
				user.setAccountNonLocked(true);
				user.setAccountLockedUntil(null);
				user.resetFailedLoginAttempts();
				userRepository.save(user);
			} else {
				throw new AuthenticationException(messageService.getMessage("error.login.account_locked"));
			}
		}

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password()));

			user.resetFailedLoginAttempts();
			String clientIp = getCurrentClientIp();
			user.recordSuccessfulLogin(clientIp);
			userRepository.save(user);

			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			if (!userDetails.isEnabled()) {
				throw new AuthenticationException(messageService.getMessage("error.login.disabled"));
			}

			String token = jwtService.generateToken(userDetails);
			long tokenExpirationTime = jwtService.extractClaim(token,
					claims -> claims.getExpiration().getTime() - System.currentTimeMillis());

			LOGGER.info("User logged in successfully: {}", userDetails.getUsername());

			return new LoginResponse(token, tokenExpirationTime);

		} catch (BadCredentialsException e) {
			boolean wasLocked = user.incrementFailedLoginAttempts(5, 30);
			userRepository.save(user);

			if (wasLocked) {
				LOGGER.warn("Account locked due to multiple failed login attempts: {}", loginRequest.identifier());
			}

			LOGGER.warn("Authentication failed for user {}: {}", loginRequest.identifier(), e.getMessage());
			throw new AuthenticationException(messageService.getMessage("error.login.invalid"));
		} catch (DisabledException e) {
			LOGGER.warn("Attempted login to disabled account: {}", loginRequest.identifier());
			throw new AuthenticationException(messageService.getMessage("error.login.disabled"));
		} catch (Exception e) {
			LOGGER.error("Authentication error: {}", e.getMessage(), e);
			throw new AuthenticationException(messageService.getMessage("error.login.invalid"));
		}

	}

	@Override
	public void logout(String token) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.logout({})", CLASS_NAME, token);
		}

		if (token == null || token.isEmpty()) {
			throw new TokenException(messageService.getMessage("error.token.null"));
		}

		tokenBlacklistService.blacklistToken(token);
		SecurityContextHolder.clearContext();
		LOGGER.info("User logged out and token blacklisted: {}", token);
	}

	@Override
	@Transactional
	public void requestPasswordReset(String email) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.requestPasswordReset({})", CLASS_NAME, email);
		}

		UserEntity user = getUserByEmailOrThrow(email);

		String passwordResetToken = generateAlphanumericCode();
		user.setPasswordResetToken(passwordResetToken);
		user.setPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(15));

		userRepository.save(user);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					sendPasswordResetEmail(user);
				} catch (Exception e) {
					LOGGER.error("Failed to send password reset email: {}", e.getMessage(), e);
					// retry mechanism?
				}
			}
		});
	}

	@Override
	@Transactional
	public void resetPassword(String email, String passwordResetToken, String newPassword) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.resetPassword({}, {})", CLASS_NAME, email, passwordResetToken);
		}

		UserEntity user = getUserByEmailOrThrow(email);

		// TOOD: add check if password is equal also on backend

		if (user.isPasswordResetTokenExpired()) {
			throw new BusinessException(messageService.getMessage("error.user.reset_token_expired"));
		}

		if (!user.getPasswordResetToken().equals(passwordResetToken)) {
			throw new ValidationException(messageService.getMessage("error.user.reset_token"));
		}

		if (newPassword.length() < 8) {
			throw new ValidationException(messageService.getMessage("error.user.password.length"));
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setPasswordChangedAt(LocalDateTime.now());
		user.setPasswordResetToken(null);
		user.setPasswordResetExpiresAt(null);
		userRepository.save(user);

		// NOTE: This will just log for now until we add redis
		tokenBlacklistService.blacklistAllUserTokens(user.getId());

		LOGGER.info("User password reset successfully for: {}", email);
	}

	/**
	 * Scheduled task to clean up expired verification codes
	 * Runs periodically to find and clear expired verification codes
	 * from user accounts that have not been verified.
	 */
	@Scheduled(cron = "0 0 * * * ?") // NOTE: maybe make it run every day or 3,4 hours...
	@Transactional
	public void cleanupExpiredVerificationCodes() {
		LOGGER.info("Cleaning up expired verification codes");
		LocalDateTime now = LocalDateTime.now();

		List<UserEntity> usersWithExpiredCodes = userRepository
				.findByVerificationCodeExpiresAtBeforeAndEnabledFalse(now);

		for (UserEntity user : usersWithExpiredCodes) {
			LOGGER.debug("Clearing expired verification code for user: {}", user.getEmail());
			user.setVerificationCode(null);
			user.setVerificationCodeExpiresAt(null);
		}

		if (!usersWithExpiredCodes.isEmpty()) {
			userRepository.saveAll(usersWithExpiredCodes);
			LOGGER.info("Cleaned up {} expired verification codes", usersWithExpiredCodes.size());
		}
	}

	/**
	 * Generates random alphanumeric verification code. Excluding (O, 0, 1, I)
	 *
	 * @return Random alphanumeric code as a string
	 */
	private String generateAlphanumericCode() {
		SecureRandom random = new SecureRandom();
		String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}

		return sb.toString();
	}

	/**
	 * Sends an acc verification email to a user
	 *
	 * @param user The user to send the verification email to
	 * @throws ExternalServiceException if sending the email fails
	 */
	private void sendVerificationEmail(UserEntity user) {
		String subject = messageService.getMessage("email.verification.subject");
		try {
			Map<String, Object> templateModel = new HashMap<>();
			templateModel.put("verificationCode", user.getVerificationCode());
			emailService.sendTemplatedEmail(
					user.getEmail(),
					subject,
					"verification-email",
					templateModel);
		} catch (MessagingException e) {
			LOGGER.error("Failed to send verification email: {}", e.getMessage(), e);
			throw new ExternalServiceException(messageService.getMessage("email.verification.failed"), e);
		}
	}

	private UserEntity getUserByEmailOrThrow(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("error.user.not.found")));
	}

	private String getCurrentClientIp() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String xForwardedFor = request.getHeader("X-Forwarded-For");
			if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
				return xForwardedFor.split(",")[0].trim();
			}
			return request.getRemoteAddr();
		}
		return "unknown";
	}

	/**
	 * Sends a password reset email to a user
	 *
	 * @param user The user to send the password reset email to
	 * @throws ExternalServiceException If sending the email fails
	 */
	private void sendPasswordResetEmail(UserEntity user) {
		String subject = messageService.getMessage("email.password.reset.subject");

		try {
			Map<String, Object> templateModel = new HashMap<>();
			templateModel.put("passwordResetToken", user.getPasswordResetToken());

			emailService.sendTemplatedEmail(user.getEmail(), subject, "password-reset", templateModel);
		} catch (MessagingException e) {
			LOGGER.error("Failed to send password reset email: {}", e.getMessage(), e);
			throw new ExternalServiceException(messageService.getMessage("email.password.reset.failed"), e);
		}
	}

}
