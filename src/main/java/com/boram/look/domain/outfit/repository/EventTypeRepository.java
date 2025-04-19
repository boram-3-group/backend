package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.outfit.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
    List<EventType> findByIdIn(List<Integer> idList);

}
