package com.slobodanzivanovic.backend.model.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login
 *
 * @author Slobodan Zivanovic
 */
public record LoginRequest(

		@NotBlank(message = "{validation.identifier.notblank}")
		String identifier,

		@NotBlank(message = "{validation.password.notblank}")
		String password

) {
}
