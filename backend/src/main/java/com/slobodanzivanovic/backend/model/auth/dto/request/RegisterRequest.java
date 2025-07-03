package com.slobodanzivanovic.backend.model.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests
 *
 * @author Slobodan Zivanovic
 */
public record RegisterRequest(

		@NotBlank(message = "{validation.username.notblank}")
		@Size(min = 3, max = 50, message = "{validation.username.size}")
		String username,

		@NotBlank(message = "{validation.email.notblank}")
		@Email(message = "{validation.email.invalid}")
		@Size(max = 100, message = "{validation.email.size}")
		String email,

		@NotBlank(message = "{validation.password.notblank}")
		@Size(min = 6, max = 40, message = "{validation.password.size}")
		String password,

		@Size(max = 50, message = "{validation.firstname.size}")
		String firstName,

		@Size(max = 50, message = "{validation.lastname.size}")
		String lastName

) {

	public RegisterRequest {
		if (firstName == null) {
			firstName = "Default First Name";
		}

		if (lastName == null) {
			lastName = "Default Last Name";
		}
	}
}
