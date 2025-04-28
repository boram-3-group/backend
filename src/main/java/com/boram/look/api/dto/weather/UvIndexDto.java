package com.boram.look.api.dto.weather;

import com.boram.look.domain.weather.uv.UvGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "UvIndexDto", description = "자외선 예보 DTO")
public class UvIndexDto {

    @Schema(description = "자외선 지수", example = "30")
    private Integer uvIndex;

    @Schema(
            description = "자외선 지수에 해당하는 메세지\n" +
                    "지수가 나쁨 이상이면 예시 메세지 출력, 미만이면 공백을 출력함",
            example = "오늘은 공기질이 나빠요, 마스크 꼭 챙기기!"
    )
    private String message;

    @Schema(description = "아이콘을 출력해야 되는지 여부")
    private Boolean iconFlag;
    @Schema(description = "ondolook에서 정한 자외선 등급")
    private UvGrade grade;
}
