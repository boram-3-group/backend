package com.boram.look.domain.weather.uv;

import com.boram.look.api.dto.AirQualityDto;
import com.boram.look.api.dto.UvIndexDto;
import com.boram.look.domain.weather.air.AirQualityGrade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "uv_index_range")
public class UvIndexRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer min;
    private Integer max;
    @Column(name = "icon_flag")
    private Boolean iconFlag;
    private String message;
    @Enumerated(EnumType.STRING)
    private UvGrade grade;

    public UvIndexDto toDto(Integer currentValue) {
        return UvIndexDto.builder()
                .uvIndex(currentValue)
                .message(this.message)
                .iconFlag(this.iconFlag)
                .grade(this.grade)
                .build();
    }
}
