package com.boram.look.api.dto.user;

import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.Agreed;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Schema(name = "UserDto", description = "유저 DTO")
public class UserDto {

    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    @Schema(name = "UserDto.Save", description = "회원가입, 정보 수정 요청 DTO")
    public static class Save {
        @Schema(description = "username - 회원 로그인시 사용되는 Id")
        @Size(min = 6, max = 12, message = "아이디는 6~12자로 해주세요.")
        private String username;
        @Schema(description = "비밀번호")
        @Size(min = 8, max = 14, message = "비밀번호는 8~14자로 해주세요.")
        private String password;

        @Schema(description = "성별")
        private Gender gender;
        @Schema(description = "생일")
        private LocalDate birthDate;

        @Schema(description = "닉네임 - 외부로 보여질 이름")
        @Size(min = 1, max = 7, message = "닉네임은 1~7자로 해주세요.")
        @Pattern(regexp = "^[^\\s]+$", message = "닉네임에는 공백을 포함할 수 없습니다.")
        private String nickname;

        @Schema(description = "이메일")
        @Email
        private String email;

        @Schema(description = "이메일 인증 코드")
        private String verificationCode;

        @Schema(description = "이용약관 동의 여부")
        private Boolean agreedToTerms;
        @Schema(description = "개인정보처리방침 동의 여부")
        private Boolean agreedToPrivacy;
        @Schema(description = "마케팅 수신 동의 여부")
        private Boolean agreedToMarketing;
        @Schema(description = "위치정보 활용 동의 여부")
        private Boolean agreedToLocation;

        public User toEntity(String encodedPassword, Agreed agreed) {
            return User.builder()
                    .password(encodedPassword)
                    .username(this.username)
                    .gender(this.gender)
                    .nickname(this.nickname)
                    .birthDate(this.birthDate)
                    .agreed(agreed)
                    .email(this.email)
                    .registrationId(OAuth2RegistrationId.NONE)
                    .build();
        }

    }


    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    @Schema(name = "UserDto.Profile", description = "유저 프로필 DTO")
    public static class Profile {
        @Schema(description = "id - DB에서 발급한 회원의 고유키")
        private String id;
        @Schema(description = "username - 회원 로그인시 사용되는 Id")
        private String username;
        @Schema(description = "닉네임 - 외부로 보여질 이름")
        private String nickname;
        @Schema(description = "생일")
        private LocalDate birthDate;
        @Schema(description = "성별")
        private Gender gender;
        @Schema(description = "OAUTH 출처")
        private OAuth2RegistrationId registrationId;
    }

    @Schema(name = "UserDto.PasswordResetEmail")
    public record PasswordResetEmail(
            @Schema(description = "로그인시 유저 ID")
            String username,
            @Schema(description = "유저 이메일")
            String email
    ) {
    }

    @Schema(name = "UserDto.VerifyPasswordEmail")
    public record VerifyPasswordEmail(
            @Schema(description = "로그인시 유저 ID")
            String username,
            @Schema(description = "유저 이메일")
            String code
    ) {
    }

    @Schema(name = "UserDto.PasswordResetRequest", description = "패스워드 변경 DTO")
    public record PasswordResetRequest(
            @Schema(description = "이메일로부터 진입한 사용자의 쿼리스트링에 있는 verification code")
            String verificationCode,
            @Schema(description = "로그인 id")
            String username,
            @Schema(description = "새로운 비밀번호")
            String newPassword
    ) {
    }

    @Schema(name = "UserDto.JoinEmailVerify")
    public record JoinEmailVerify(
            String email,
            String code
    ) {
    }
}
