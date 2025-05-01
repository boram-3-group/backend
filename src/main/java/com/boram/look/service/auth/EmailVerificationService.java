package com.boram.look.service.auth;

import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.auth.PasswordResetCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private final SesClient sesClient;
    private static final Duration EXPIRE_TIME = Duration.ofMinutes(4);

    public void sendVerificationCode(String email, String key, String type) {
        String code = generateCode();
        String subject = "Ondolook 이메일 인증 번호";
        String content = "인증번호는 " + code + "입니다.";
        redisTemplate.opsForValue().set("verify:" + type + ":" + code, key, EXPIRE_TIME);
        this.sendEmail(email, subject, content);
    }

    public String verifyCode(String type, String inputCode) {
        return redisTemplate.opsForValue().get("verify:" + type + ":" + inputCode);
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public void sendEmail(String to, String subject, String content) {
        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder()
                        .toAddresses(to)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).charset("UTF-8").build())
                        .body(Body.builder()
                                .text(Content.builder().data(content).charset("UTF-8").build())
                                .build())
                        .build())
                .source("noreply@ondolook.awsapps.com")
                .build();
        sesClient.sendEmail(request);
    }

}
