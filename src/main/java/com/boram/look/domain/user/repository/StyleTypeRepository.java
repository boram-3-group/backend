package com.boram.look.domain.user.repository;

import com.boram.look.domain.user.entity.StyleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StyleTypeRepository extends JpaRepository<StyleType, Integer> {
    Set<StyleType> findByIdIn(List<Integer> styleIds);
}
