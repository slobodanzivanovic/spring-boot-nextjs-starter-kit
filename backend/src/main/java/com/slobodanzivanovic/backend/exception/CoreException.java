package com.slobodanzivanovic.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all app-specific exceptions.
 *
 * @author Slobodan Zivanovic - June 21, 2025
 */
@Getter
public abstract class CoreException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String errorCode;

	protected CoreException(String message, HttpStatus httpStatus, String errorCode) {
		super(message);
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
	}

	protected CoreException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
		super(message, cause);
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
	}

}
