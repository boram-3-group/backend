package com.boram.look.domain.region.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "sido_region")
public class SidoRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "sido": "11",
    // "sidonm": "서울특별시",
    @Column(name = "sido", nullable = false)
    private String sido;

    @Column(name = "api_key")
    private String apiKey;

    private double lat;  // 중심 위도

    private double lon;  // 중심 경도

    @Lob
    @Column(name = "polygon_text", columnDefinition = "MEDIUMTEXT")
    private String polygonText; // WKT 형식의 Polygon 문자열
}
