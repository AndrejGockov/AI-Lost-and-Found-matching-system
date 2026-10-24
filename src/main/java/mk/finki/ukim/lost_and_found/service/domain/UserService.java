package mk.finki.ukim.lost_and_found.service.domain;


import mk.finki.ukim.lost_and_found.model.domain.User;

public interface UserService {
    User findByUsername(String username);
}
