package com.boram.look.domain.region.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "region", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sido", "sgg"})
})
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "sgg": "11110",
    // "sido": "11",
    // "sidonm": "서울특별시",
    // "sggnm": "종로구",

    @Column(name = "sgg", nullable = false)
    private String sgg;
    @Column(name = "sggnm", nullable = false)
    private String sggnm;
    @Column(name = "sido", nullable = false)
    private String sido;
    @Column(name = "sidonm", nullable = false)
    private String sidonm;

    private double lat;  // 중심 위도

    private double lon;  // 중심 경도

    private int nx;      // 기상청 격자 X

    private int ny;      // 기상청 격자 Y

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String polygonText; // WKT 형식의 Polygon 문자열

}
