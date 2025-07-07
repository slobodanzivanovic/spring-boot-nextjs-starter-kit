package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;

/**
 * Exception thrown when processing OAuth authentication fails. It returns a BAD_REQUEST (400) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class OAuthProcessingException extends CoreException {

	public OAuthProcessingException(String message) {
		super(message, HttpStatus.BAD_REQUEST, ErrorCodeConstants.OAUTH_PROCESSING_ERROR);
	}

	public OAuthProcessingException(String message, Throwable cause) {
		super(message, cause, HttpStatus.BAD_REQUEST, ErrorCodeConstants.OAUTH_PROCESSING_ERROR);
	}

}
