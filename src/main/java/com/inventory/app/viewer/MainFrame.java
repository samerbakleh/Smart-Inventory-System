package com.inventory.app.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private List<String> cards = new ArrayList<>();

    public MainFrame() {
        super("Production & Inventory Management System");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // save files
                System.exit(0);
            }
        });
        setLayout(new CardLayout());

        LogInPanel logInPanel = new LogInPanel(this);
        cards.add(logInPanel.getName());
        add(logInPanel, logInPanel.getName());

        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

        // start program operations: load files, start tasks, ...
    }

    public List<String> getCards() {
        return cards;
    }

}