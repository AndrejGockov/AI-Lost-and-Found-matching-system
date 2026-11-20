package mk.finki.ukim.lost_and_found.model.exceptions;

public class TransferAlreadyProcessedException extends RuntimeException {
    public TransferAlreadyProcessedException() {
        super("This transfer was already processed.");
    }
}