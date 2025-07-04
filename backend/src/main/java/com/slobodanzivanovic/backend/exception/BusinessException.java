package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;

/**
 * Exception thrown when a business rule is violated
 * It returns a BAD_REQUEST (400) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class BusinessException extends CoreException {

	public BusinessException(String message) {
		super(message, HttpStatus.BAD_REQUEST, ErrorCodeConstants.BUSINESS_RULE_VIOLATION);
	}

}
