package com.boram.look.domain.air;

import java.util.Arrays;

public enum AirQualityGrade {
    GOOD("좋음"),
    MODERATE("보통"),
    BAD("나쁨");

    private final String label;

    AirQualityGrade(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AirQualityGrade fromKorean(String text) {
        return Arrays.stream(values())
                .filter(grade -> grade.label.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown grade: " + text));
    }
}
