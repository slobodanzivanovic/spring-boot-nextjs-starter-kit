package com.slobodanzivanovic.backend.controller.admin;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.slobodanzivanovic.backend.model.common.dto.CustomResponse;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.service.admin.AdminService;
import com.slobodanzivanovic.backend.util.PagedResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users")
	public CustomResponse<PagedResponse<UserResponse>> getUsers(
			@RequestParam(value = "page", defaultValue = "0") int page) {
		PagedResponse<UserResponse> users = adminService.getUsers(page);

		return CustomResponse.<PagedResponse<UserResponse>>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(users)
				.build();
	}

	@GetMapping("/users/{userId}")
	public CustomResponse<UserResponse> getUserById(@PathVariable UUID userId) {

		UserResponse user = adminService.getUserById(userId);

		return CustomResponse.<UserResponse>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(user)
				.build();
	}

	@PutMapping("/users/{userId}/roles")
	public CustomResponse<UserResponse> updateUserRoles(
			@PathVariable UUID userId,
			@RequestBody @Valid Set<String> roleNames) {

		UserResponse updatedUser = adminService.updateUserRoles(userId, roleNames);

		return CustomResponse.<UserResponse>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(updatedUser)
				.build();
	}

	@PutMapping("/users/{userId}/lock")
	public CustomResponse<UserResponse> updateUserLockStatus(
			@PathVariable UUID userId,
			@RequestParam boolean locked,
			@RequestParam(required = false, defaultValue = "0") Integer lockDurationDays) {

		UserResponse user = adminService.updateUserLockStatus(userId, locked, lockDurationDays);

		return CustomResponse.<UserResponse>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(user)
				.build();
	}

}
