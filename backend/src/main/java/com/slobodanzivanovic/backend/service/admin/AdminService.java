package com.slobodanzivanovic.backend.service.admin;

import java.util.UUID;

import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.util.PagedResponse;

/**
 * TODO: revisit add docs
 *
 * @author Slobodan Zivanovic
 */
public interface AdminService {

	PagedResponse<UserResponse> getUsers(int page);

	UserResponse getUserById(UUID userId);

}
