package com.boram.look.api.controller;

import com.boram.look.api.dto.user.UserDto;
import com.boram.look.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> joinUser(@RequestBody @Valid UserDto.Save dto) {
        log.info("UserController.joinUser is called.\ndto:{}", dto);
        String userId = userService.joinUser(dto);
        URI uri = URI.create("/api/v1/user/" + userId);
        return ResponseEntity.created(uri).body("회원 가입 완료");
    }

    @PreAuthorize("#userId == authentication.principal.user.id.toString()")
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

    @PreAuthorize("#userId == authentication.principal.user.id.toString()")
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

    @PreAuthorize("#userId == authentication.principal.user.id.toString()")
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "탈퇴할 유저 id") @PathVariable String userId
    ) {
        log.info("UserController.deleteUser is called.\nuserId:{}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 삭제 완료");
    }

    @Operation(summary = "유저네임 중복여부")
    @GetMapping("/username/{username}")
    public ResponseEntity<?> canUseUsername(
            @Parameter(description = "유저ID (로그인시 사용할)") @PathVariable String username
    ) {
        boolean isExist = userService.canUseUsername(username);
        String resultStr = "사용 가능한 유저 네임";
        if (isExist) {
            resultStr = "중복된 유저 네임";
        }
        return ResponseEntity.ok(resultStr);
    }

}
