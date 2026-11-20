package mk.finki.ukim.lost_and_found.model.exceptions;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String foundItemTitle) {
        super("No pending transfer offer found for " + foundItemTitle + ".");
    }
}