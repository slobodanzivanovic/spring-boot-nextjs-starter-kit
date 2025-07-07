package com.slobodanzivanovic.backend.security.jwt;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.slobodanzivanovic.backend.util.ApplicationContextProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * Service for managing blacklisted JWT tokens
 *
 * @author Slobodan Zivanovic
 */
@Service
public class TokenBlacklistService {

	private static final String CLASS_NAME = TokenBlacklistService.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	// we also can use HashSet here instead of conhashmap but let's stick with this
	// for thread safety
	// in future we will use redis for this
	private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

	public void blacklistToken(String token) {
		blacklistedTokens.add(token);
		LOGGER.debug("Token blacklisted: {}, Total blacklisted tokens: {}", token, blacklistedTokens.size());
	}

	public void blacklistAllUserTokens(UUID userId) {
		// TODO: WHEN REDIS IS ADDED WE WILL STORE TOKEN BY USER ID...
		// FOR NOW LETS JUST LOG
		LOGGER.info("All tokens for user {} would be blacklisted", userId);
	}

	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}

	/**
	 * TODO:NOTE:
	 * Clears expired tokens from the blacklist to prevent memory leaks (JUST FOR
	 * NOW WE WILL USE REDIS OR OUR IMPL)
	 */
	public void clearExpiredTokens() {
		Set<String> tokensToRemove = new HashSet<>();

		for (String token : blacklistedTokens) {
			try {
				JwtService jwtService = ApplicationContextProvider.bean(JwtService.class);
				Claims claims = jwtService.extractAllClaims(token);
				Date expiration = claims.getExpiration();

				if (expiration.before(new Date())) {
					tokensToRemove.add(token);
				}
			} catch (ExpiredJwtException e) {
				// Token is already expired, so we can remove it
				tokensToRemove.add(token);
			} catch (Exception e) {
				// If we cant parse the token or theres another issue, remove it
				LOGGER.warn("Removing invalid token from blacklist: {}", e.getMessage());
				tokensToRemove.add(token);
			}
		}

		blacklistedTokens.removeAll(tokensToRemove);
		LOGGER.info("Cleaned up {} expired tokens. Remaining tokens in blacklist: {}",
				tokensToRemove.size(), blacklistedTokens.size());
	}

	@Scheduled(fixedRate = 3600000) // 1hr
	public void scheduledCleanup() {
		// This will never happen i think but anyway lets add this check so it dont log
		// on every restart of app
		if (!blacklistedTokens.isEmpty()) {
			LOGGER.info("Running scheduled token blacklist cleanup");
			clearExpiredTokens();
		}
	}

}
