package com.inventory.app.service;

import com.inventory.app.model.User;

import com.inventory.app.repository.UserRepository;

import com.inventory.app.controller.LoginRequest;

import com.inventory.app.util.Global;

public class AuthenticationService {
    public static boolean login(LoginRequest request) {
        User user = UserRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword()) && user.getRole() == request.getRole()) {
            Global.currentUser = user;
            return true;
        }
        return false;
    }
}


