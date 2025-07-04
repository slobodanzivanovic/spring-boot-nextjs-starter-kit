package com.slobodanzivanovic.backend.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.dto.request.VerifyRequest;
import com.slobodanzivanovic.backend.model.common.dto.CustomResponse;
import com.slobodanzivanovic.backend.service.auth.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for auth operations
 *
 * @author Slobodan Zivanovic
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String CLASS_NAME = AuthController.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final AuthenticationService authenticationService;

	/**
	 * Register a new user.
	 *
	 * @param registerRequest The registration details
	 */
	@PostMapping("/register")
	public CustomResponse<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
		LOGGER.info("Registering user: {}", registerRequest);
		this.authenticationService.register(registerRequest);
		return CustomResponse.<Void>builder()
				.httpStatus(HttpStatus.CREATED)
				.isSuccess(true)
				.build();
	}

	@PostMapping("/verify")
	public CustomResponse<Void> verifyAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
		this.authenticationService.verifyUser(verifyRequest.email(), verifyRequest.verificationCode());
		return CustomResponse.SUCCESS;
	}

}
