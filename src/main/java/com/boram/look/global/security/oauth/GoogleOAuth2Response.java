package com.boram.look.global.security.oauth;

import com.boram.look.domain.user.constants.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class GoogleOAuth2Response extends OAuth2Response {

    private String id;
    private LocalDateTime connectedAt;
    private Map<String, Object> properties;
    private GoogleAccount googleAccount;

    @Override
    public String id() {
        return getStringValue(this.id);
    }

    @Override
    public String nickname() {
        return this.googleAccount.getProfile().getNickname();
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String email() {
        return this.googleAccount.getEmail();
    }

    @Override
    public String phoneNumber() {
        return null;
    }

    @Override
    public String profileImageUrl() {
        return this.googleAccount.getProfile().getProfileImageUrl();
    }

    @Override
    public Gender gender() {
        return Gender.NONE;
    }

    @Override
    public OAuth2RegistrationId registrationId() {
        return OAuth2RegistrationId.GOOGLE;
    }

    @SuppressWarnings("unchecked")
    public static GoogleOAuth2Response from(Map<String, Object> attributes) {
        return new GoogleOAuth2Response(
                attributes.get("sub").toString(),
                LocalDateTime.now(),
                attributes,
                new GoogleAccount()
        );
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoogleAccount {

        private Boolean profileNicknameNeedsAgreement;
        private Boolean profileImageNeedsAgreement;
        private Profile profile;
        private Boolean hasEmail;
        private Boolean emailNeedsAgreement;
        private Boolean isEmailValid;
        private Boolean isEmailVerified;
        private String email;

        @SuppressWarnings("unchecked")
        public static GoogleAccount from(Map<String, Object> attributes) {
            return new GoogleAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("profile_image_needs_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    String.valueOf(attributes.get("email"))
            );
        }

        @Getter
        @AllArgsConstructor
        public static class Profile {

            private String nickname;
            private String thumbnailImageUrl;
            private String profileImageUrl;
            private Boolean isDefaultImage;
            private Boolean isDefaultNickname;

            public static Profile from(Map<String, Object> attributes) {
                return new Profile(
                        getStringValue(attributes.get("nickname")),
                        getStringValue(attributes.get("thumbnail_image_url")),
                        getStringValue(attributes.get("profile_image_url")),
                        Boolean.valueOf(String.valueOf(attributes.get("is_default_image"))),
                        Boolean.valueOf(String.valueOf(attributes.get("is_default_nickname")))
                );
            }
        }
    }
}
