package com.boram.look.api.controller;

import com.boram.look.api.dto.notice.NoticeDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.notice.NoticeService;
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
