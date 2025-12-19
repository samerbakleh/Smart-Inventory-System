package com.inventory.app.controller;

import com.inventory.app.service.LoginResult;
import com.inventory.app.model.User;
import com.inventory.app.service.AuthenticationService;
import com.inventory.app.repository.UserRepository;
public class AuthenticationController {
    public static LoginResult LoginController(String username, String password, String role) {
        User userInput = new User("","","","",username, password, UserRepository.GetEnumRole(role));
        return AuthenticationService.Login(userInput);
    }

}
