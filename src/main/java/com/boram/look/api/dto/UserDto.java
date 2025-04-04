package com.boram.look.api.dto;

import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.StyleType;
import com.boram.look.domain.user.entity.ThermoSensitivity;
import com.boram.look.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

public class UserDto {

    @Builder
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Save {
        private String username;
        private String password;
        private List<Integer> styleTypeIds;
        private Integer thermoId;
        private Gender gender;

        public User toEntity(ThermoSensitivity sensitivity, Set<StyleType> styleTypes) {
            return User.builder()
                    .password(this.password)
                    .username(this.username)
                    .gender(this.gender)
                    .thermoSensitivity(sensitivity)
                    .styleTypes(styleTypes)
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
