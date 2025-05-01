package com.boram.look.service.weather.mid;

import com.boram.look.api.dto.weather.MidForecastRequest;
import com.boram.look.api.dto.weather.MidTemperatureRequest;
import com.boram.look.api.dto.weather.MidTermForecastDto;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.mid.MidTermForecast;
import com.boram.look.domain.weather.mid.MidTermTemperature;
import com.boram.look.domain.weather.mid.repository.MidTermForecastRepository;
import com.boram.look.domain.weather.mid.repository.MidTermTemperatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MidForecastService {

    private final MidTermForecastRepository forecastRepository;
    private final MidTermTemperatureRepository temperatureRepository;


    @Transactional(readOnly = true)
    public List<MidTermForecastDto> getMidTermsWeather(SidoRegionCache sido) {
        LocalDate today = LocalDate.now();
        LocalDate day10After = today.plusDays(9);
        return forecastRepository.find10DaysMidTermForecasts(sido.midFcstKey(), today, day10After);
    }

    @Transactional
    public void saveMidTemperature(MidTermTemperature temperature) {
        MidTermTemperature entity = temperatureRepository.findByForecastDateAndRegId(temperature.getForecastDate(), temperature.getRegId())
                .map(
                        finded -> {
                            finded.update(temperature.getForecastDate(), temperature.getMinTemperature(), temperature.getMaxTemperature());
                            return finded;
                        })
                .orElseGet(() -> MidTermTemperature.builder()
                        .regId(temperature.getRegId())
                        .forecastDate(temperature.getForecastDate())
                        .minTemperature(temperature.getMinTemperature())
                        .maxTemperature(temperature.getMaxTemperature())
                        .build()
                );
        temperatureRepository.save(entity);
    }

    @Transactional
    public void saveMidForecast(MidTermForecast forecast) {
        MidTermForecast entity = forecastRepository.findByForecastDateAndRegId(forecast.getForecastDate(), forecast.getRegId())
                .map(
                        finded -> {
                            finded.update(forecast.getForecastDate(), forecast.getRainProbability(), forecast.getWeather());
                            return finded;
                        })
                .orElseGet(() -> MidTermForecast.builder()
                        .regId(forecast.getRegId())
                        .forecastDate(forecast.getForecastDate())
                        .rainProbability(forecast.getRainProbability())
                        .weather(forecast.getWeather())
                        .build()
                );
        forecastRepository.save(entity);
    }

    @Transactional
    public void saveMidTermTemperature(MidTemperatureRequest request) {
        LocalDate today = LocalDate.now();

        // 온도 저장 (4~10일)
        for (int day = 4; day <= 10; day++) {
            saveTemperature(today, day, request);
        }
    }

    @Transactional
    public void saveMidTermForecast(MidForecastRequest request) {
        LocalDate today = LocalDate.now();

        // 온도 저장 (4~10일)
        for (int day = 4; day <= 10; day++) {
            saveForecast(today, day, request);
        }
    }


    private void saveForecast(LocalDate today, int day, MidForecastRequest request) {
        LocalDate forecastDate = today.plusDays(day);
        MidTermForecast entity = forecastRepository.findByForecastDateAndRegId(forecastDate, request.getRegId())
                .map(
                        finded -> {
                            finded.update(forecastDate, request.getRainProb(day), request.getWeather(day));
                            return finded;
                        }).orElseGet(() -> {
                            return MidTermForecast.builder()
                                    .regId(request.getRegId())
                                    .forecastDate(forecastDate)
                                    .rainProbability(request.getRainProb(day))
                                    .weather(request.getWeather(day))
                                    .build();
                        }
                );
        forecastRepository.save(entity);

    }

    private void saveTemperature(LocalDate today, int day, MidTemperatureRequest request) {
        LocalDate forecastDate = today.plusDays(day);
        MidTermTemperature entity = temperatureRepository.findByForecastDateAndRegId(forecastDate, request.getRegId())
                .map(
                        finded -> {
                            finded.update(forecastDate, request.getMinTemperature(day), request.getMaxTemperature(day));
                            return finded;
                        }).orElseGet(() -> MidTermTemperature.builder()
                        .regId(request.getRegId())
                        .forecastDate(forecastDate)
                        .minTemperature(request.getMinTemperature(day))
                        .maxTemperature(request.getMaxTemperature(day))
                        .build()
                );
        temperatureRepository.save(entity);
    }

    @Transactional
    public void deletePastDateWeather() {
        LocalDate today = LocalDate.now();
        temperatureRepository.findTemperatureBeforeDate(today);
        forecastRepository.deleteForecastBeforeDate(today);
    }
}
