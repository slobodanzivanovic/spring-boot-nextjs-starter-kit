package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;

/**
 * @author Slobodan Zivanovic
 */
public class ValidationException extends CoreException {

	public ValidationException(String message) {
		super(message, HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_FAILED);
	}

}
