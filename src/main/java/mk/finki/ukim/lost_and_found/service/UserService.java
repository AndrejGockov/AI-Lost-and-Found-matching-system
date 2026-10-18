package mk.finki.ukim.lost_and_found.service;


import mk.finki.ukim.lost_and_found.model.User;

public interface UserService {
    User findByUsername(String username);
}
