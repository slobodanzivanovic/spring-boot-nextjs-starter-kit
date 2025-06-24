package com.slobodanzivanovic.backend.exception;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an external service integration fails.
 * It returns a SERVICE_UNAVAILABLE (503) HTTP status.
 *
 * @author Slobodan Zivanovic - June 24, 2025
 */
public class ExternalServiceException extends CoreException {

	public ExternalServiceException(String message) {
		super(message, HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeConstants.EXTERNAL_SERVICE_ERROR);
	}

	public ExternalServiceException(String message, Throwable cause) {
		super(message, cause, HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeConstants.EXTERNAL_SERVICE_ERROR);
	}

}
