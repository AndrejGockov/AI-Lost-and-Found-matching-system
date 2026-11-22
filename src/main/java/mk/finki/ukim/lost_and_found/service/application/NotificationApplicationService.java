package mk.finki.ukim.lost_and_found.service.application;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayNotificationResponseDto;

import java.util.List;

public interface NotificationApplicationService {
    List<DisplayNotificationResponseDto> findAllByUser(User user);

    DisplayNotificationResponseDto findById(String id, User user);

    DisplayNotificationResponseDto markAsRead(String id, User user);

    List<DisplayNotificationResponseDto> markAllAsRead(User user);

    DisplayNotificationResponseDto matchesNotification(User user, String id, List<String> ids);
}