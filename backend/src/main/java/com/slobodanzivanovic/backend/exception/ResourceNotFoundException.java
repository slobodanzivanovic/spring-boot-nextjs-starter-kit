package com.slobodanzivanovic.backend.exception;

import org.springframework.http.HttpStatus;

import com.slobodanzivanovic.backend.constants.ErrorCodeConstants;
import com.slobodanzivanovic.backend.service.localization.MessageService;

/**
 * Exception thrown when a requested resource cannot be found
 * It returns a NOT_FOUND (404) HTTP status
 *
 * @author Slobodan Zivanovic
 */
public class ResourceNotFoundException extends CoreException {

	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND, ErrorCodeConstants.RESOURCE_NOT_FOUND);
	}

	public ResourceNotFoundException(String resourceType, String identifier) {
		super(
				MessageService.getStaticMessage("error.resource.not.found", resourceType, identifier),
				HttpStatus.NOT_FOUND,
				ErrorCodeConstants.RESOURCE_NOT_FOUND);
	}

}
