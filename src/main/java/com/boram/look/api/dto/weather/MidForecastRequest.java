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
public class MidForecastRequest {

    private String regId;

    private Integer rnSt4;
    private String wf4;

    private Integer rnSt5;
    private String wf5;

    private Integer rnSt6;
    private String wf6;

    private Integer rnSt7;
    private String wf7;

    private Integer rnSt8;
    private String wf8;

    private Integer rnSt9;
    private String wf9;

    private Integer rnSt10;
    private String wf10;

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
