package com.boram.look.domain.s3.repository;

import com.boram.look.domain.s3.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findByPathKey(String pathKey);
}
