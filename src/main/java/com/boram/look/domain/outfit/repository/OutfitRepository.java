package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.outfit.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {
}
