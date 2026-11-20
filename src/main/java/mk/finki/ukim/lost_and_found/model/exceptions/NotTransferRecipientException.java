package mk.finki.ukim.lost_and_found.model.exceptions;

public class NotTransferRecipientException extends RuntimeException {
    public NotTransferRecipientException() {
        super("Only the recipient can accept or reject the transfer.");
    }
}