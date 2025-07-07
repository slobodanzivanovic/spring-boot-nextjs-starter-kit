package com.slobodanzivanovic.backend.model.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Change password request DTO
 *
 * @author Slobodan Zivanovic
 */
public record ChangePasswordRequest(

		@NotBlank(message = "{validation.password.current.notblank}")
		String currentPassword,

		@NotBlank(message = "{validation.password.new.notblank}") @Size(min = 6, max = 40, message = "{validation.password.size}")
		String newPassword,

		@NotBlank(message = "{validation.password.confirm.notblank}")
		String confirmPassword

) {

	public boolean isPasswordsMatch() {
		return this.newPassword != null && this.newPassword.equals(this.confirmPassword);
	}
}
