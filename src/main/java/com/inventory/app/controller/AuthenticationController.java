package com.inventory.app.controller;

import com.inventory.app.model.enums.Role;
import com.inventory.app.service.AuthenticationService;
public class AuthenticationController {
    public static boolean loginController(String username, String password, String role) {
        LoginRequest request = new LoginRequest(username, password, Role.textToRole(role));
        return AuthenticationService.login(request);
    }

}