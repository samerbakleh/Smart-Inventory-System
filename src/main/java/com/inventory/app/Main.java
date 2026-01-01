package com.inventory.app;

import com.inventory.app.viewer.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();

            // شاشة تجريبية فقط
            JPanel p = new JPanel();
            p.add(new JLabel("Hello from Supervisor (placeholder)"));

            frame.registerView("supervisor", p);
            frame.show("supervisor");

            frame.setVisible(true);
        });
    }
}
