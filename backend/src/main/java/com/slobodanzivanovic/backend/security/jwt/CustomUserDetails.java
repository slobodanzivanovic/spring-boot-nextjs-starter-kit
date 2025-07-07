package com.slobodanzivanovic.backend.security.jwt;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;

/**
 * Custom implementation of Spring Security UserDetails interface
 *
 * @author Slobodan Zivanovic
 */
public record CustomUserDetails(UserEntity user) implements UserDetails {

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.user.getRoles().stream()
				.map(RoleEntity::getName)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.user.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.isEnabled();
	}

}
