package mk.finki.ukim.lost_and_found.model.exceptions;

public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException(String username) {
        super(String.format("Professor with username %s not found", username));
    }
}

