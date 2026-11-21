package mk.finki.ukim.lost_and_found.model.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String id) {
        super("Notification not found: " + id);
    }
}