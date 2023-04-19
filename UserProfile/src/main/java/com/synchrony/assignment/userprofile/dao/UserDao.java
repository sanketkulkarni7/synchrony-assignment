package com.synchrony.assignment.userprofile.dao;

import com.synchrony.assignment.userprofile.model.User;
import com.synchrony.assignment.userprofile.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author sanketku
 */
@Component
public class UserDao {
    private UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }
}
