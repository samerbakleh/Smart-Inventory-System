package com.inventory.app;

import com.inventory.app.util.AppConstants;

import com.inventory.app.viewer.MainFrame;

import com.inventory.app.repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);
        UserRepository.loadUsersFromFile();
        MainFrame.startMainFrame();

    }
}

