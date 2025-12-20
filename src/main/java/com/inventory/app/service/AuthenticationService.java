package com.inventory.app.service;

import com.inventory.app.model.User;

import com.inventory.app.repository.UserRepository;

public class AuthenticationService {
    public static LoginResult login(User user1) {
        User user = UserRepository.findByUsername(user1.getUserName());
        if (user != null && user.getPassword().equals(user1.getPassword()) && user.getRole() == user1.getRole()) {
            return new LoginResult(true, "", user);
        }
        return new LoginResult(false, "", null);
    }
}


