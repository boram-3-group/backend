package com.boram.look.api.controller;

import com.boram.look.api.dto.UserDto;
import com.boram.look.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> joinUser(@RequestBody UserDto.Save dto) {
        log.info("UserController.joinUser is called.\ndto:{}", dto);
        userService.joinUser(dto);
        return ResponseEntity.created(URI.create("asdf")).body("회원 가입 완료");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto.Save dto
    ) {
        log.info("UserController.updateUser is called.\nuserId:{}\ndto:{}", userId, dto);
        userService.updateUser(userId, dto);
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        UserDto.Profile profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 삭제 완료");
    }

}
