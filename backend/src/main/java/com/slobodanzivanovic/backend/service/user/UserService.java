package com.slobodanzivanovic.backend.service.user;

import org.springframework.web.multipart.MultipartFile;

import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;

/**
 * Service interface for managing user-related operations
 *
 * @author Slobodan Zivanovic
 */
public interface UserService {

	UserResponse updateProfilePicture(MultipartFile file);

}
