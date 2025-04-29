package com.boram.look.api.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MidTemperatureRequest {

    private String regId;

    private Integer taMin4;
    private Integer taMax4;

    private Integer taMin5;
    private Integer taMax5;

    private Integer taMin6;
    private Integer taMax6;

    private Integer taMin7;
    private Integer taMax7;

    private Integer taMin8;
    private Integer taMax8;

    private Integer taMin9;
    private Integer taMax9;

    private Integer taMin10;
    private Integer taMax10;

    public Integer getRainProb(int day) {
        try {
            return (Integer) this.getClass().getDeclaredField("rnSt" + day).get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public String getWeather(int day) {
        try {
            return (String) this.getClass().getDeclaredField("wf" + day).get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getMinTemperature(int day) {
        try {
            return (Integer) this.getClass().getDeclaredField("taMin" + day).get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getMaxTemperature(int day) {
        try {
            return (Integer) this.getClass().getDeclaredField("taMax" + day).get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public void setFieldValue(String fieldName, Object value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {

        }
    }
}
