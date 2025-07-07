package com.slobodanzivanovic.backend.security.oauth2;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

/**
 * @author Slobodan Zivanovic
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

	// NOTE: we can make it record... i dont have time fck it

	private final OAuth2User oauth2User;
	private final UUID userId;
	private final String email;
	private final String provider;

	public CustomOAuth2User(OAuth2User oauth2User, UUID userId, String email, String provider) {
		this.oauth2User = oauth2User;
		this.userId = userId;
		this.email = email;
		this.provider = provider;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return email;
	}

}
