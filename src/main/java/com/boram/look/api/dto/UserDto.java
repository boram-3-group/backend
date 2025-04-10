package com.boram.look.api.dto;

import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

public class UserDto {

    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Save {
        private String username;
        private String password;
        private Gender gender;
        private LocalDate birthDate;
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
    public static class Profile {
        private String id;
        private String username;
        private String nickname;
        private LocalDate birthDate;
        private Gender gender;
    }

}
