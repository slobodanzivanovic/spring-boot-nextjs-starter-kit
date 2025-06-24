package com.slobodanzivanovic.backend.repository.auth;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Slobodan Zivanovic - June 21, 2025
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	List<UserEntity> findByVerificationCodeExpiresAtBeforeAndEnabledFalse(LocalDateTime dateTime);

}
