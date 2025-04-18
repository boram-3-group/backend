package com.boram.look.service.s3;

import com.boram.look.domain.s3.FileMetadata;
import com.boram.look.domain.s3.repository.FileMetadataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetadataService {
    private final FileMetadataRepository metadataRepository;

    @Transactional
    public FileMetadata saveFileMetadata(UUID userId, MultipartFile file, String key) {
        FileMetadata metadata = FileMetadata.builder()
                .size(file.getSize())
                .originalFilename(file.getOriginalFilename())
                .pathKey(key)
                .uploaderUserId(userId)
                .uploadedAt(LocalDateTime.now())
                .contentType(file.getContentType())
                .build();
        return metadataRepository.save(metadata);
    }

    @Transactional
    public String deleteFileMetadata(Long fileId) {
        Optional<FileMetadata> metadata = metadataRepository.findById(fileId);
        metadata.ifPresent(data -> metadataRepository.deleteById(data.getId()));
        return metadata.get().getPathKey();
    }

    @Transactional(readOnly = true)
    public String getFilePathKey(Long fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId).orElseThrow(EntityNotFoundException::new);
        return metadata.getPathKey();
    }

    @Transactional(readOnly = true)
    public FileMetadata getFileMetadata(Long fileId) {
        return metadataRepository.findById(fileId).orElseThrow(EntityNotFoundException::new);
    }

}
