package com.slobodanzivanovic.backend.service.user;

import org.springframework.web.multipart.MultipartFile;

import com.slobodanzivanovic.backend.model.user.dto.request.ChangePasswordRequest;
import com.slobodanzivanovic.backend.model.user.dto.request.UpdateProfileRequest;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;

/**
 * Service interface for managing user-related operations
 *
 * TODO: revisit add doc
 *
 * @author Slobodan Zivanovic
 */
public interface UserService {

	UserResponse updateProfilePicture(MultipartFile file);

	// TODO: revisit this maybe use own response then UserResponse
	UserResponse updateProfile(UpdateProfileRequest updateProfileRequest);

	void changePassword(ChangePasswordRequest changePasswordRequest);

}
