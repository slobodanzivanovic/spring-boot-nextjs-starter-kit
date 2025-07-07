package com.slobodanzivanovic.backend.model.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slobodanzivanovic.backend.model.auth.entity.UserEntity;
import com.slobodanzivanovic.backend.model.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.FilenameUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Slobodan Zivanovic
 */
@Entity
@Table(name = "uploaded_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UploadedFile extends BaseEntity {

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "size")
	private Long size;

	@Column(name = "original_file_name")
	private String originalFileName;

	@Column(name = "extension")
	private String extension;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt;

	@ManyToOne
	@JsonIgnore
	private UserEntity user;

	public UploadedFile(String originalFileName, Long size, UserEntity user) {
		this.originalFileName = originalFileName;
		this.size = size;
		this.user = user;
		this.extension = FilenameUtils.getExtension(originalFileName);
	}

	public void onUploaded(String url) {
		this.url = url;
		this.uploadedAt = LocalDateTime.now();
	}

	public String buildPath(String... path) {
		StringBuilder sb = new StringBuilder();
		sb.append("user:").append(user.getId()).append("/");
		for (String p : path) {
			sb.append(p).append("/");
		}
		sb.append(UUID.randomUUID());
		sb.append(".").append(extension);
		return sb.toString();
	}

}
