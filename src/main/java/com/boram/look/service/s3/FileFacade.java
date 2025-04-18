package com.boram.look.service.s3;

import com.boram.look.api.dto.FileDto;
import com.boram.look.domain.s3.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileFacade {

    private final S3FileService s3FileService;
    private final FileMetadataService metadataService;

    public FileDto upload(MultipartFile file, String uploadDir, UUID uploaderId) {
        String url = s3FileService.upload(file, uploadDir);
        FileMetadata metadata = metadataService.saveFileMetadata(uploaderId, file, url);
        String presignedUrl = s3FileService.generatePresignedUrl(metadata.getPathKey(), 50);
        return metadata.toDto(presignedUrl);
    }

    public void delete(Long fileId, Long requesterId) {
        FileMetadata meta = metadataService.getFileMetadata(fileId);

        if (!meta.getUploaderUserId().equals(requesterId)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }

        s3FileService.delete(meta.getPathKey());
        metadataService.deleteFileMetadata(fileId);
    }

    public FileDto buildFileDtoByFileId(Long fileId) {
        FileMetadata metadata = metadataService.getFileMetadata(fileId);
        String presignedUrl = s3FileService.generatePresignedUrl(metadata.getPathKey(), 50);
        return metadata.toDto(presignedUrl);
    }

    public FileDto buildFileDto(FileMetadata metadata) {
        String presignedUrl = s3FileService.generatePresignedUrl(metadata.getPathKey(), 50);
        return metadata.toDto(presignedUrl);
    }
}
