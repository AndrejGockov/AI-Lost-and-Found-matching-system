package mk.finki.ukim.lost_and_found.model.exceptions;

public class LostItemNotFoundException extends RuntimeException {
    public LostItemNotFoundException(String id) {
        super("Lost item has not been found with ID: %s".formatted(id));
    }
}
