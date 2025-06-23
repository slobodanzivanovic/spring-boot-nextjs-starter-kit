package com.slobodanzivanovic.backend.service.auth.impl;

import com.slobodanzivanovic.backend.exception.ResourceNotFoundException;
import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.auth.mapper.RequestMapper;
import com.slobodanzivanovic.backend.repository.auth.RoleRepository;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.service.auth.AuthenticationService;
import com.slobodanzivanovic.backend.service.localization.MessageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Slobodan Zivanovic - June 21, 2025
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final String CLASS_NAME = AuthenticationServiceImpl.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RequestMapper requestMapper;

	public AuthenticationServiceImpl(
		MessageService messageService,
		RequestMapper requestMapper,
		UserRepository userRepository,
		RoleRepository roleRepository
	) {
		this.messageService = messageService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.requestMapper = requestMapper;
	}

	@Override
	@Transactional
	public UserEntity register(RegisterRequest registerRequest) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.signup({})", CLASS_NAME, registerRequest);
		}

		// validate here (myb some validation service) TODO:
		UserEntity newUser = requestMapper.map(registerRequest);

		RoleEntity userRole = roleRepository.findByName("ROLE_USER")
			.orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("error.user.role")));
		newUser.addRole(userRole);

		String verificationCode = "12345"; // create generate method...
		newUser.setVerificationCode(verificationCode);
		newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
		newUser.setCreatedBy(registerRequest.username());

		// send email

		return userRepository.save(newUser);
	}

}
