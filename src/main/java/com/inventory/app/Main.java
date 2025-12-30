package com.inventory.app;

import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
            }
        });

    }

}

