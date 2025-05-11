package com.boram.look.api.controller;

import com.boram.look.api.dto.notification.FcmTokenDto;
import com.boram.look.api.dto.notification.UserNotificationSettingDto;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.notification.FcmTokenService;
import com.boram.look.service.notification.UserNotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@RestController
public class NotificationController {

    private final FcmTokenService fcmTokenService;
    private final UserNotificationSettingService userSettingService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFcmToken(
            FcmTokenDto.Save dto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        fcmTokenService.saveFcmToken(principalDetails, dto);
        return ResponseEntity.created(URI.create("none")).build();
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/notification-setting")
    public ResponseEntity<?> saveNotificationSetting(
            UserNotificationSettingDto.Save dto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userSettingService.saveNotificationSetting(principalDetails, dto);
        return ResponseEntity.created(URI.create("none")).build();
    }


}
