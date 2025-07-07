package com.slobodanzivanovic.backend.model.user.dto.response;

import java.util.Set;
import java.util.UUID;

/**
 * @author Slobodan Zivanovic
 */
public record UserResponse(

		UUID id,

		String username,

		String email,

		String firstName,

		String lastName,

		String profileImageUrl,

		Set<String> roles

) {
}
