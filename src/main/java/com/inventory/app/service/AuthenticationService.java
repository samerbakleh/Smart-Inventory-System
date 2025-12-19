package com.inventory.app.service;

import com.inventory.app.model.User;

import java.util.ArrayList;

import com.inventory.app.util.clsGlobal;

import com.inventory.app.repository.UserRepository;

public class AuthenticationService {
    public static User Find(String UserName, String Password) {
        ArrayList<User> Users = UserRepository.LoadUsersFromFile();
        for (User user : Users) {
            if (user.get_UserName().equals(clsGlobal.CurrentUser.get_UserName()) && clsGlobal.CurrentUser.get_Password().equals(user.get_Password())&&user.get_Role()==clsGlobal.CurrentUser.get_Role()) {
                return user;
            }
        }
        return GetEmptyUsetObject();
    }

    public static User GetEmptyUsetObject() {
        return new User("", "", "", "", "", "", User.enRole.eEmptyRole);
    }

}


