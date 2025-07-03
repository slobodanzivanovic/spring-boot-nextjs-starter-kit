package com.slobodanzivanovic.backend.repository.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.slobodanzivanovic.backend.model.auth.entity.RoleEntity;

/**
 * Repository for roles
 *
 * @author Slobodan Zivanovic
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

	Optional<RoleEntity> findByName(String name);

}
