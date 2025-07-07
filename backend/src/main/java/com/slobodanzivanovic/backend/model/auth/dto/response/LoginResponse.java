package com.slobodanzivanovic.backend.model.auth.dto.response;

/**
 * DTO for user login response
 *
 * @author Slobodan Zivanovic
 */
public record LoginResponse(

		String token,

		long expirationTime

) {
}
