package com.slobodanzivanovic.backend.model.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.slobodanzivanovic.backend.model.auth.dto.request.RegisterRequest;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.common.mapper.BaseMapper;

/**
 * Mapper for converting registration req to user entities
 *
 * @author Slobodan Zivanovic
 */
@Mapper(componentModel = "spring")
public abstract class RequestMapper implements BaseMapper<RegisterRequest, UserEntity> {

	@Autowired
	protected BCryptPasswordEncoder passwordEncoder;

	@Override
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deleted", constant = "false")
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "deletedBy", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(source = "username", target = "username")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
	@Mapping(source = "firstName", target = "firstName")
	@Mapping(source = "lastName", target = "lastName")
	@Mapping(target = "phoneNumber", ignore = true)
	@Mapping(target = "birthDate", ignore = true)
	@Mapping(target = "gender", ignore = true)
	@Mapping(target = "verificationCode", ignore = true)
	@Mapping(target = "verificationCodeExpiresAt", ignore = true)
	@Mapping(target = "passwordResetToken", ignore = true)
	@Mapping(target = "passwordResetExpiresAt", ignore = true)
	@Mapping(target = "enabled", constant = "false")
	@Mapping(target = "accountNonLocked", constant = "true")
	@Mapping(target = "failedLoginAttempts", constant = "0")
	@Mapping(target = "accountLockedUntil", ignore = true)
	@Mapping(target = "passwordChangedAt", ignore = true)
	@Mapping(target = "lastLoginAt", ignore = true)
	@Mapping(target = "lastLoginIp", ignore = true)
	@Mapping(target = "roles", ignore = true)
	public abstract UserEntity map(RegisterRequest source);

	@Named("encodePassword")
	protected String encodePassword(String password) {
		return this.passwordEncoder.encode(password);
	}

}
