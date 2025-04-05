package com.boram.look.api.dto;

import com.boram.look.domain.Action;
import com.boram.look.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

public class SensitivityDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class Edit {
        private Integer id;
        private String content;
        private Action action;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public static class Get {
        private Integer id;
        private String content;
        private UserDto.Profile createUser;
        private UserDto.Profile updateUser;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


}
