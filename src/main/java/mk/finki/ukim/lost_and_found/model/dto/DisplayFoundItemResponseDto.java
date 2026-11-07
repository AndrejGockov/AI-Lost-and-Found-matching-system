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

    // Restricted version
    public static DisplayFoundItemResponseDto fromRestricted(FoundItem item) {
        return new DisplayFoundItemResponseDto(
                item.getId(),
                item.getPoster().getId(),
                item.getPoster().getName(),
                item.getHolder().getId(),
                item.getHolder().getName(),
                item.getTitle(),
                // description hidden
                null,
                item.getDatePosted(),
                item.getLastUpdated(),
                // dateFound hidden
                null,
                // locationFound hidden
                null,
                item.isIdentifiable(),
                item.getStatus(),
                // images hidden
                List.of(),
                DisplayTransferFoundItemResponseDto.from(item.getTransfers())
        );
    }

    public static List<DisplayFoundItemResponseDto> from(List<FoundItem> items) {
        return items.stream().map(DisplayFoundItemResponseDto::from).toList();
    }

    public static List<DisplayFoundItemResponseDto> fromRestricted(List<FoundItem> items) {
        return items.stream().map(DisplayFoundItemResponseDto::fromRestricted).toList();
    }
}