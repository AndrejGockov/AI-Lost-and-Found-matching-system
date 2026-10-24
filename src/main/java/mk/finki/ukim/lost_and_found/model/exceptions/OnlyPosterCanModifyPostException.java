package mk.finki.ukim.lost_and_found.model.exceptions;

public class OnlyPosterCanModifyPostException extends RuntimeException {
    public OnlyPosterCanModifyPostException() {
        super("Only the poster can modify this post.");
    }
}
