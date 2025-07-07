package com.slobodanzivanovic.backend.model.user.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.common.mapper.BaseMapper;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;

/**
 * @author Slobodan Zivanovic
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserEntity, UserResponse> {

	@Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
	@Override
	UserResponse map(UserEntity source);

	@Named("rolesToStrings")
	default Set<String> rolesToStrings(Set<RoleEntity> roles) {
		if (roles == null) {
			return Set.of();
		}
		return roles.stream()
				.map(role -> role.getName())
				.collect(Collectors.toSet());
	}

}
