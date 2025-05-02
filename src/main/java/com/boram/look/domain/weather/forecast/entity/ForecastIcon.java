package com.boram.look.domain.weather.forecast.entity;

public enum ForecastIcon {
    CLEAR_DAY(1, "맑음 (낮)"),
    CLEAR_NIGHT(2, "맑음 (밤)"),
    PARTLY_CLOUDY_DAY(3, "구름 조금 (낮)"),
    PARTLY_CLOUDY_NIGHT(4, "구름 조금 (밤)"),
    MOSTLY_CLOUDY_DAY(5, "구름 많음 (낮)"),
    MOSTLY_CLOUDY_NIGHT(6, "구름 많음 (밤)"),
    CLOUDY(7, "흐림"),
    SHOWER(8, "소나기"),
    RAIN(9, "비"),
    LIGHT_RAIN_DAY(10, "가끔 비 (낮)"),
    LIGHT_RAIN_NIGHT(11, "가끔 비 (밤)"),
    SNOW(12, "눈"),
    LIGHT_SNOW_DAY(13, "가끔 눈 (낮)"),
    LIGHT_SNOW_NIGHT(14, "가끔 눈 (밤)"),
    RAIN_AND_SNOW(15, "비 또는 눈"),
    LIGHT_RAIN_OR_SNOW_DAY(16, "가끔 비 또는 눈 (낮)"),
    LIGHT_RAIN_OR_SNOW_NIGHT(17, "가끔 비 또는 눈 (밤)"),
    THUNDERSTORM(18, "천둥번개"),
    FOG(19, "안개"),
    YELLOW_DUST(20, "황사");
    private final String iconMessage;
    private final Integer iconNumber;

    ForecastIcon(Integer iconNumber, String iconMessage) {
        this.iconNumber = iconNumber;
        this.iconMessage = iconMessage;
    }

    public String getIconMessage() {
        return iconMessage;
    }

    public Integer getIconNumber() {
        return iconNumber;
    }
}
