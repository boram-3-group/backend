package com.boram.look.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private final SesClient sesClient;
    private static final Duration EXPIRE_TIME = Duration.ofMinutes(4);

    public void sendVerificationCode(String email) {
        String code = generateCode();
        redisTemplate.opsForValue().set("email_verify:" + email, code, EXPIRE_TIME);

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder()
                        .toAddresses(email)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder().data("이메일 인증 번호입니다.").charset("UTF-8").build())
                        .body(Body.builder()
                                .text(Content.builder().data("인증번호는 " + code + "입니다.").charset("UTF-8").build())
                                .build())
                        .build())
                .source("noreply@ondolook.awsapps.com")
                .build();

        sesClient.sendEmail(request);
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get("email_verify:" + email);
        return savedCode.isEmpty() && savedCode.equals(inputCode);
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드는 " + code + "입니다. 3분 내에 입력해주세요.");
        //mailSender.send(message);
    }
}
