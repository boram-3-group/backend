package com.boram.look.domain.weather;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Forecast", description = "예보 DTO")
public class Forecast {
    @Schema(description = "시간 hh:mm")
    private String time;      // "0900"
    @Schema(description = "기온")
    private float temperature;  // T3H
    @Schema(description = "구름낀 정도")
    private int sky;          // SKY
    @Schema(description = "강수 유형")
    private int pty;          // PTY
    @Schema(description = "강수 확률")
    private int pop;          // POP
    @Schema(description = "아이콘 이미지 url")
    private String icon;      // ☀️, 🌧 등
    @Schema(description = "날씨에 대한 설명")
    private String message;   // 텍스트 메시지
}
