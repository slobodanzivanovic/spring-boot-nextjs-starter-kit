package com.slobodanzivanovic.backend.model.user.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Delete account request DTO
 *
 * @author Slobodan Zivanovic
 */
public record DeleteAccountRequest(

		@NotBlank(message = "{validation.password.notblank}")
		String password

) {
}
