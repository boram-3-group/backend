package com.boram.look.domain.weather.uv;

import java.util.Arrays;

public enum UvGrade {
    LOW("낮음"),        // 0~3
    MODERATE("보통"),   // 3~6
    HIGH("높음"),       // 6~8
    VERY_HIGH("매우높음"),  // 8~10
    EXTREME("위험");    // 11+

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
