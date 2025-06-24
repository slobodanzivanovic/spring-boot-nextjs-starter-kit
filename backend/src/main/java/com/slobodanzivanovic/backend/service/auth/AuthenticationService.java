package com.slobodanzivanovic.backend.service.auth;

import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;

/**
 * Service interface for auth operations
 *
 * @author Slobodan Zivanovic - June 21, 2025
 */
public interface AuthenticationService {

	/**
	 * Register a new user in system
	 *
	 * @param registerRequest The registration req
	 */
	void register(RegisterRequest registerRequest);

}
