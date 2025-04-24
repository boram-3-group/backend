package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.outfit.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
    List<EventType> findByIdIn(List<Integer> idList);

}
