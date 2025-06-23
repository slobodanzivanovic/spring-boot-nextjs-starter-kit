package com.slobodanzivanovic.backend.service.auth;

import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;

/**
 * Service interface for auth operations
 *
 * @author Slobodan Zivanovic - June 21, 2025
 */
public interface AuthenticationService {

	UserEntity register(RegisterRequest registerRequest);

}
