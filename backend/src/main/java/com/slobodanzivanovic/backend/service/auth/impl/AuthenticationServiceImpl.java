package com.slobodanzivanovic.backend.service.auth.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.slobodanzivanovic.backend.exception.BusinessException;
import com.slobodanzivanovic.backend.exception.ExternalServiceException;
import com.slobodanzivanovic.backend.exception.ResourceAlreadyExistsException;
import com.slobodanzivanovic.backend.exception.ResourceNotFoundException;
import com.slobodanzivanovic.backend.exception.ValidationException;
import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.auth.mapper.RequestMapper;
import com.slobodanzivanovic.backend.repository.auth.RoleRepository;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.service.auth.AuthenticationService;
import com.slobodanzivanovic.backend.service.email.EmailService;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

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

	public AuthenticationServiceImpl(
			MessageService messageService,
			UserRepository userRepository,
			RoleRepository roleRepository,
			RequestMapper requestMapper,
			EmailService emailService) {
		this.messageService = messageService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.requestMapper = requestMapper;
		this.emailService = emailService;
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

		//TODO: validate here (myb some validation service)
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

}
