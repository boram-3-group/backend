package com.boram.look.service.auth;

import com.boram.look.api.dto.UserDto;
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

    public void sendVerificationCode(String email) {
        String code = generateCode();
        String subject = "Ondolook 이메일 인증 번호";
        String content = "인증번호는 " + code + "입니다.";
        redisTemplate.opsForValue().set("email_verify:" + email, code, EXPIRE_TIME);
        this.sendEmail(email, subject, content);
    }

    public void sendUsernameEmail(String email, String username) {
        String subject = "로그인 아이디입니다.";
        String content = "당신의 아이디는 " + username + "입니다.";
        this.sendEmail(email, subject, content);
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get("email_verify:" + email);
        return savedCode.isEmpty() && savedCode.equals(inputCode);
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

    public void sendResetPasswordEmail(UserDto.PasswordResetEmail dto) {
        String subject = "Ondolook 비밀번호 재설정";
        String content = "비밀번호를 재설정하시려면 아래의 링크를 눌러주세요.\n" +
                dto.callbackUrl();
        this.sendEmail(dto.email(), subject, content);
    }
}
