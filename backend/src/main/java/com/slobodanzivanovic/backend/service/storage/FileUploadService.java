package com.slobodanzivanovic.backend.service.storage;

/**
 * @author Slobodan Zivanovic
 */
public interface FileUploadService {

	String uploadFile(String filePath, byte[] file);

}
