package com.slobodanzivanovic.backend.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.util.ApplicationContextProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service for JWT token operations
 *
 * @author Slobodan Zivanovic
 */
@Service
public class JwtService {

	private static final String CLASS_NAME = JwtService.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	@Value("${jwt.token.secret-key}")
	private String secretKey;

	@Value("${jwt.token.expiration}")
	private Long jwtExpiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		UserEntity user = ((CustomUserDetails) userDetails).user();

		claims.put("id", user.getId());
		claims.put("email", user.getEmail());
		claims.put("roles", userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()));
		claims.put("locked", !user.isAccountNonLocked());

		Date now = new Date();
		if (user.getPasswordChangedAt() != null) {
			claims.put("passwordChangedAt", user.getPasswordChangedAt().toString());
		}

		long expiration = jwtExpiration;

		return Jwts
				.builder()
				.claims(claims)
				.subject(userDetails.getUsername())
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expiration))
				.signWith(getSignInKey())
				.compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);

		TokenBlacklistService tokenBlacklistService = ApplicationContextProvider.bean(TokenBlacklistService.class);

		if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)
				|| tokenBlacklistService.isTokenBlacklisted(token)) {
			return false;
		}

		// If token was issued before password was changed, invalidate it
		if (userDetails instanceof CustomUserDetails) {
			UserEntity user = ((CustomUserDetails) userDetails).user();
			LocalDateTime passwordChangedAt = user.getPasswordChangedAt();

			if (passwordChangedAt != null) {
				try {
					Date tokenIssuedAt = extractClaim(token, Claims::getIssuedAt);
					LocalDateTime tokenIssuedDateTime = LocalDateTime.ofInstant(
							tokenIssuedAt.toInstant(), ZoneId.systemDefault());

					// If token was issued before password change, its invalid
					if (tokenIssuedDateTime.isBefore(passwordChangedAt)) {
						return false;
					}
				} catch (Exception e) {
					LOGGER.warn("Error checking token issue date against password change: {}", e.getMessage());
					return false;
				}
			}
		}

		return true;
	}

	private boolean isTokenExpired(String token) {
		try {
			return extractExpiration(token).before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
