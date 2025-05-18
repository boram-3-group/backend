package com.boram.look.api.dto.weather;

import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.weather.forecast.entity.Forecast;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "Forecast", description = "예보 DTO")
public class ForecastDto {
    @Schema(description = "예보날짜 yyyymmdd")
    private String date;      // "0900"
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
    @Schema(description = "아이콘 이름")
    private ForecastIcon icon;      // ☀️, 🌧 등
    @Schema(description = "아이콘 번호")
    private Integer iconNumber;      // ☀️, 🌧 등
    @Schema(description = "날씨에 대한 설명")
    private String iconMessage;   // 텍스트 메시지

    public void withForecastIcon(ForecastIcon icon) {
        this.icon = icon;
        this.iconNumber = icon.getIconNumber();
        this.iconMessage = icon.getIconMessage();
    }

    public Forecast toEntity(Region region) {
        return Forecast.builder()
                .date(this.date)
                .time(this.time)
                .pop(this.pop)
                .sky(this.sky)
                .pty(this.pty)
                .temperature(this.temperature)
                .region(region)
                .icon(this.icon)
                .iconMessage(this.iconMessage)
                .iconNumber(this.iconNumber)
                .build();
    }

    public LocalDateTime toDateTime() {
        String dateStr = String.valueOf(this.date);
        String timeStr = String.valueOf(this.time);

        return LocalDateTime.of(
                Integer.parseInt(dateStr.substring(0, 4)),
                Integer.parseInt(dateStr.substring(4, 6)),
                Integer.parseInt(dateStr.substring(6, 8)),
                Integer.parseInt(timeStr.substring(0, 2)),
                Integer.parseInt(timeStr.substring(2, 4))
        );
    }
}
