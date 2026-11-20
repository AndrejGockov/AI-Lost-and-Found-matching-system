package mk.finki.ukim.lost_and_found.model.exceptions;

public class CannotTransferItemException extends RuntimeException {
    public CannotTransferItemException() {
        super("Cannot transfer item.");
    }
}
