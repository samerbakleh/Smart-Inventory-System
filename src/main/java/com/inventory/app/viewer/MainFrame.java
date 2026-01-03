package com.inventory.app.viewer;

import com.inventory.app.controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {

    private MainController mainController;
    private LogInPanel logInPanel;
    private ProductLineManagerPanel productLineManagerPanel;

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
                SwingWorker<Object, Object> saveFilesWorker = new SwingWorker<>() {
                    @Override
                    protected Object doInBackground() throws IOException {
                        mainController.saveFiles();
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            get();
                            System.exit(0);
                        } catch(ExecutionException e) {
                            Throwable throwable = e.getCause();
                            /*

                            show throwable error message

                             */
                        } catch(InterruptedException e) {
                            /*

                            show error message

                             */
                        }
                    }
                };
                saveFilesWorker.execute();
            }
        });
        setLayout(new CardLayout());
        addComponents();
        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

        // start program operations: load files, start tasks, ...
        SwingWorker<Object, Object> loadFilesWorker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws IOException {
                mainController.loadFiles();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                } catch(ExecutionException e) {
                    Throwable throwable = e.getCause();
                            /*

                            show throwable error message

                             */
                } catch(InterruptedException e) {
                            /*

                            show error message

                             */
                }
            }
        };
        loadFilesWorker.execute();
    }

    public String getLogInCardName() {
        return logInPanel.getName();
    }
    public String getManagerCardName() {
        return productLineManagerPanel.getName();
    }

    public void addComponents() {
        logInPanel = new LogInPanel(this);
        add(logInPanel, logInPanel.getName());

        productLineManagerPanel = new ProductLineManagerPanel(this);
        add(productLineManagerPanel, productLineManagerPanel.getName());
    }

    public void switchCard(String cardName) {
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(getContentPane(), cardName);
    }

}