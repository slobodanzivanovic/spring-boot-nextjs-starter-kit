package com.slobodanzivanovic.backend.repository.storage;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.slobodanzivanovic.backend.model.storage.entity.UploadedFile;

/**
 * @author Slobodan Zivanovic
 */
@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, UUID> {
}
