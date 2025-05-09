package com.boram.look.service.user;

import com.boram.look.api.dto.FileDto;
import com.boram.look.api.dto.user.BookmarkDto;
import com.boram.look.domain.outfit.OutfitImage;
import com.boram.look.domain.outfit.repository.OutfitImageRepository;
import com.boram.look.domain.user.entity.Bookmark;
import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.BookmarkRepository;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.s3.FileFacade;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final OutfitImageRepository outfitImageRepository;
    private final FileFacade fileFacade;

    @Transactional
    public BookmarkDto insertBookmark(PrincipalDetails principalDetails, Long outfitImageId) {
        User loginUser = principalDetails.getUser();
        OutfitImage outfitImage = outfitImageRepository.findById(outfitImageId).orElseThrow(EntityNotFoundException::new);
        Bookmark bookmark = bookmarkRepository.findByUserAndOutfitImage(loginUser, outfitImage)
                .orElseGet(() -> Bookmark.builder()
                        .user(loginUser)
                        .outfitImage(outfitImage)
                        .build());

        Bookmark entity = bookmarkRepository.save(bookmark);
        FileDto fileDto = fileFacade.buildFileDto(outfitImage.getFileMetadata());
        return BookmarkDto.builder()
                .id(entity.getId())
                .outfitImage(outfitImage.toDto(fileDto))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<BookmarkDto> getAllBookmarks(PrincipalDetails principalDetails, Pageable pageable) {
        User loginUser = principalDetails.getUser();
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(pageable, loginUser.getId());
        return bookmarks.map(bookmark -> {
            FileDto fileDto = fileFacade.buildFileDto(bookmark.getOutfitImage().getFileMetadata());
            return BookmarkDto.builder()
                    .id(bookmark.getId())
                    .outfitImage(bookmark.getOutfitImage().toDto(fileDto))
                    .build();
        });
    }

    @Transactional
    public void deleteBookmark(PrincipalDetails principalDetails, Long outfitImageId) {
        User loginUser = principalDetails.getUser();
        OutfitImage outfitImage = outfitImageRepository.findById(outfitImageId).orElseThrow(EntityNotFoundException::new);
        Bookmark bookmark = bookmarkRepository.findByUserAndOutfitImage(loginUser, outfitImage).orElseThrow(EntityNotFoundException::new);
        bookmarkRepository.delete(bookmark);
    }

}
