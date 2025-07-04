package com.slobodanzivanovic.backend.constants;

/**
 * @author Slobodan Zivanovic
 */
public class ErrorCodeConstants {

	private ErrorCodeConstants() {
	}

	public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
	public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
	public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
	public static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
	public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
	public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";
	public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
	public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
	public static final String TYPE_MISMATCH = "TYPE_MISMATCH";
	public static final String RESOURCE_ALREADY_EXISTS = "RESOURCE_ALREADY_EXISTS";
	public static final String EXTERNAL_SERVICE_ERROR = "EXTERNAL_SERVICE_ERROR";
	public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";

}
