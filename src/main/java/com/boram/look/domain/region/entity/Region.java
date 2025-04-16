package com.boram.look.domain.region.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "region")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Region {

    @Id
    private String code; // 시군구 코드

    private String name; // 시군구 이름

    private double lat;  // 중심 위도

    private double lon;  // 중심 경도

    private int nx;      // 기상청 격자 X

    private int ny;      // 기상청 격자 Y



    @Lob
    @Column(columnDefinition = "TEXT")
    private String polygonText; // WKT 형식의 Polygon 문자열

}
