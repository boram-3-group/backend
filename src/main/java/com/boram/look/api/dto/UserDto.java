package com.boram.look.api.dto;

import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class UserDto {

    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Save {
        private String username;
        private String password;
        private Gender gender;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .password(encodedPassword)
                    .username(this.username)
                    .gender(this.gender)
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
        private String password;
        private List<Integer> styleTypeIds;
        private Integer thermoId;
        private Gender gender;
    }

}
