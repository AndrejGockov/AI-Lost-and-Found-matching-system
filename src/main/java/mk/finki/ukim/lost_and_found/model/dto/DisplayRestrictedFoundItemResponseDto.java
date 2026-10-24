package mk.finki.ukim.lost_and_found.model.dto;

import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;

// Restricted view for non-identifiable items
public record DisplayRestrictedFoundItemResponseDto(
        String id,
        String title,
        String posterId,
        String posterName,
        String holderId,
        String holderName,
        String description,
        Instant datePosted,
        Instant lastUpdated,
        ItemStatus status
) {
    public static DisplayRestrictedFoundItemResponseDto from(FoundItem item) {
        return new DisplayRestrictedFoundItemResponseDto(
                item.getId(),
                item.getTitle(),
                item.getPoster().getId(),
                item.getPoster().getName(),
                item.getHolder().getId(),
                item.getHolder().getName(),
                item.getDescription(),
                item.getDatePosted(),
                item.getLastUpdated(),
                item.getStatus()
        );
    }

    public static List<DisplayRestrictedFoundItemResponseDto> from(List<FoundItem> items) {
        return items
                .stream()
                .map(DisplayRestrictedFoundItemResponseDto::from)
                .toList();
    }
}