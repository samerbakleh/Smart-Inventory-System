package com.inventory.app;

import com.inventory.app.util.AppConstants;

import javax.swing.*;

import com.inventory.app.viewer.MainFrame;

import com.inventory.app.viewer.LogInPanel;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        // تشغيل الواجهة في الثريد الخاص بها (Best Practice for Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // محاولة تفعيل الستايل الخاص بالنظام
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
}

}

