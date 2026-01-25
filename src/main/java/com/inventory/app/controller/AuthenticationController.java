package com.inventory.app.controller;

import com.inventory.app.model.enums.Role;
import com.inventory.app.service.AuthenticationService;

public class AuthenticationController {
    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public boolean loginController(String username, String password, String role) {
        LoginRequest request = new LoginRequest(username, password, Role.textToRole(role));
        return authenticationService.login(request);
    }

}