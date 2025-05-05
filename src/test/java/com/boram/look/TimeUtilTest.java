package com.boram.look;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TimeUtilTest {

    @Test
    public void getNearestPastThreeHour() {
        LocalDateTime time = LocalDateTime.now();
        time = time.withHour(21).withMinute(0);
        int currentHour = time.getHour();

        // 3시간 단위로 내림
        int forecastHour = (currentHour / 3) * 3;
        System.out.println(forecastHour);
        int currentMinute = time.getMinute();

        LocalDateTime forecastTime = time.withHour(forecastHour).withMinute(0).withSecond(0).withNano(0);
        if (currentMinute <= 11 && currentHour != 0) {
            forecastHour -= 3;
            forecastTime = time.withHour(forecastHour).withMinute(0).withSecond(0).withNano(0);
        } else if (currentHour == 0 && currentMinute <= 11) {
            forecastTime = time.minusDays(1).withHour(21).withMinute(0).withSecond(0).withNano(0);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        System.out.println(forecastTime.format(formatter));
    }


    @Test
    public void getNearestFetchThreeHour() {
        LocalDateTime time = LocalDateTime.now();
        time = time.withHour(0);
        int currentHour = time.getHour();

        // 3시간 단위로 내림
        int forecastHour = (currentHour / 3) * 3;
        System.out.println("fetch time: " + forecastHour);

        LocalDateTime forecastTime = time.withHour(forecastHour).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        System.out.println(forecastTime.format(formatter));
    }
}
