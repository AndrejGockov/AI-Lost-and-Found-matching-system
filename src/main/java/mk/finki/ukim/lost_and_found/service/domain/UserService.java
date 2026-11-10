package mk.finki.ukim.lost_and_found.service.domain;


import mk.finki.ukim.lost_and_found.model.domain.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    boolean isStudent(User user);

    boolean isProfessor(User user);
    List<User> searchActive(String query, User excluding, int limit);
}
