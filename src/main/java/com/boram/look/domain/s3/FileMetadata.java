package com.boram.look.domain.s3;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private String uploaderUserId; // 또는 User 객체

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
