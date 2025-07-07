package com.slobodanzivanovic.backend.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.common.dto.CustomResponse;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.model.user.mapper.UserMapper;
import com.slobodanzivanovic.backend.security.jwt.CustomUserDetails;
import com.slobodanzivanovic.backend.service.user.UserService;

import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;
	private final UserService userService;

	@GetMapping("/me")
	public CustomResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		UserEntity user = userDetails.user();
		UserResponse userResponse = this.userMapper.map(user);

		return CustomResponse.<UserResponse>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(userResponse)
				.build();
	}

	@PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CustomResponse<UserResponse> updateProfileImage(@RequestParam("image") MultipartFile image) {
		UserResponse updatedUser = userService.updateProfilePicture(image);

		return CustomResponse.<UserResponse>builder()
				.httpStatus(HttpStatus.OK)
				.isSuccess(true)
				.response(updatedUser)
				.build();
	}

}
