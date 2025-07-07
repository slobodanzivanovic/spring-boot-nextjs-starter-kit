package com.slobodanzivanovic.backend.service.user.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.slobodanzivanovic.backend.exception.BusinessException;
import com.slobodanzivanovic.backend.exception.ValidationException;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.storage.entity.UploadedFile;
import com.slobodanzivanovic.backend.model.user.dto.request.ChangePasswordRequest;
import com.slobodanzivanovic.backend.model.user.dto.request.UpdateProfileRequest;
import com.slobodanzivanovic.backend.model.user.dto.response.UserResponse;
import com.slobodanzivanovic.backend.model.user.mapper.UserMapper;
import com.slobodanzivanovic.backend.repository.auth.UserRepository;
import com.slobodanzivanovic.backend.repository.storage.UploadedFileRepository;
import com.slobodanzivanovic.backend.service.localization.MessageService;
import com.slobodanzivanovic.backend.service.storage.FileUploadService;
import com.slobodanzivanovic.backend.service.user.UserService;
import com.slobodanzivanovic.backend.util.AuthHelper;

import lombok.RequiredArgsConstructor;

/**
 * @author Slobodan Zivanovic
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String CLASS_NAME = UserServiceImpl.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
			"image/jpeg", "image/png", "image/gif", "image/webp");

	private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

	private final UserRepository userRepository;
	private final UploadedFileRepository uploadedFileRepository;
	private final MessageService messageService;
	private final AuthHelper authHelper;
	private final FileUploadService fileUploadService;
	private final UserMapper userMapper;
	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponse updateProfilePicture(MultipartFile file) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.updateProfilePicture({})", CLASS_NAME, file.getOriginalFilename());
		}

		UserEntity currentUser = authHelper.getCurrentAuthenticatedUser();

		validateProfileImage(file);

		try {
			UploadedFile uploadedFile = new UploadedFile(
					file.getOriginalFilename(),
					file.getSize(),
					currentUser);

			String filePath = uploadedFile.buildPath("profile");

			String fileUrl = fileUploadService.uploadFile(filePath, file.getBytes());

			uploadedFile.onUploaded(fileUrl);
			uploadedFileRepository.save(uploadedFile);

			String previousImageUrl = currentUser.getProfileImageUrl();
			currentUser.setProfileImageUrl(fileUrl);
			userRepository.save(currentUser);

			LOGGER.info("Profile picture updated for user: {}. Previous URL: {}, New URL: {}",
					currentUser.getUsername(), previousImageUrl, fileUrl);

			return userMapper.map(currentUser);
		} catch (IOException e) {
			LOGGER.error("Failed to update profile picture: {}", e.getMessage(), e);
			throw new BusinessException(messageService.getMessage("error.profile.image.update.failed"));
		}

	}

	@Override
	@Transactional
	public UserResponse updateProfile(UpdateProfileRequest request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.updateProfile({})", CLASS_NAME, request);
		}

		UserEntity currentUser = authHelper.getCurrentAuthenticatedUser();

		if (request.firstName() != null && !request.firstName().trim().isEmpty()) {
			currentUser.setFirstName(request.firstName().trim());
		}

		if (request.lastName() != null && !request.lastName().trim().isEmpty()) {
			currentUser.setLastName(request.lastName().trim());
		}

		// TODO: revisit this
		if (request.phoneNumber() != null) {
			if (request.phoneNumber().trim().isEmpty()) {
				currentUser.setPhoneNumber(null);
			} else {
				currentUser.setPhoneNumber(request.phoneNumber().trim());
			}
		}

		if (request.birthDate() != null) {
			currentUser.setBirthDate(request.birthDate());
		}

		if (request.gender() != null && !request.gender().trim().isEmpty()) {
			currentUser.setGender(request.gender().trim());
		}

		UserEntity savedUser = userRepository.save(currentUser);

		LOGGER.info("Profile updated for user: {}", currentUser.getUsername());

		return userMapper.map(savedUser);
	}

	@Override
	@Transactional
	public void changePassword(ChangePasswordRequest request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}.changePassword() for user", CLASS_NAME);
		}

		UserEntity currentUser = authHelper.getCurrentAuthenticatedUser();

		if (!request.isPasswordsMatch()) {
			throw new ValidationException(messageService.getMessage("error.password.mismatch"));
		}

		if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
			throw new ValidationException(messageService.getMessage("error.password.current.invalid"));
		}

		if (passwordEncoder.matches(request.newPassword(), currentUser.getPassword())) {
			throw new ValidationException(messageService.getMessage("error.password.same.as.current"));
		}

		currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
		currentUser.setPasswordChangedAt(LocalDateTime.now());

		userRepository.save(currentUser);

		LOGGER.info("Password changed successfully for user: {}", currentUser.getUsername());
	}

	/**
	 * TODO: just for now we prob should have some validation service...
	 *
	 * @param file File to validate
	 */
	private void validateProfileImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException(messageService.getMessage("error.profile.image.empty"));
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			throw new BusinessException(messageService.getMessage(
					"error.profile.image.size.exceeded", String.valueOf(MAX_FILE_SIZE / (1024 * 1024))));
		}

		if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
			throw new BusinessException(messageService.getMessage(
					"error.profile.image.type.invalid", String.join(", ", ALLOWED_CONTENT_TYPES)));
		}
	}

}
