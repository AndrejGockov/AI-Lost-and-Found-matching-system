package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;

public record DisplayLostItemResponseDto(
        String id,
        String posterId,
        String posterName,
        String title,
        String description,
        Instant datePosted,
        Instant lastUpdated,
        boolean updated,
        Instant dateLost,
        String locationLost,
        ItemStatus status,
        List<DisplayItemImageResponseDto> images
) {
    public static DisplayLostItemResponseDto from(LostItem item) {
        return new DisplayLostItemResponseDto(
                item.getId(),
                item.getPoster().getId(),
                item.getPoster().getName(),
                item.getTitle(),
                item.getDescription(),
                item.getDatePosted(),
                item.getLastUpdated(),
                item.isUpdated(),
                item.getDateLost(),
                item.getLocationLost(),
                item.getStatus(),
                item.getImages()
                        .stream()
                        .map(DisplayItemImageResponseDto::from)
                        .toList()
        );
    }

    public static List<DisplayLostItemResponseDto> from(List<LostItem> items) {
        return items
                .stream()
                .map(DisplayLostItemResponseDto::from)
                .toList();
    }
}