package com.inventory.app;

import com.inventory.app.util.ErrorLog;
import com.inventory.app.viewer.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);
        UserRepository.loadUsersFromFile();
        MainFrame.startMainFrame();
        ErrorLog.installGlobalHandlers();

     

    }
}
