package com.boram.look.api.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationDto {
    private String title;
    private String content;
    private String targetToken;
}