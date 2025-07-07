package com.slobodanzivanovic.backend.security.oauth2;

import java.util.Map;

/**
 * @author Slobodan Zivanovic
 */
public abstract class OAuth2UserInfo {

	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

	// TODO: when we add s3
	public abstract String getImageUrl();

	public String getFirstName() {
		String name = getName();
		if (name != null && name.contains(" ")) {
			return name.split(" ")[0];
		}
		return name;
	}

	public String getLastName() {
		String name = getName();
		if (name != null && name.contains(" ")) {
			String[] parts = name.split(" ");
			if (parts.length > 1) {
				return parts[parts.length - 1];
			}
		}
		return "";
	}

}
