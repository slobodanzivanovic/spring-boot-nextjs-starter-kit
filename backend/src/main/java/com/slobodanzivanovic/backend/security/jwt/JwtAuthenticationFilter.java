package com.slobodanzivanovic.backend.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.slobodanzivanovic.backend.service.localization.MessageService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Filter for JWT-based authentication
 *
 * @author Slobodan Zivanovic
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String CLASS_NAME = JwtAuthenticationFilter.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final TokenBlacklistService tokenBlacklistService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = authHeader.substring(7);

		if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
			LOGGER.warn("Attempt to use blacklisted token: {}", jwt);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					messageService.getMessage("error.token.blacklisted"));
			return;
		}

		try {
			String username = jwtService.extractUsername(jwt);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					if (!userDetails.isAccountNonLocked()) {
						LOGGER.warn("Attempt to use token for locked account: {}", username);
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
								messageService.getMessage("error.login.account_locked"));
						return;
					}

					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null, // null since we dont need password for token auth
							userDetails.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					LOGGER.debug("Successfully authenticated user: {}", username);
				}
			}

		} catch (ExpiredJwtException e) {
			LOGGER.error("Expired JWT token: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					messageService.getMessage("error.token.expired"));
			return;
		} catch (SignatureException e) {
			LOGGER.error("Invalid JWT signature: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					messageService.getMessage("error.token.invalid.signature"));
			return;
		} catch (Exception e) {
			LOGGER.error("Error processing JWT token: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					messageService.getMessage("error.token.processing"));
			return;
		}

		filterChain.doFilter(request, response);
	}

}
