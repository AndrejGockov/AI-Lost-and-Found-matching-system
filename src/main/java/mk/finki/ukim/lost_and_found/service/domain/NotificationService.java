package mk.finki.ukim.lost_and_found.service.domain;

import mk.finki.ukim.lost_and_found.model.domain.*;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> findAllByUser(User user);

    Optional<Notification> findById(String id);

    Notification markAsRead(String id);

    List<Notification> markAllAsRead(User user);

    // Notification types that use the create function:
    // - updated status for lost/found items
    // - accepted/rejected transfer for found items (dobivane odgovor na transfer ponudi)
    // - receiving transfer offers (ponuda za privzemane found items)
    // - receiving possible matches between lost & found items
    Notification create(User user, String text, String link);

    // Status updates
    Notification updatedLostItemStatusNotification(User user, LostItem item);

    Notification updatedFoundItemStatusNotification(User user, FoundItem item);

    // Transferring found items
    Notification transferOfferAnswerNotification(User user, TransferFoundItem transfer, boolean accepted);

    Notification receivedTransferOfferNotification(User user, TransferFoundItem transfer);

    // Matches for post
    Notification matchesNotification(User user, String id, List<String> ids);
}