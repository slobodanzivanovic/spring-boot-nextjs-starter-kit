package com.slobodanzivanovic.backend.repository.auth;

import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Slobodan Zivanovic - June 21, 2025
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
