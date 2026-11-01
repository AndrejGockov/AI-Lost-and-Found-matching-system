package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;

public record DisplayFoundItemResponseDto(
        String id,
        String posterId,
        String posterName,
        String holderId,
        String holderName,
        String title,
        String description,
        Instant datePosted,
        Instant lastUpdated,
        Instant dateFound,
        String locationFound,
        boolean identifiable,
        ItemStatus status,
        List<DisplayItemImageResponseDto> images,
        List<DisplayTransferFoundItemResponseDto> transfers
) {
    public static DisplayFoundItemResponseDto from(FoundItem item) {
        return new DisplayFoundItemResponseDto(
                item.getId(),
                item.getPoster().getId(),
                item.getPoster().getName(),
                item.getHolder().getId(),
                item.getHolder().getName(),
                item.getTitle(),
                item.getDescription(),
                item.getDatePosted(),
                item.getLastUpdated(),
                item.getDateFound(),
                item.getLocationFound(),
                item.isIdentifiable(),
                item.getStatus(),
                item.getImages()
                        .stream()
                        .map(DisplayItemImageResponseDto::from)
                        .toList(),
                DisplayTransferFoundItemResponseDto.from(item.getTransfers())
        );
    }

    public static List<DisplayFoundItemResponseDto> from(List<FoundItem> items) {
        return items
                .stream()
                .map(DisplayFoundItemResponseDto::from)
                .toList();
    }
}