package com.boram.look.api.dto.notice;

import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.notice.Notice;
import com.boram.look.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NoticeDto {


    @AllArgsConstructor
    @Builder
    @Getter
    public static class Get {
        private Long id;
        private String title;
        private String content;
        private UserDto.Profile writerProfile;
        private LocalDateTime createdAt;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    public static class Save {
        private String title;
        private String content;

        public Notice toEntity(User loginUser) {
            return Notice.builder()
                    .title(this.title)
                    .content(this.content)
                    .writer(loginUser)
                    .build();
        }
    }
}
