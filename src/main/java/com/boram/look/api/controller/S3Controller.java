package com.boram.look.api.controller;

import com.boram.look.service.s3.FileMetadataService;
import com.boram.look.service.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class S3Controller {

    private final S3FileService s3FileService;
    private final FileMetadataService fileMetadataService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("uploadDir") String uploadDir
    ) {
        String url = s3FileService.upload(file, uploadDir);
        // fileMetadataService.saveFileMetadata("asdf", file, url);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> delete(@PathVariable Long fileId) {
        String pathKey = fileMetadataService.deleteFileMetadata(fileId);
        s3FileService.delete(pathKey);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{fileId}/url")
    public ResponseEntity<?> getPresignedUrl(@PathVariable Long fileId) {
        String pathKey = fileMetadataService.getFilePathKey(fileId);
        String presignedUrl = s3FileService.generatePresignedUrl(pathKey, 10);
        return ResponseEntity.ok(presignedUrl);
    }
}
