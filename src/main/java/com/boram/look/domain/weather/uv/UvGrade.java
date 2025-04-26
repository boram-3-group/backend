package com.boram.look.domain.weather.uv;

import java.util.Arrays;

public enum UvGrade {
    GOOD("좋음"),

    MODERATE("보통"),

    BAD("나쁨"),

    VERY_BAD("매우나쁨");

    private final String label;

    UvGrade(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static UvGrade fromLabel(String text) {
        return Arrays.stream(values())
                .filter(grade -> grade.label.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown grade: " + text));
    }
}
