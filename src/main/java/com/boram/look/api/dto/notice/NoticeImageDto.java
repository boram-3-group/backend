package com.boram.look.api.dto.notice;

import com.boram.look.api.dto.FileDto;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NoticeImageDto {

    public record Push(
            List<MultipartFile> images,
            String title,
            String description
    ) {
    }

    @Builder
    public record Get(
            Long id,
            String title,
            String description,
            List<FileDto> images
    ) {
    }
}
