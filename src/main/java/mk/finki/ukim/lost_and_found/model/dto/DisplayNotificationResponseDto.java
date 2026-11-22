package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.Notification;

import java.time.Instant;
import java.util.List;

public record DisplayNotificationResponseDto(
        String id,
        String userId,
        String message,
        Instant dateCreated,
        boolean viewed,
        String link
) {
    public static DisplayNotificationResponseDto from(Notification notification) {
        return new DisplayNotificationResponseDto(
                notification.getId(),
                notification.getUser().getId(),
                notification.getMessage(),
                notification.getDateCreated(),
                notification.isViewed(),
                notification.getLink()
        );
    }

    public static List<DisplayNotificationResponseDto> from(List<Notification> notifications) {
        return notifications
                .stream()
                .map(DisplayNotificationResponseDto::from)
                .toList();
    }
}