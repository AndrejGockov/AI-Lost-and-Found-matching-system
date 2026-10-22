package mk.finki.ukim.lost_and_found.service.impl;

import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.exceptions.InvalidUsernameException;
import mk.finki.ukim.lost_and_found.repository.UserRepository;
import mk.finki.ukim.lost_and_found.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {this.userRepository = userRepository;}

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findById(username).orElseThrow(InvalidUsernameException::new);
    }

}
