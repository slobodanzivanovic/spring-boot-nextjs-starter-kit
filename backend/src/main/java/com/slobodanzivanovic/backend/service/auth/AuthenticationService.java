package com.slobodanzivanovic.backend.service.auth;

import com.slobodanzivanovic.backend.model.auth.dto.request.LoginRequest;
import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.dto.response.LoginResponse;

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

	/**
	 * Verifies a user account using the verification code
	 *
	 * @param email            The email of the account to verify
	 * @param verificationCode The verification code sent to the users email
	 */
	void verifyUser(String email, String verificationCode);

	/**
	 * Resends the verification code to a users email
	 *
	 * @param email The email to send the verification code to
	 */
	void resendVerificationCode(String email);

	/**
	 * Authenticates a user and generates a JWT token
	 *
	 * @param loginRequest The login request containing credentials
	 * @return A login response containing the JWT token and expiration time
	 */
	LoginResponse login(LoginRequest loginRequest);

	/**
	 * Logs out a user by invalidating their JWT token
	 *
	 * @param token The jwt token to invalidate
	 */
	void logout(String token);

	/**
	 * Initiates a password reset by sending a reset token
	 *
	 * @param email The email of the account to reset the password for
	 */
	void requestPasswordReset(String email);

	/**
	 * Resets a user password
	 *
	 * @param email              The email of the account
	 * @param passwordResetToken The password reset token sent to the user's email
	 * @param newPassword        The new password
	 */
	void resetPassword(String email, String passwordResetToken, String newPassword);

}
