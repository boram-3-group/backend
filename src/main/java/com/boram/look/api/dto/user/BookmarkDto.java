package com.boram.look.api.dto.user;

import com.boram.look.api.dto.outfit.OutfitImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class BookmarkDto {

    private Long id;
    private OutfitImageDto outfitImage;
}
