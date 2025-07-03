package com.slobodanzivanovic.backend.service.auth;

import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;

/**
 * @author Slobodan Zivanovic
 */
public interface AuthenticationService {

	/**
	 * Register a new user in system
	 *
	 * @param registerRequest The registration req
	 */
	void register(RegisterRequest registerRequest);

}
