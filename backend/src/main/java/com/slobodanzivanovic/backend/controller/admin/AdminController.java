package com.slobodanzivanovic.backend.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.slobodanzivanovic.backend.model.common.dto.CustomResponse;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.service.admin.AdminService;
import com.slobodanzivanovic.backend.util.PagedResponse;

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

}
