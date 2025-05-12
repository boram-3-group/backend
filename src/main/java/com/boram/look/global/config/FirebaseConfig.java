package com.boram.look.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        String secretJson = getSecretFromAws();
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(secretJson.getBytes(StandardCharsets.UTF_8))
                    ))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase successfully initialized from AWS Secrets Manager.");
        } catch (Exception e) {
            log.error("Firebase initialization failed", e);
        }
    }

    private String getSecretFromAws() {
        String secretName = "firebase/sdk-key";
        Region region = Region.of("us-east-1");

        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build()) {

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            return response.secretString(); // JSON 문자열 그대로 반환
        }
    }
}
