package com.boram.look.domain.condition.repository;

import com.boram.look.domain.condition.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
    List<EventType> findByIdIn(List<Integer> idList);

    Page<EventType> findAllByOrderBySequence(Pageable pageable);

}
