package com.boram.look.api.dto;

import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
        private String username;
        @Schema(description = "비밀번호")
        private String password;
        @Schema(description = "성별")
        private Gender gender;
        @Schema(description = "생일")
        private LocalDate birthDate;
        @Schema(description = "닉네임 - 외부로 보여질 이름")
        private String nickname;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .password(encodedPassword)
                    .username(this.username)
                    .gender(this.gender)
                    .nickname(this.nickname)
                    .birthDate(this.birthDate)
                    .build();
        }

    }


    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    @Schema(name="UserDto.Profile", description = "유저 프로필 DTO")
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
    }

}
