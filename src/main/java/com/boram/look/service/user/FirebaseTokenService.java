package com.boram.look.service.user;

import com.boram.look.domain.user.entity.FirebaseToken;
import com.boram.look.domain.user.repository.FirebaseTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FirebaseTokenService {

    private final FirebaseTokenRepository firebaseTokenRepository;

    @Transactional
    public void insertToken(String token, String userId, String deviceId) {
        FirebaseToken firebaseToken = FirebaseToken.builder()
                .fcmToken(token)
                .userId(userId)
                .deviceId(deviceId)
                .build();
        firebaseTokenRepository.save(firebaseToken);
    }
}
