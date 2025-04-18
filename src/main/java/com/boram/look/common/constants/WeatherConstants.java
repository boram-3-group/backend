package com.boram.look.common.constants;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherConstants {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final List<String> BASE_TIME_LIST = List.of(
            "0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"
    );

    public static final String KMA_TEMP_3H = "T3H";
    public static final String KMA_PRECIPITATION_PROBABILITY = "SKY";
    public static final String KMA_PRECIPITATION_TYPE = "PTY";
    public static final String KMA_SKY_CONDITION = "POP";
}
