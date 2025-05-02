package com.boram.look.service.auth;

import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.auth.PasswordResetCode;
import com.boram.look.domain.auth.constants.VerificationConstants;
import com.boram.look.global.ex.EmailNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private final SesClient sesClient;

    public void sendVerificationCode(String email, String key, String type) {
        String code = generateCode();
        String subject = "Ondolook 이메일 인증 번호";
        String content = "인증번호는 " + code + "입니다.";
        redisTemplate.opsForValue().set(VerificationConstants.AUTH_VERIFICATION_KEY_PREFIX + type + code, key, VerificationConstants.AUTH_VERIFICATION_EXPIRE_TIME);
        this.sendEmail(email, subject, content);
    }

    public String verifyCode(String type, String inputCode) {
        return redisTemplate.opsForValue().get(VerificationConstants.AUTH_VERIFICATION_KEY_PREFIX + type + inputCode);
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

    public void saveEmailVerificationHistory(String email) {
        redisTemplate.opsForValue().set(VerificationConstants.EMAIL_HISTORY_KEY + email, email, VerificationConstants.JOIN_EMAIL_VERIFICATION_EXPIRE_TIME);
    }


    public void isVerifiedEmail(String email) {
        String loadedEmail = redisTemplate.opsForValue().get(VerificationConstants.EMAIL_HISTORY_KEY + email);
        if (!Objects.equals(loadedEmail, email)) {
            throw new EmailNotVerifiedException(email);
        }
    }

}
