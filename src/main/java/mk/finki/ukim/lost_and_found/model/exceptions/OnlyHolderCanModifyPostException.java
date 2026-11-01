package mk.finki.ukim.lost_and_found.model.exceptions;

public class OnlyHolderCanModifyPostException extends RuntimeException {
    public OnlyHolderCanModifyPostException() {
        super("Only the poster can modify this post.");
    }
}
