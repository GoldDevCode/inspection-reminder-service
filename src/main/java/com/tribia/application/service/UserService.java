package com.tribia.application.service;

import com.tribia.application.entity.User;

import java.util.Optional;

public interface UserService {
    User findOrCreateUser(String email);

    Optional<User> findUserByEmail(String email);

    User saveUser(User user);
}
