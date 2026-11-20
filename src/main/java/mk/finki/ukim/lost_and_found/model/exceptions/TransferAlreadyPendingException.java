package mk.finki.ukim.lost_and_found.model.exceptions;

public class TransferAlreadyPendingException extends RuntimeException {
    public TransferAlreadyPendingException() {
        super("This item already has a pending transfer offer.");
    }
}