package com.slobodanzivanovic.backend.service.storage.impl;

import org.springframework.stereotype.Service;

import com.slobodanzivanovic.backend.config.S3Config;
import com.slobodanzivanovic.backend.service.storage.FileUploadService;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * @author Slobodan Zivanovic
 */
@Service
@AllArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

	private final S3Client s3Client;
	private final S3Config s3Config;

	public String uploadFile(String filePath, byte[] file) {
		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(s3Config.getBucketName())
				.storageClass(s3Config.getStorageClass())
				.key(filePath)
				.acl(ObjectCannedACL.PUBLIC_READ)
				.build();
		s3Client.putObject(request, RequestBody.fromBytes(file));
		try {
			GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(s3Config.getBucketName()).key(filePath)
					.build();
			return s3Client.utilities().getUrl(getUrlRequest).toURI().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
