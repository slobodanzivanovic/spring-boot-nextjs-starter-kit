package com.slobodanzivanovic.backend.model.auth.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.slobodanzivanovic.backend.model.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a base user in the system
 *
 * @author Slobodan Zivanovic
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "roles")
public class UserEntity extends BaseEntity {

	@Column(name = "username", unique = true, nullable = false, length = 50)
	private String username;

	@Column(name = "email", unique = true, nullable = false, length = 100)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "gender")
	private String gender;

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "verification_code_expires_at")
	private LocalDateTime verificationCodeExpiresAt;

	@Column(name = "password_reset_token")
	private String passwordResetToken;

	@Column(name = "password_reset_expires_at")
	private LocalDateTime passwordResetExpiresAt;

	@Column(name = "enabled", nullable = false)
	@lombok.Builder.Default
	private boolean enabled = false;

	@Column(name = "account_non_locked", nullable = false)
	@lombok.Builder.Default
	private boolean accountNonLocked = true;

	@Column(name = "failed_login_attempts")
	@lombok.Builder.Default
	private Integer failedLoginAttempts = 0;

	@Column(name = "account_locked_until")
	private LocalDateTime accountLockedUntil;

	@Column(name = "password_changed_at")
	private LocalDateTime passwordChangedAt;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Column(name = "last_login_ip")
	private String lastLoginIp;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@lombok.Builder.Default
	private Set<RoleEntity> roles = new HashSet<>();

	// helper methods
	public void addRole(RoleEntity role) {
		roles.add(role);
		role.getUsers().add(this);
	}

	public void removeRole(RoleEntity role) {
		roles.remove(role);
		role.getUsers().remove(this);
	}

	/**
	 * Increments failed login attempts and locks account if threshold is reached
	 *
	 * @param maxAttempts  maximum allowed failed attempts before locking
	 * @param lockTimeMins duration in minutes to lock the account
	 * @return true if account was locked as a result of this increment
	 */
	public boolean incrementFailedLoginAttempts(int maxAttempts, int lockTimeMins) {
		this.failedLoginAttempts++;

		if (this.failedLoginAttempts >= maxAttempts) {
			this.accountNonLocked = false;
			this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockTimeMins);
			return true;
		}
		return false;
	}

	/**
	 * Reset failed login attempts on successful login
	 */
	public void resetFailedLoginAttempts() {
		this.failedLoginAttempts = 0;
	}

	/**
	 * Records a successful login
	 *
	 * @param ipAddress the IP address of the login attempt
	 */
	public void recordSuccessfulLogin(String ipAddress) {
		this.lastLoginAt = LocalDateTime.now();
		this.lastLoginIp = ipAddress;
		resetFailedLoginAttempts();
	}

	/**
	 * Override soft delete to handle user-specific logic
	 *
	 * @param deletedBy username of the user who performed the deletion
	 */
	@Override
	public void softDelete(String deletedBy) {
		super.softDelete(deletedBy); // Call base implementation first
		this.enabled = false; // User-specific logic
	}

	/**
	 * Checks if the verification code is expired
	 *
	 * @return true if expired or null
	 */
	public boolean isVerificationCodeExpired() {
		return verificationCodeExpiresAt == null ||
				LocalDateTime.now().isAfter(verificationCodeExpiresAt);
	}

	/**
	 * Checks if the password reset token is expired
	 *
	 * @return true if expired or null
	 */
	public boolean isPasswordResetTokenExpired() {
		return passwordResetExpiresAt == null ||
				LocalDateTime.now().isAfter(passwordResetExpiresAt);
	}

	/**
	 * Checks if the account lock period has expired
	 *
	 * @return true if the account can be unlocked
	 */
	public boolean canUnlockAccount() {
		return accountLockedUntil != null &&
				LocalDateTime.now().isAfter(accountLockedUntil);
	}

}
