package com.boram.look.api.dto.weather;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private String dataType;
        private Items items;
        private int totalCount;
    }

    @Data
    public static class Items {
        private List<WeatherForecastDto> item;
    }

}
