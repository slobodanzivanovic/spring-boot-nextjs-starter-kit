package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;

/**
 * Exception thrown when authentication fails. It returns a UNAUTHORIZED (401) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class AuthenticationException extends CoreException {

	public AuthenticationException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, ErrorCodeConstants.AUTHENTICATION_FAILED);
	}

}
