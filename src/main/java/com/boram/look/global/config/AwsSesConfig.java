package com.boram.look.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsSesConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String username;
    @Value("${cloud.aws.credentials.secret-key}")
    private String password;

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                username,
                                password
                        )
                ))
                .build();
    }
}
