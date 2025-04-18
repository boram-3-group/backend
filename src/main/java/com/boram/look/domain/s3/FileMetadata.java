package com.boram.look.domain.s3;

import com.boram.look.api.dto.FileDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFilename;
    @Column(name = "path_key")
    private String pathKey; // S3 key (e.g., uploads/abc.jpg)
    @Column(name = "content_type")
    private String contentType;

    private Long size;

    @Column(name = "uploader_user_id")
    private UUID uploaderUserId; // 또는 User 객체

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public FileDto toDto(String presignedUrl) {
        return FileDto.builder()
                .fileId(this.id)
                .originalFilename(this.originalFilename)
                .presignedUrl(presignedUrl)
                .contentType(this.contentType)
                .size(this.size)
                .uploadedAt(this.uploadedAt)
                .build();
    }
}
