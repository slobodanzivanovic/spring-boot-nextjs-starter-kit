package com.slobodanzivanovic.backend.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author Slobodan Zivanovic
 */
@Configuration
@Getter
public class S3Config {

	@Value("${app.s3.bucket-name}")
	private String bucketName;

	@Value("${app.s3.access-key}")
	private String accessKey;

	@Value("${app.s3.secret-key}")
	private String secretKey;

	@Value("${app.s3.base-url}")
	private String baseUrl;

	@Value("${app.s3.region}")
	private String region;

	@Value("${app.s3.storage-class}")
	private String storageClass;

	@Bean
	public AwsCredentials awsCredentials() {
		return AwsBasicCredentials.create(accessKey, secretKey);
	}

	@Bean
	public S3Client s3Client(AwsCredentials awsCredentials) {
		return S3Client.builder()
				.endpointOverride(URI.create(baseUrl))
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
				.forcePathStyle(true)
				.build();
	}

}
