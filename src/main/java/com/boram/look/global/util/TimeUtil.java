package com.boram.look.global.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static LocalDateTime roundToNearestHour(LocalDateTime now) {
        int minute = now.getMinute();
        if (minute > 20) {
            return now.withMinute(0).withSecond(0).withNano(0);
        } else {
            return now.minusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
    }

    public static String formatTimeToString(LocalDateTime dateTime, DateTimeFormatter outputFormat) {
        return dateTime.format(outputFormat);
    }

    public static String buildYyyyMMddhhTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return time.format(formatter);
    }

    public static String getNearestPastThreeHour(LocalDateTime time) {
        int currentHour = time.getHour();

        // 3시간 단위로 내림
        int forecastHour = (currentHour / 3) * 3;

        // 예보 기준 시간 만들기
        LocalDateTime forecastTime = time.withHour(forecastHour).withMinute(0).withSecond(0).withNano(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return forecastTime.format(formatter);
    }

    public static String getToday6AmTime() {
        LocalDateTime todayAtSixAM = LocalDateTime.now()
                .with(LocalTime.of(6, 0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return todayAtSixAM.format(formatter);
    }

}
