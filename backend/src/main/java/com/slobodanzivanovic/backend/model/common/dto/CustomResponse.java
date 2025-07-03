package com.slobodanzivanovic.backend.model.common.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a generic response object
 *
 * @param <T> Type of the response payload
 *
 * @author Slobodan Zivanovic
 */
@Getter
@Builder
public class CustomResponse<T> {

	@Builder.Default
	private LocalDateTime time = LocalDateTime.now();

	private HttpStatus httpStatus;

	private Boolean isSuccess;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T response;

	/**
	 * Default success response with HTTP OK status and success indicator set to
	 * true
	 */
	public static final CustomResponse<Void> SUCCESS = CustomResponse.<Void>builder()
			.httpStatus(HttpStatus.OK)
			.isSuccess(true)
			.build();

	/**
	 * Creates a success response with the provided payload and HTTP OK status
	 *
	 * @param <T>      Type of the response payload
	 * @param response Response payload
	 * @return CustomResponse instance with success status, HTTP OK, and the provided payload
	 */
	public static <T> CustomResponse<T> successOf(final T response) {
		return CustomResponse.<T>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(response)
				.build();
	}

}
