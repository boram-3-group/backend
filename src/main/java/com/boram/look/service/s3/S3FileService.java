package com.boram.look.service.s3;

import com.boram.look.api.dto.PresignedUrlDto;
import com.boram.look.domain.s3.FileMetadata;
import com.boram.look.domain.s3.repository.FileMetadataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileMetadataRepository metadataRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String keyPrefix) {
        String filename = keyPrefix + UUID.randomUUID();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    public PresignedUrlDto generatePresignedUrl(String key, int expireMinutes, Long fileId) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expireMinutes))
                .getObjectRequest(request)
                .build();
        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
        return PresignedUrlDto.builder()
                .fileId(fileId)
                .presignedUrl(presignedUrl)
                .build();
    }


    @Transactional
    public void saveFileMetadata(String userId, MultipartFile file, String key) {
        FileMetadata metadata = FileMetadata.builder()
                .size(file.getSize())
                .originalFilename(file.getOriginalFilename())
                .pathKey(key)
                .uploaderUserId(userId)
                .uploadedAt(LocalDateTime.now())
                .contentType(file.getContentType())
                .build();
        metadataRepository.save(metadata);
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
}
