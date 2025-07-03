package com.slobodanzivanovic.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;

/**
 * @author Slobodan Zivanovic
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	List<UserEntity> findByVerificationCodeExpiresAtBeforeAndEnabledFalse(LocalDateTime dateTime);

}
