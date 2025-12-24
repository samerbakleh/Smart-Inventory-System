package com.inventory.app.controller;

import com.inventory.app.model.enums.Role;

public class LoginRequest {
    private String username;
    private String password;
    private Role role;

    public LoginRequest(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
