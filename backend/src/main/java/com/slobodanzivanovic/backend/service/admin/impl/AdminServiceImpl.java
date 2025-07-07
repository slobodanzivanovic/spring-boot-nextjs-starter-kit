package com.slobodanzivanovic.backend.service.admin.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.model.user.mapper.UserMapper;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.service.admin.AdminService;
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

	private final UserRepository userRepository;
	private final UserMapper userMapper;

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

}
