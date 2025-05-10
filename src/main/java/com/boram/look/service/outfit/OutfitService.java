package com.boram.look.service.outfit;

import com.boram.look.api.dto.FileDto;
import com.boram.look.api.dto.outfit.OutfitDto;
import com.boram.look.api.dto.outfit.OutfitImageDto;
import com.boram.look.domain.condition.EventType;
import com.boram.look.domain.outfit.Outfit;
import com.boram.look.domain.outfit.OutfitImage;
import com.boram.look.domain.condition.TemperatureRange;
import com.boram.look.domain.condition.repository.EventTypeRepository;
import com.boram.look.domain.outfit.repository.OutfitImageRepository;
import com.boram.look.domain.outfit.repository.OutfitRepository;
import com.boram.look.domain.condition.repository.TemperatureRangeRepository;
import com.boram.look.domain.s3.FileMetadata;
import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.BookmarkRepository;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.s3.FileFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final OutfitImageRepository outfitImageRepository;
    private final TemperatureRangeRepository temperatureRangeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FileFacade fileFacade;

    @Transactional
    public void insertOutfit(OutfitDto.Insert dto) {
        EventType type = eventTypeRepository.findById(dto.eventTypeId()).orElseThrow(ResourceNotFoundException::new);
        TemperatureRange temperatureRange = temperatureRangeRepository.findById(dto.temperatureRangeId()).orElseThrow(ResourceNotFoundException::new);
        Outfit outfit = Outfit.builder()
                .eventType(type)
                .temperatureRange(temperatureRange)
                .gender(dto.gender())
                .build();
        outfitRepository.save(outfit);
    }

    @Transactional
    public void insertOutfitImages(
            PrincipalDetails principalDetails,
            List<MultipartFile> images,
            List<OutfitDto.Image> dtos,
            Long outfitId
    ) {
        if (images.size() != dtos.size()) {
            throw new IllegalArgumentException("이미지 개수와 설명 개수가 일치하지 않습니다.");
        }

        User user = principalDetails.getUser();
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(ResourceNotFoundException::new);

        List<FileMetadata> imageMetadataList = new ArrayList<>();

        for (MultipartFile imageFile : images) {
            FileMetadata metadata = fileFacade.uploadAndGetEntity(imageFile, "/outfit-images/", user.getId());
            imageMetadataList.add(metadata);
        }

        List<OutfitImage> outfitImages = IntStream.range(0, dtos.size())
                .mapToObj(i -> OutfitImage.builder()
                        .fileMetadata(imageMetadataList.get(i))
                        .outfit(outfit)
                        .title(dtos.get(i).title())
                        .description(dtos.get(i).description())
                        .build())
                .toList();

        outfitImageRepository.saveAll(outfitImages);
    }

    @Transactional
    public void updateOutfit(OutfitDto.Insert dto, Long outfitId) {
        Outfit outfit = outfitRepository.findById(outfitId).orElseThrow(ResourceNotFoundException::new);
        EventType eventType = eventTypeRepository.findById(dto.eventTypeId()).orElseThrow(ResourceNotFoundException::new);
        TemperatureRange temperatureRange = temperatureRangeRepository.findById(dto.temperatureRangeId()).orElseThrow(ResourceNotFoundException::new);
        outfit.update(eventType, temperatureRange, dto.gender());
    }

    @Transactional
    public void deleteOutfit(Long outfitId) {
        outfitRepository.deleteById(outfitId);
    }

    @Transactional(readOnly = true)
    public OutfitDto.Transfer matchOutfit(Integer eventTypeId, List<Forecast> forecasts, Gender gender, UUID userId) {
        float averageTemperature = (float) forecasts.stream()
                .mapToDouble(Forecast::getTemperature)
                .average()
                .orElse(20.0);

        EventType eventType = eventTypeRepository.findById(eventTypeId).orElseThrow(ResourceNotFoundException::new);
        TemperatureRange temperatureRange = temperatureRangeRepository.findByTemperature(averageTemperature).orElseThrow(ResourceNotFoundException::new);
        Outfit outfit = outfitRepository.findByEventTypeAndTemperatureRangeAndGender(eventType, temperatureRange, gender).orElseThrow(ResourceNotFoundException::new);

        Set<Long> bookmarkedImageIds;
        if (userId != null) {
            List<Long> imageIds = outfit.getImages().stream().map(OutfitImage::getId).toList();
            bookmarkedImageIds = new HashSet<>(bookmarkRepository.findBookmarkedImageIds(userId, imageIds));
        } else {
            bookmarkedImageIds = Collections.emptySet();
        }

        List<OutfitImage> allImages = outfit.getImages();
        Collections.shuffle(allImages); // 리스트를 무작위로 섞음

        int imageLimit = (userId != null) ? 3 : 1;
        List<OutfitImage> selectedImages = allImages.stream()
                .limit(imageLimit)
                .toList();

        List<OutfitImageDto> images = selectedImages.stream()
                .map(image -> {
                    FileDto dto = fileFacade.buildFileDto(image.getFileMetadata());
                    boolean bookmarked = userId != null && bookmarkedImageIds.contains(image.getId());
                    return OutfitImageDto.builder()
                            .id(image.getId())
                            .title(image.getTitle())
                            .description(image.getDescription())
                            .metadata(dto)
                            .bookmarked(bookmarked)
                            .build();
                })
                .toList();
        return outfit.toDto(images);
    }

}
