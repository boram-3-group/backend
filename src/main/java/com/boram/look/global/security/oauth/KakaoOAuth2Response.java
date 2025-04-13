package com.boram.look.global.security.oauth;

import com.boram.look.domain.user.constants.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoOAuth2Response extends OAuth2Response {

    private String id;
    private LocalDateTime connectedAt;
    private Map<String, Object> properties;
    private KakaoAccount kakaoAccount;

    @Override
    public String id() {
        return getStringValue(this.id);
    }

    @Override
    public String nickname() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String email() {
        return null;
    }

    @Override
    public String phoneNumber() {
        return null;
    }

    @Override
    public String profileImageUrl() {
        return null;
    }

    @Override
    public Gender gender() {
        return Gender.NONE;
    }

    @Override
    public OAuth2RegistrationId registrationId() {
        return OAuth2RegistrationId.KAKAO;
    }

    /**
     * 주어진 속성 맵으로부터 KakaoOAuth2Response 인스턴스를 생성합니다.
     *
     * @param attributes 응답 속성을 포함하는 맵
     * @return 새로운 KakaoOAuth2Response 인스턴스
     */
    public static KakaoOAuth2Response from(Map<String, Object> attributes) {
        return new KakaoOAuth2Response(
                attributes.get("sub").toString(),
                LocalDateTime.now(),
                attributes,
                new KakaoAccount()
        );
    }

    public static class KakaoAccount {
    }
}
