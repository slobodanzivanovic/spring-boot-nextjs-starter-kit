package com.slobodanzivanovic.backend.repository.auth;

import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for roles
 *
 * @author Slobodan Zivanovic - June 21, 2025
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

	Optional<RoleEntity> findByName(String name);

}
