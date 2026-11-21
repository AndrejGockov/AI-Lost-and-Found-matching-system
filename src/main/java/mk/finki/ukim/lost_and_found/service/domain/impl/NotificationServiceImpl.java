package mk.finki.ukim.lost_and_found.service.domain.impl;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.*;
import mk.finki.ukim.lost_and_found.model.exceptions.NotificationNotFoundException;
import mk.finki.ukim.lost_and_found.repository.FoundItemRepository;
import mk.finki.ukim.lost_and_found.repository.LostItemRepository;
import mk.finki.ukim.lost_and_found.repository.NotificationRepository;
import mk.finki.ukim.lost_and_found.service.domain.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    @Override
    public List<Notification> findAllByUser(User user) {
        return notificationRepository.findAllByUserOrderByDateCreatedDesc(user);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification markAsRead(String id) {
        Notification notification = findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        notification.setViewed(true);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findAllByUserAndIsViewedFalse(user);
        unread.forEach(n -> n.setViewed(true));
        return notificationRepository.saveAll(unread);
    }

    @Override
    public Notification create(User user, String text, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(text);
        notification.setLink(link);

        return notificationRepository.save(notification);
    }

    @Override
    public Notification updatedLostItemStatusNotification(User user, LostItem item) {
        String text = "The status of your lost item \"" + item.getTitle() + "\" was updated to "
                + item.getStatus().getDisplayName() + ".";

        return create(user, text, "lost-items/" + item.getId());
    }

    @Override
    public Notification updatedFoundItemStatusNotification(User user, FoundItem item) {
        String text = "The status of your found item \"" + item.getTitle() + "\" was updated to "
                + item.getStatus().getDisplayName() + ".";

        return create(user, text, "found-items/" + item.getId());
    }

    @Override
    public Notification transferOfferAnswerNotification(User user, TransferFoundItem transfer, boolean accepted) {
        FoundItem foundItem = transfer.getFoundItem();
        User responder = transfer.getNewHolder();

        String text = accepted
                ? responder.getName() + " has accepted your offer for the item: " + foundItem.getTitle() + "."
                : responder.getName() + " has rejected your offer for the item: " + foundItem.getTitle() + ".";

        return create(user, text, "found-items/" + foundItem.getId());
    }

    @Override
    public Notification receivedTransferOfferNotification(User user, TransferFoundItem transfer) {
        FoundItem foundItem = transfer.getFoundItem();
        User offeredBy = foundItem.getHolder(); // current holder at offer time = the one sending the offer

        String text = "You have received an offer for this item: " + foundItem.getTitle()
                + " by " + offeredBy.getName() + ".";

        return create(user, text, "found-items/" + foundItem.getId());
    }

    @Override
    public Notification matchesNotification(User user, String id, List<String> ids) {
        return null;
    }
}