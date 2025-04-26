package com.boram.look.domain.weather.air;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AirQualityCache {
    private Integer seoul;
    private Integer gyeonggi;
    private Integer incheon;
    private Integer gangwon;
    private Integer daejeon;
    private Integer sejong;
    private Integer chungbuk;
    private Integer chungnam;
    private Integer gwangju;
    private Integer jeonbuk;
    private Integer jeonnam;
    private Integer busan;
    private Integer daegu;
    private Integer ulsan;
    private Integer gyeongbuk;
    private Integer gyeongnam;
    private Integer jeju;
    //2025-04-23 16:00
    private String dataTime;
    private String dataGubun;
    private String itemCode;
}
