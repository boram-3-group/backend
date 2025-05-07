package com.boram.look.api.controller;

import com.boram.look.api.dto.notice.NoticeDto;
import com.boram.look.api.dto.notice.NoticeImageDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.notice.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notice")
@Slf4j
@Schema(
        name = "공지사항 컨트롤러",
        description = """
                API Flow
                1. 글 작성
                  1) image 추가, 설명 입력후 업로드 - post, /api/v1/notice/image
                  2) 반환된 image key를 notice 작성시에 포함하여 전송 - post, /api/v1/notice
                                
                2. 글 수정
                   1)
                                
                3. 글 삭제
                   1)             
                """
)
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> writeNotice(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            NoticeDto.Save dto
    ) {
        Long noticeId = noticeService.insertNotice(principalDetails, dto);
        return ResponseEntity.created(URI.create("/api/v1/notice/" + noticeId)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/image")
    public ResponseEntity<?> pushNoticeImages(
            @ModelAttribute NoticeImageDto.Push dto,
            PrincipalDetails principalDetails
    ) {
        NoticeImageDto.Get response = noticeService.pushNoticeImages(principalDetails, dto);
        return ResponseEntity.created(URI.create("none")).body(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/image/{notice-image-id}")
    public ResponseEntity<?> deleteNoticeImages(
            @PathVariable(name = "notice-image-id") Long noticeImageId,
            PrincipalDetails principalDetails
    ) {
        //TODO 이미지 삭제 기능
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{notice-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateNotice(
            @PathVariable(name = "notice-id") Long noticeId,
            NoticeDto.Save dto
    ) {
        NoticeDto.Get result = noticeService.updateNotice(noticeId, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{notice-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteNotice(
            @PathVariable(name = "notice-id") Long noticeId
    ) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.created(URI.create("/api/v1/notice/" + noticeId)).build();
    }

    @GetMapping("/{notice-id}")
    public ResponseEntity<?> getNoticeDetail(@PathVariable(name = "notice-id") Long noticeId) {
        NoticeDto.Get noticeDto = noticeService.getNoticeDetail(noticeId);
        return ResponseEntity.ok(noticeDto);
    }

    @GetMapping
    public ResponseEntity<?> getNoticeList(Pageable pageable) {
        Page<NoticeDto.Get> notices = noticeService.getAllNotice(pageable);
        return ResponseEntity.ok(notices);
    }

}
