package com.boram.look.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static LocalDateTime roundToNearestHour(LocalDateTime now) {
        int minute = now.getMinute();
        if (minute < 30) {
            return now.withMinute(0).withSecond(0).withNano(0);
        } else {
            return now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
    }

    public static String formatTimeToString(LocalDateTime dateTime, DateTimeFormatter outputFormat) {
        return dateTime.format(outputFormat);

    }
}
