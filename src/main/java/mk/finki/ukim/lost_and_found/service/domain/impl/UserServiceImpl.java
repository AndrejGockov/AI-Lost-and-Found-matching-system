package mk.finki.ukim.lost_and_found.service.domain.impl;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.exceptions.InvalidUsernameException;
import mk.finki.ukim.lost_and_found.repository.UserRepository;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {this.userRepository = userRepository;}

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findById(username).orElseThrow(InvalidUsernameException::new);
    }

    @Override
    public boolean isStudent(User user) {
        return Boolean.TRUE.equals(user.getRole().isStudent());
    }

    @Override
    public boolean isProfessor(User user) {return Boolean.TRUE.equals(user.getRole().isProfessor());}

    @Override
    public List<User> searchActive(String query, User excluding, int limit) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();
        return userRepository.searchActive(q, excluding.getId(),
                PageRequest.of(0, Math.min(Math.max(limit, 1), 20)));
    }
}
