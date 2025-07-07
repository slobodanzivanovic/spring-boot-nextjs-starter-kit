package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;

/**
 * Exception thrown when there is an issue with a JWT token. It returns a UNAUTHORIZED (401) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class TokenException extends CoreException {

	public TokenException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, ErrorCodeConstants.INVALID_TOKEN);
	}

}
