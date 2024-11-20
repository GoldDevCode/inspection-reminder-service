package com.tribia.application.service.impl;

import com.tribia.application.entity.User;
import com.tribia.application.repository.UserRepository;
import com.tribia.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    @Override
    public User findOrCreateUser(String email) {
        validate(email);
        return userRepository.findByEmail(email)
                .orElse(new User(email));
    }

    public Optional<User> findUserByEmail(String email) {
        validate(email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepository.save(user);
    }
}
