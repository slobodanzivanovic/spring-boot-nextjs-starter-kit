package com.slobodanzivanovic.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;
import com.slobodanzivanovic.backend.model.common.dto.CustomResponse;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

/**
 * Global exception handler
 * Handler catches and processes exceptions thrown throughout the app,
 * translating them into appro HTTP responses with standard error formats
 *
 * @author Slobodan Zivanovic
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private static final String CLASS_NAME = GlobalExceptionHandler.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	/**
	 * Handle core app exceptions. Processes exceptions that extend CoreException
	 *
	 * @param ex      The core exception
	 * @param request The HTTP request
	 * @return A standard error response
	 */
	@ExceptionHandler(CoreException.class)
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleCoreException(
			CoreException ex, HttpServletRequest request) {

		LOGGER.error("Core exception occurred: {}", ex.getMessage(), ex);

		Map<String, Object> details = new HashMap<>();
		details.put("message", ex.getMessage());
		details.put("errorCode", ex.getErrorCode());
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(ex.getHttpStatus())
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(ex.getHttpStatus())
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle all other unhandled exceptions. Acts as a catch-all for any exceptions
	 * not specifically handled by other exception handlers.
	 *
	 * @param ex      The exception
	 * @param request The HTTP request
	 * @return A generic error response
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleGenericException(
			Exception ex, HttpServletRequest request) {

		LOGGER.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage("error.internal.server.error"));
		details.put("errorCode", ErrorCodeConstants.INTERNAL_SERVER_ERROR);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle validation exceptions from request body validation
	 * Processes MethodArgumentNotValidException which occurs when @Valid validation
	 * fails on a method argument, typically request bodies
	 *
	 * @param ex      The validation exception
	 * @param request The HTTP request
	 * @return A validation error response with field-specific errors
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleValidationExceptions(
			MethodArgumentNotValidException ex, HttpServletRequest request) {

		LOGGER.error("Validation exception occurred: {}", ex.getMessage());

		String errorMessages = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		Map<String, Object> validationErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage("error.validation.failed", errorMessages));
		details.put("errorCode", ErrorCodeConstants.VALIDATION_FAILED);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());
		details.put("errors", validationErrors);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(HttpStatus.BAD_REQUEST)
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle constraint violation exceptions
	 * Processes ConstraintViolationException which occurs when bean validation
	 * constraints are violated
	 *
	 * @param ex      The constraint violation exception
	 * @param request The HTTP request
	 * @return A validation error response with constraint-specific errors
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleConstraintViolationException(
			ConstraintViolationException ex, HttpServletRequest request) {

		LOGGER.error("Constraint violation exception occurred: {}", ex.getMessage());

		Map<String, Object> validationErrors = new HashMap<>();
		ex.getConstraintViolations().forEach(
				violation -> validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage("error.constraint.violation", ex.getMessage()));
		details.put("errorCode", ErrorCodeConstants.CONSTRAINT_VIOLATION);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());
		details.put("errors", validationErrors);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(HttpStatus.BAD_REQUEST)
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle not found exceptions
	 * Processes exceptions related to resources not being found in the system
	 *
	 * @param ex      The not found exception
	 * @param request The HTTP request
	 * @return A not found error response
	 */
	@ExceptionHandler({
			EntityNotFoundException.class,
			UsernameNotFoundException.class,
	})
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleNotFoundExceptions(
			Exception ex, HttpServletRequest request) {

		LOGGER.error("Not found exception occurred: {}", ex.getMessage());

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage("error.resource.not.found.global", ex.getMessage()));
		details.put("errorCode", ErrorCodeConstants.RESOURCE_NOT_FOUND);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(HttpStatus.NOT_FOUND)
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle authentication exceptions
	 * Processes exceptions related to authentication failures such as
	 * invalid credentials or disabled accounts
	 *
	 * @param ex      The authentication exception
	 * @param request The HTTP request
	 * @return An authentication error response
	 */
	@ExceptionHandler({
			BadCredentialsException.class,
			DisabledException.class,
			LockedException.class
	})
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleAuthenticationExceptions(
			Exception ex, HttpServletRequest request) {

		LOGGER.error("Authentication exception occurred: {}", ex.getMessage());

		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String errorCode;
		String messageKey;

		if (ex instanceof DisabledException) {
			messageKey = "error.account.disabled";
			errorCode = ErrorCodeConstants.ACCOUNT_DISABLED;
		} else if (ex instanceof LockedException) {
			messageKey = "error.account.locked";
			errorCode = ErrorCodeConstants.ACCOUNT_LOCKED;
		} else if (ex instanceof BadCredentialsException) {
			messageKey = "error.invalid.credentials";
			errorCode = ErrorCodeConstants.INVALID_CREDENTIALS;
		} else {
			messageKey = "error.authentication.failed";
			errorCode = ErrorCodeConstants.AUTHENTICATION_FAILED;
		}

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage(messageKey));
		details.put("errorCode", errorCode);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(status)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(status)
						.isSuccess(false)
						.response(details)
						.build());
	}

	/**
	 * Handle type mismatch exceptions
	 * <p>
	 * Processes exceptions that occur when the type of a method argument
	 * does not match the expected type, often for path variables or request
	 * parameters
	 * </p>
	 *
	 * @param ex      The type mismatch exception
	 * @param request The HTTP request
	 * @return A type mismatch error response
	 */
	@SuppressWarnings("null")
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<CustomResponse<Map<String, Object>>> handleTypeMismatchException(
			MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

		LOGGER.error("Type mismatch exception occurred: {}", ex.getMessage());

		String parameterName = ex.getName();
		String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

		Map<String, Object> details = new HashMap<>();
		details.put("message", messageService.getMessage("error.type.mismatch", parameterName, requiredType));
		details.put("errorCode", ErrorCodeConstants.TYPE_MISMATCH);
		details.put("path", request.getRequestURI());
		details.put("timestamp", LocalDateTime.now());
		details.put("parameterName", parameterName);
		details.put("requiredType", requiredType);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(CustomResponse.<Map<String, Object>>builder()
						.httpStatus(HttpStatus.BAD_REQUEST)
						.isSuccess(false)
						.response(details)
						.build());
	}

}
