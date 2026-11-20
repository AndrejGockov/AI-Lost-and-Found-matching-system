package mk.finki.ukim.lost_and_found.model.exceptions;

public class CannotTransferToSelfException extends RuntimeException {
    public CannotTransferToSelfException() {
        super("You cannot transfer an item to yourself.");
    }
}