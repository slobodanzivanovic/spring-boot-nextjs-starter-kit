package com.slobodanzivanovic.backend.model.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile information
 *
 * @author Slobodan Zivanovic
 */
public record UpdateProfileRequest(

		@Size(min = 2, max = 50, message = "{validation.firstname.size}")
		String firstName,

		@Size(min = 2, max = 50, message = "{validation.lastname.size}")
		String lastName,

		@Pattern(regexp = "^\\+?[0-9]\\d{1,14}$", message = "{validation.phone.invalid}")
		String phoneNumber,

		@Past(message = "{validation.birthdate.past}")
		LocalDate birthDate,

		@Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)$", message = "{validation.gender.invalid}")
		String gender

) {
}
