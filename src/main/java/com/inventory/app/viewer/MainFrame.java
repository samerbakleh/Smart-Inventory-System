package com.inventory.app.viewer;

import com.inventory.app.controller.AuthenticationController;
import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.MainController;
import com.inventory.app.controller.ProductLineController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {

    private MainController mainController;
    private JPanel cardPanel;
    private LogInPanel logInPanel;
    private ManagerPanel managerPanel;
    private SupervisorPanel supervisorPanel;

    public MainFrame(MainController mainController, AuthenticationController authenticationController,
                     ItemController itemController, ProductLineController productLineController) {
        super("Production & Inventory Management System");
        this.mainController = mainController;
        this.logInPanel = new LogInPanel(authenticationController);
        this.managerPanel = new ManagerPanel(productLineController);
        this.supervisorPanel = new SupervisorPanel(itemController, productLineController);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

    private void addComponents() {
        cardPanel = new JPanel(new CardLayout());

        logInPanel.setContainer(this);

        cardPanel.add(logInPanel, logInPanel.getName());
        cardPanel.add(managerPanel, managerPanel.getName());
        cardPanel.add(supervisorPanel, supervisorPanel.getName());

        add(cardPanel);
    }

    public void switchCard(String cardName) {
        CardLayout layout = (CardLayout) cardPanel.getLayout();
        layout.show(cardPanel, cardName);
    }
    public void switchToLogInPanel() {
        switchCard(logInPanel.getName());
    }
    public void switchToManagerPanel() {
        switchCard(managerPanel.getName());
    }
    public void switchToSupervisorPanel() {
        switchCard(supervisorPanel.getName());
    }

}