package com.boram.look.domain.weather;

public class WeatherMapper {
    public static String getWeatherIcon(int pty, int sky) {
        if (pty == 1 || pty == 4) return "🌧"; // 비 or 소나기
        if (pty == 2 || pty == 3) return "🌨"; // 비/눈, 눈
        if (sky == 1) return "☀️"; // 맑음
        if (sky == 3) return "⛅"; // 구름 많음
        return "☁️"; // 흐림
    }

    public static String getMessage(int pty, int sky) {
        if (pty == 1 || pty == 4) return "비가 오고 있어요";
        if (pty == 2 || pty == 3) return "눈이 내리고 있어요";
        if (sky == 1) return "맑은 날씨입니다";
        if (sky == 3) return "구름이 많아요";
        return "흐린 날씨입니다";
    }
}
