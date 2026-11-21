package mk.finki.ukim.lost_and_found.service.application.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.Notification;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayNotificationResponseDto;
//import mk.finki.ukim.lost_and_found.model.exceptions.NotNotificationOwnerException;
import mk.finki.ukim.lost_and_found.model.exceptions.NotificationNotFoundException;
import mk.finki.ukim.lost_and_found.service.application.NotificationApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationApplicationServiceImpl implements NotificationApplicationService {

    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<DisplayNotificationResponseDto> findAllByUser(User user) {
        return DisplayNotificationResponseDto.from(notificationService.findAllByUser(user));
    }

    @Override
    @Transactional
    public DisplayNotificationResponseDto markAsRead(String id, User user) {
        Notification notification = notificationService.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

//        if (!notification.getUser().getId().equals(user.getId())) {
//            throw new NotNotificationOwnerException();
//        }

        if (!notification.isViewed()) {
            notification = notificationService.markAsRead(id);
        }

        return DisplayNotificationResponseDto.from(notification);
    }

    @Override
    @Transactional
    public List<DisplayNotificationResponseDto> markAllAsRead(User user) {
        return DisplayNotificationResponseDto.from(notificationService.markAllAsRead(user));
    }

    @Override
    public DisplayNotificationResponseDto matchesNotification(User user, String id, List<String> ids) {
        return DisplayNotificationResponseDto.from(notificationService.matchesNotification(user, id, ids));
    }
}