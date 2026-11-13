package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.enums.ItemType;

public record DisplayItemResponseDto(
        String id,
        ItemType type,
        String title,
        String description,
        Instant datePosted,
        Instant lastUpdated,
        String posterId,
        String posterName,
        String holderId,
        String holderName,
        ItemStatus status,
        boolean hasImages,
        List<DisplayItemImageResponseDto> images
) {
    public static DisplayItemResponseDto from(DisplayFoundItemResponseDto dto) {
        return new DisplayItemResponseDto(
                dto.id(),
                ItemType.FOUND,
                dto.title(),
                dto.description(),
                dto.datePosted(),
                dto.lastUpdated(),
                dto.posterId(),
                dto.posterName(),
                dto.holderId(),
                dto.holderName(),
                dto.status(),
                dto.images() != null && !dto.images().isEmpty(),
                dto.images()
        );
    }

    public static DisplayItemResponseDto from(DisplayLostItemResponseDto dto) {
        return new DisplayItemResponseDto(
                dto.id(),
                ItemType.LOST,
                dto.title(),
                dto.description(),
                dto.datePosted(),
                dto.lastUpdated(),
                dto.posterId(),
                dto.posterName(),
                null,
                null,
                dto.status(),
                dto.images() != null && !dto.images().isEmpty(),
                dto.images()
        );
    }
}