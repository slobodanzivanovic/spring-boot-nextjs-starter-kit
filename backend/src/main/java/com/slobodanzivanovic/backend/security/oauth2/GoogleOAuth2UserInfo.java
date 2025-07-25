package com.slobodanzivanovic.backend.security.oauth2;

import java.util.Map;

/**
 * @author Slobodan Zivanovic
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String) attributes.get("sub");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) attributes.get("picture");
	}

	@Override
	public String getFirstName() {
		return (String) attributes.get("given_name");
	}

	@Override
	public String getLastName() {
		return (String) attributes.get("family_name");
	}

}
