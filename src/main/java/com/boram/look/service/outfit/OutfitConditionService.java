package com.boram.look.service.outfit;

import com.boram.look.api.dto.EventTypeDto;
import com.boram.look.api.dto.TemperatureRangeDto;
import com.boram.look.domain.outfit.EventType;
import com.boram.look.domain.outfit.TemperatureRange;
import com.boram.look.domain.outfit.repository.EventTypeRepository;
import com.boram.look.domain.outfit.repository.TemperatureRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitConditionService {
    private final EventTypeRepository eventTypeRepository;
    private final TemperatureRangeRepository temperatureRangeRepository;


    @Transactional(readOnly = true)
    public Page<EventTypeDto.Copy> findEventTypes(Pageable pageable) {
        Page<EventType> eventTypes = eventTypeRepository.findAll(pageable);
        return eventTypes.map(EventType::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TemperatureRangeDto.Copy> findTemperatureRanges(Pageable pageable) {
        Page<TemperatureRange> eventTypes = temperatureRangeRepository.findAll(pageable);
        return eventTypes.map(TemperatureRange::toDto);
    }


    @Transactional
    public void insertEventTypes(List<EventTypeDto.Save> saveList) {
        List<EventType> eventTypes = saveList.stream()
                .map(EventTypeDto.Save::toEntity)
                .toList();
        eventTypeRepository.saveAll(eventTypes);
    }

    @Transactional
    public void insertTemperatureRanges(List<TemperatureRangeDto.Save> saveList) {
        List<TemperatureRange> temperatureRanges = saveList.stream()
                .map(TemperatureRangeDto.Save::toEntity)
                .toList();
        temperatureRangeRepository.saveAll(temperatureRanges);
    }

    @Transactional
    public void updateEventTypes(List<EventTypeDto.Copy> updateList) {
        List<Integer> idList = updateList.stream().map(EventTypeDto.Copy::id).toList();
        List<EventType> eventTypes = eventTypeRepository.findByIdIn(idList);
        Map<Integer, EventTypeDto.Copy> updateMap = updateList.stream()
                .collect(Collectors.toMap(EventTypeDto.Copy::id, Function.identity()));
        eventTypes.forEach(eventType -> {
            EventTypeDto.Copy dto = updateMap.get(eventType.getId());
            eventType.update(dto.categoryName());
        });
    }

    @Transactional
    public void updateTemperatureRanges(List<TemperatureRangeDto.Copy> updateList) {
        List<Integer> idList = updateList.stream().map(TemperatureRangeDto.Copy::id).toList();
        List<TemperatureRange> tempRanges = temperatureRangeRepository.findByIdIn(idList);
        Map<Integer, TemperatureRangeDto.Copy> updateMap = updateList.stream()
                .collect(Collectors.toMap(TemperatureRangeDto.Copy::id, Function.identity()));
        tempRanges.forEach(tempRange -> {
            TemperatureRangeDto.Copy dto = updateMap.get(tempRange.getId());
            tempRange.update(dto.max(), dto.min());
        });
    }

    @Transactional
    public void deleteTemperatureRanges(List<Integer> deleteIds) {
        List<TemperatureRange> tempRanges = temperatureRangeRepository.findByIdIn(deleteIds);
        temperatureRangeRepository.deleteAll(tempRanges);
    }

    @Transactional
    public void deleteEventTypes(List<Integer> deleteIds) {
        List<EventType> eventTypes = eventTypeRepository.findByIdIn(deleteIds);
        eventTypeRepository.deleteAll(eventTypes);
    }


}
