package mk.finki.ukim.lost_and_found.model.exceptions;

public class FoundItemNotFoundException extends RuntimeException {
    public FoundItemNotFoundException(String id) {
        super("Found item has not been found with ID: %s".formatted(id));
    }
}