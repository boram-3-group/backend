package com.boram.look.domain.weather;

import lombok.Data;

@Data
public class Forecast {
    private String time;      // "0900"
    private int temperature;  // T3H
    private int sky;          // SKY
    private int pty;          // PTY
    private int pop;          // POP
    private String icon;      // ☀️, 🌧 등
    private String message;   // 텍스트 메시지

}
