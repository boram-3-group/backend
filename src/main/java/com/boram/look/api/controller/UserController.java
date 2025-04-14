package com.boram.look.api.controller;

import com.boram.look.api.dto.UserDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입")
    @PostMapping
    public ResponseEntity<?> joinUser(@RequestBody UserDto.Save dto) {
        log.info("UserController.joinUser is called.\ndto:{}", dto);
        userService.joinUser(dto);
        return ResponseEntity.created(URI.create("asdf")).body("회원 가입 완료");
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable String userId,
            @RequestBody UserDto.Save dto
    ) {
        log.info("UserController.updateUser is called.\nuserId:{}\ndto:{}", userId, dto);
        userService.updateUserProfile(userId, dto);
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "비밀번호 변경")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable String userId,
            @RequestBody String password
    ) {
        log.info("UserController.updateUser is called.\nuserId:{}\npassword:{}", userId, password);
        userService.updateUserPassword(userId, password);
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    @GetMapping("/{userId}")
    @Operation(summary = "userId - DB고유키 - 에 해당하는 회원의 프로필 조회")
    @ApiResponse(
            responseCode = "200",
            description = "회원 프로필",
            content = @Content(schema = @Schema(implementation = UserDto.Profile.class)))
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        log.info("UserController.getUserProfile is called.\nuserId:{}", userId);
        UserDto.Profile profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        log.info("UserController.deleteUser is called.\nuserId:{}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 삭제 완료");
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("test is called.\nprincipalDetails: {}", principalDetails);
        return ResponseEntity.ok("gogo");
    }
}
