package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;
import com.slobodanzivanovic.backend.service.localization.MessageService;
import com.slobodanzivanovic.backend.util.ApplicationContextProvider;

/**
 * Exception thrown when attempting to create a resource that already exists
 * It returns a CONFLICT (409) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class ResourceAlreadyExistsException extends CoreException {

	public ResourceAlreadyExistsException(String message) {
		super(message, HttpStatus.CONFLICT, ErrorCodeConstants.RESOURCE_ALREADY_EXISTS);
	}

	public ResourceAlreadyExistsException(String resourceType, String identifier) {
		super(
				ApplicationContextProvider.bean(MessageService.class)
						.getMessage("error.resource.already.exists", resourceType, identifier),
				HttpStatus.CONFLICT,
				ErrorCodeConstants.RESOURCE_ALREADY_EXISTS);
	}

}
