package com.boram.look.api.controller;

import com.boram.look.api.dto.notification.FcmTokenDto;
import com.boram.look.api.dto.notification.NotificationDto;
import com.boram.look.api.dto.notification.UserNotificationSettingDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.notification.FcmTokenService;
import com.boram.look.service.notification.UserNotificationSettingService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@RestController
@Tag(
        name = "fcm 알림 컨트롤러",
        description = """
                !!! API 호출 플로우
                  1. 로그인시 또는 유저 알림 호출 전에 fcm token을 저장한다. - 기존 프론트앤드 로직 따름
                    endpoint: /api/v1/fcm-token
                    method: post
                  
                  2. 알림 세팅 저장 요청
                    endpoint: /api/v1/notification-setting
                    method: post
                    
                  3. 알림 세팅 삭제
                    endpoint: /api/v1/notification-setting
                    method: delete
                    
                  4. noti test: 알림 테스트용 api
                """
)
public class NotificationController {

    private final FcmTokenService fcmTokenService;
    private final UserNotificationSettingService userSettingService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFcmToken(
            @RequestBody FcmTokenDto.Save dto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        fcmTokenService.saveFcmToken(principalDetails, dto);
        return ResponseEntity.created(URI.create("none")).build();
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/notification-setting")
    public ResponseEntity<?> saveNotificationSetting(
            @RequestBody UserNotificationSettingDto.Save dto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userSettingService.saveNotificationSetting(principalDetails, dto);
        return ResponseEntity.created(URI.create("none")).build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/notification-setting")
    public ResponseEntity<?> saveNotificationSetting(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userSettingService.deleteNotificationSetting(principalDetails);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/notification-setting")
    public ResponseEntity<?> getNotificationSetting(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        UserNotificationSettingDto.Get dto = userSettingService.getNotificationSetting(principalDetails);
        return ResponseEntity.ok().body(dto);
    }


    @Hidden
    @PostMapping("/noti-test")
    public ResponseEntity<?> notiTest(@RequestBody NotificationDto dto) {
        userSettingService.sendNotification(dto);
        return ResponseEntity.ok().build();
    }


}
