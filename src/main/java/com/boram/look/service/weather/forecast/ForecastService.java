package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.repository.RegionRepository;
import com.boram.look.domain.weather.forecast.entity.Forecast;
import com.boram.look.domain.weather.forecast.repository.ForecastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastService {

    private final ForecastRepository forecastRepository;
    private final RegionRepository regionRepository;

    @Transactional
    public Map<Long, List<ForecastDto>> saveShortTermsForecast(Map<Long, List<ForecastDto>> weatherMap) {
        Map<Long, List<ForecastDto>> resultMap = new HashMap<>();
        for (Map.Entry<Long, List<ForecastDto>> entry : weatherMap.entrySet()) {
            List<ForecastDto> dtos = entry.getValue();
            Long regionId = entry.getKey();
            forecastRepository.deleteByRegionId(regionId);

            // 지역 정보 조회
            Optional<Region> region = regionRepository.findById(regionId);
            if (region.isEmpty()) continue;

            List<Forecast> forecasts = dtos.stream()
                    .map(dto -> dto.toEntity(region.get()))
                    .toList();
            forecastRepository.saveAll(forecasts);

            // 하루치만 추출 (최소 시간부터 24시간 이내)
            List<LocalDateTime> dateTimes = dtos.stream()
                    .map(ForecastDto::toDateTime)
                    .sorted()
                    .toList();

            if (dateTimes.isEmpty()) continue;
            LocalDateTime minDateTime = dateTimes.get(0);
            LocalDateTime maxDateTime = minDateTime.plusHours(24);

            List<ForecastDto> oneDayDtos = dtos.stream()
                    .filter(dto -> {
                        LocalDateTime dt = dto.toDateTime();
                        return !dt.isBefore(minDateTime) && dt.isBefore(maxDateTime);
                    })
                    .toList();

            resultMap.put(regionId, oneDayDtos);
        }

        return resultMap;
    }

    @Transactional
    public List<ForecastDto> saveShortTermsForecastByRegion(List<ForecastDto> dtos, Long regionId) {
        forecastRepository.deleteByRegionId(regionId);

        // 지역 정보 조회
        Optional<Region> region = regionRepository.findById(regionId);
        if (region.isEmpty()) return new ArrayList<>();

        List<Forecast> forecasts = dtos.stream()
                .map(dto -> dto.toEntity(region.get()))
                .toList();
        forecastRepository.saveAll(forecasts);

        // 하루치만 추출 (최소 시간부터 24시간 이내)
        List<LocalDateTime> dateTimes = dtos.stream()
                .map(ForecastDto::toDateTime)
                .sorted()
                .toList();

        if (dateTimes.isEmpty()) return new ArrayList<>();
        LocalDateTime minDateTime = dateTimes.get(0);
        LocalDateTime maxDateTime = minDateTime.plusHours(24);

        return dtos.stream()
                .filter(dto -> {
                    LocalDateTime dt = dto.toDateTime();
                    return !dt.isBefore(minDateTime) && dt.isBefore(maxDateTime);
                })
                .toList();
    }

    public List<Forecast> find24HourForecasts(Long regionId) {
        Optional<Region> region = regionRepository.findById(regionId);
        if (region.isEmpty()) return new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(24);

        // 예보 데이터가 "20250518", "0100" 이런 형식이면
        String startDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = end.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 날짜 기준으로 대략적으로 필터 (하루 or 이틀치)
        List<Forecast> forecasts = forecastRepository.findByRegionAndDateBetween(region.get(), startDate, endDate);

        // 날짜 + 시간 합쳐서 정확히 24시간 이내 필터
        return forecasts.stream()
                .filter(f -> {
                    LocalDateTime forecastTime = toDateTime(f.getDate(), f.getTime());
                    return !forecastTime.isBefore(now) && forecastTime.isBefore(end);
                })
                .toList();
    }

    // date: "20250518", time: "0100"
    private LocalDateTime toDateTime(String date, String time) {
        String paddedTime = String.format("%04d", Integer.parseInt(time)); // "0100"
        return LocalDateTime.of(
                Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(4, 6)),
                Integer.parseInt(date.substring(6, 8)),
                Integer.parseInt(paddedTime.substring(0, 2)),
                Integer.parseInt(paddedTime.substring(2, 4))
        );
    }


}
