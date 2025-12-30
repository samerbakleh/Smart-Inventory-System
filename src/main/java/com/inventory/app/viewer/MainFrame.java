package com.inventory.app.viewer;

import com.inventory.app.controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private MainController mainController;
    private List<String> cards = new ArrayList<>();

    public MainFrame() {
        super("Production & Inventory Management System");
        mainController = new MainController();
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
            public void windowClosing(WindowEvent event) {
                try {
                    mainController.saveFiles();
                } catch(IOException e) {
                    /*

                    show error message

                     */
                }
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
        try {
            mainController.saveFiles();
        } catch(IOException e) {
            /*

            show error message

             */
        }
    }

    public List<String> getCards() {
        return cards;
    }

}