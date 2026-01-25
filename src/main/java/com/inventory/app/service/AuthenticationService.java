package com.inventory.app.service;

import com.inventory.app.model.User;

import com.inventory.app.repository.UserRepository;

import com.inventory.app.controller.LoginRequest;

import com.inventory.app.util.Global;

public class AuthenticationService {
    private UserRepository userRepository;
    public AuthenticationService(UserRepository userRepository)
    {
        this.userRepository=userRepository;
    }
    public boolean login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword()) && user.getRole() == request.getRole()) {
            Global.currentUser = user;
            return true;
        }
        return false;
    }

    public void loadUsersFromFile()
    {
        userRepository.loadUsersFromFile();
    }
}


