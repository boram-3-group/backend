package com.boram.look.api.controller;

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

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("uploadDir") String uploadDir
    ) {
        String url = s3FileService.upload(file, uploadDir);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> upload(@PathVariable String key) {
        s3FileService.delete(key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{key}/url")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String key) {
        String url = s3FileService.generatePresignedUrl(key, 10);
        return ResponseEntity.ok(url);
    }
}
