package com.slobodanzivanovic.backend.model.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for account verification.
 *
 * @author Slobodan Zivanovic
 */
public record VerifyRequest(

		@NotBlank(message = "{validation.email.notblank}")
		@Email(message = "{validation.email.invalid}")
		String email,

		@NotBlank(message = "{validation.verification.code.notblank}")
		String verificationCode

) {
}
