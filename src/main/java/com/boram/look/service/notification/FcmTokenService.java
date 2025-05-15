package com.boram.look.service.notification;

import com.boram.look.api.dto.notification.FcmTokenDto;
import com.boram.look.domain.notification.FcmToken;
import com.boram.look.domain.notification.repository.FcmTokenRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.authentication.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveFcmToken(PrincipalDetails principalDetails, FcmTokenDto.Save dto) {
        User loginUser = principalDetails.getUser();
        FcmToken fcmToken = fcmTokenRepository.findByUserIdAndFcmToken(loginUser.getId(), dto.fcmToken())
                .orElseGet(() -> FcmToken.builder()
                        .userId(loginUser.getId())
                        .fcmToken(dto.fcmToken())
                        .build()
                );
        fcmTokenRepository.save(fcmToken);
    }

}
