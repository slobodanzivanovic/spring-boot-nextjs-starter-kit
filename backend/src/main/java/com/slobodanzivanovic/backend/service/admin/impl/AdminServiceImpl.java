package com.slobodanzivanovic.backend.service.admin.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slobodanzivanovic.backend.exception.BusinessException;
import com.slobodanzivanovic.backend.exception.ResourceNotFoundException;
import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.model.user.mapper.UserMapper;
import com.slobodanzivanovic.backend.repository.auth.RoleRepository;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.service.admin.AdminService;
import com.slobodanzivanovic.backend.service.localization.MessageService;
import com.slobodanzivanovic.backend.util.AuthHelper;
import com.slobodanzivanovic.backend.util.PagedResponse;

import lombok.AllArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

	private static final String CLASS_NAME = AdminServiceImpl.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private final MessageService messageService;

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final AuthHelper authHelper;
	private final RoleRepository roleRepository;

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<UserResponse> getUsers(int page) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.getUsers(page={})", CLASS_NAME, page);
		}

		Pageable pageable = PageRequest.of(page, 10);
		Page<UserEntity> users = userRepository.findAll(pageable);

		return new PagedResponse<>(users.map(userMapper::map));
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(UUID userId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.getUserById({})", CLASS_NAME, userId);
		}

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("error.user.not.found")));

		return userMapper.map(user);
	}

	@Override
	@Transactional
	public UserResponse updateUserRoles(UUID userId, Set<String> roleNames) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.updateUserRoles({}, {})", CLASS_NAME, userId, roleNames);
		}

		UserEntity currentAdmin = authHelper.getCurrentAuthenticatedUser();
		UserEntity targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						messageService.getMessage("error.user.not.found")));

		if (targetUser.getId().equals(currentAdmin.getId())) {
			throw new BusinessException(messageService.getMessage("error.admin.update_own_role"));
		}

		Set<RoleEntity> newRoles = new HashSet<>();
		for (String roleName : roleNames) {
			String fullRoleName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
			RoleEntity role = roleRepository.findByName(fullRoleName).orElseThrow(() -> new ResourceNotFoundException(
					messageService.getMessage("error.role.not.found", fullRoleName)));
			newRoles.add(role);
		}

		targetUser.getRoles().clear();
		targetUser.getRoles().addAll(newRoles);

		UserEntity savedUser = userRepository.save(targetUser);

		LOGGER.info("User roles updated for user: {}. New roles: {}",
				targetUser.getUsername(), roleNames);

		return userMapper.map(savedUser);
	}

	@Override
	@Transactional
	public UserResponse updateUserLockStatus(UUID userId, boolean locked, int daysToUnlock) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.updateUserLockStatus({}, {})", CLASS_NAME, userId, locked);
		}

		UserEntity currentAdmin = authHelper.getCurrentAuthenticatedUser();
		UserEntity targetUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						messageService.getMessage("error.user.not.found")));

		if (targetUser.getId().equals(currentAdmin.getId()) && locked) {
			throw new BusinessException(messageService.getMessage("error.admin.self.lock"));
		}

		targetUser.setAccountNonLocked(!locked);
		if (locked) {
			targetUser.setAccountLockedUntil(LocalDateTime.now().plusDays(daysToUnlock));
		} else {
			targetUser.setAccountLockedUntil(null);
			targetUser.resetFailedLoginAttempts();
		}

		UserEntity savedUser = userRepository.save(targetUser);

		LOGGER.info("Admin {} {} user account: {}",
				currentAdmin.getUsername(), locked ? "locked" : "unlocked", targetUser.getUsername());

		return userMapper.map(savedUser);
	}

}
