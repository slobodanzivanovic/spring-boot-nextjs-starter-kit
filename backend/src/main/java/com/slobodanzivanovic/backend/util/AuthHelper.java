package com.slobodanzivanovic.backend.util;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.slobodanzivanovic.backend.exception.ResourceNotFoundException;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.security.jwt.CustomUserDetails;
import com.slobodanzivanovic.backend.service.localization.MessageService;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for authentication-related operations
 *
 * @author Slobodan Zivanovic
 */
@Component
@RequiredArgsConstructor
public class AuthHelper {

	private final MessageService messageService;

	/**
	 * Gets the currently authenticated user
	 *
	 * @return The current authenticated user entity
	 * @throws ResourceNotFoundException if no authenticated user is found
	 */
	public UserEntity getCurrentAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
				authentication instanceof AnonymousAuthenticationToken) {
			throw new ResourceNotFoundException(messageService.getMessage("error.user.not.authenticated"));
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			return ((CustomUserDetails) authentication.getPrincipal()).user();
		}

		throw new ResourceNotFoundException(messageService.getMessage("error.user.not.found"));
	}

	/**
	 * Gets the currently authenticated user without throwing exceptions
	 *
	 * @return Optional containing the user if authenticated, empty otherwise
	 */
	public Optional<UserEntity> getCurrentUserOptional() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
				authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			return Optional.of(((CustomUserDetails) authentication.getPrincipal()).user());
		}

		return Optional.empty();
	}

	/**
	 * Checks if there is an authenticated user in the current security context
	 *
	 * @return true if a user is authenticated, false otherwise
	 */
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
	}

	/**
	 * Gets the username of the currently authenticated user
	 *
	 * @return The username as a string, or null if no user is authenticated
	 */
	public String getCurrentUsername() {
		if (!isAuthenticated()) {
			return null;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	/**
	 * Gets the roles of the currently authenticated user
	 *
	 * @return A list of role names, or an empty list if no user is authenticated
	 */
	public List<String> getCurrentUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
				authentication instanceof AnonymousAuthenticationToken) {
			return List.of();
		}

		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
	}

	/**
	 * Checks if the current user has a specific role
	 *
	 * @param role The role to check for (without the "ROLE_" prefix)
	 * @return true if the user has the role, false otherwise
	 */
	public boolean hasRole(String role) {
		String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
		return getCurrentUserRoles().contains(roleWithPrefix);
	}
}
