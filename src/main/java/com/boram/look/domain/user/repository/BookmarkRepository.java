package com.boram.look.domain.user.repository;

import com.boram.look.domain.outfit.OutfitImage;
import com.boram.look.domain.user.entity.Bookmark;
import com.boram.look.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Page<Bookmark> findAllByUserId(Pageable pageable, UUID id);

    Optional<Bookmark> findByUserAndOutfitImage(User loginUser, OutfitImage outfitImage);
}
