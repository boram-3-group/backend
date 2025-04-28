package com.boram.look.api.controller;

import com.boram.look.api.dto.user.BookmarkDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.user.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(
            summary = "북마크 추가 API",
            description = "로그인 필수, outfit_image_id를 입력하면 북마크 등록됩니다."
    )
    @PreAuthorize("authentication.isAuthenticated()")
    @PostMapping("/{outfit_image_id}")
    public ResponseEntity<?> insertBookmark(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Parameter(name = "outfit_image_id", description = "추천 의상 이미지 id") @PathVariable(name = "outfit_image_id") Long outfitImageId
    ) {
        BookmarkDto dto = bookmarkService.insertBookmark(principalDetails, outfitImageId);
        return ResponseEntity.created(URI.create("/api/v1/bookmark/")).body(dto);
    }


    @Operation(
            summary = "북마크 조회 API",
            description = "로그인 필수"
    )
    @PreAuthorize("authentication.isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllBookmarks(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        Page<BookmarkDto> bookmarkDtos = bookmarkService.getAllBookmarks(principalDetails, pageable);
        return ResponseEntity.ok(bookmarkDtos);
    }

    @Operation(
            summary = "북마크 추가 API",
            description = "로그인 필수, outfit_image_id를 입력하면 북마크 등록됩니다."
    )
    @PreAuthorize("authentication.isAuthenticated()")
    @DeleteMapping("/{outfit_image_id}")
    public ResponseEntity<?> deleteBookmark(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Parameter(name = "outfit_image_id", description = "추천 의상 이미지 id") @PathVariable(name = "outfit_image_id") Long outfitImageId
    ) {
        bookmarkService.deleteBookmark(principalDetails, outfitImageId);
        return ResponseEntity.noContent().build();
    }


}
