package com.inventory.app.service;

import com.inventory.app.model.User;

import com.inventory.app.repository.UserRepository;

public class AuthenticationService {
    public static LoginResult Login(User user1) {
        User user = UserRepository.FindByUsername(user1.get_UserName());
        if (user != null && user.get_Password().equals(user1.get_Password()) && user.get_Role() == user1.get_Role()) {
            return new LoginResult(true, "", user);
        }
        return new LoginResult(false, "", null);
    }
}


