package com.inventory.app.viewer;

import com.inventory.app.service.ProductionService;
import com.inventory.app.controller.*;
import com.inventory.app.util.ErrorLog;

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
                     ItemController itemController, ProductLineController productLineController,
                     ProductTypeController productTypeController, TaskController taskController,
                     ProductController productController) {
        super("Production & Inventory Management System");
        this.mainController = mainController;
        try {
            mainController.loadFiles();
        } catch(IOException e) {
            ErrorLog.error("Files", e);
        }
        this.logInPanel = new LogInPanel(authenticationController);
        this.managerPanel = new ManagerPanel(productLineController, productTypeController);
        this.supervisorPanel = new SupervisorPanel(this, itemController, productController, productLineController, taskController);
        mainController.addProductionListener(new ProductionService.Listener() {
            @Override
            public void productionUpdated() {
                managerPanel.refreshTableContent();
                supervisorPanel.refreshProduction();
            }
            @Override
            public void materialsShortage(String message) {
                JOptionPane pane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
                JDialog dialog = pane.createDialog(MainFrame.this, "Waiting for raw materials");
                dialog.setModal(false);         // production and the UI keep going while it is shown
                dialog.setVisible(true);
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                SwingWorker<Object, Object> saveFilesWorker = new SwingWorker<>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        mainController.saveFiles();
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            get();
                            System.exit(0);
                        } catch(ExecutionException e) {
                            ErrorLog.error("File", e.getCause());
                        } catch(InterruptedException e) {
                            ErrorLog.error("Thread Interruption", e);
                        }
                    }
                };
                saveFilesWorker.execute();
            }
        });
        addComponents();
        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);
    }

    private void addComponents() {

        JMenuBar menuBar = new JMenuBar();
        JButton logOutButton = new JButton(new ImageIcon("src/main/resources/icons/log-out.png"));
        JButton refreshButton = new JButton(new ImageIcon("src/main/resources/icons/refresh.png"));
        logOutButton.setContentAreaFilled(false);
        logOutButton.addActionListener((ActionEvent event) -> {
            switchToLogInPanel();
        });
        refreshButton.setContentAreaFilled(false);
        refreshButton.addActionListener((ActionEvent event) -> {
            refresh();
        });
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(refreshButton);
        menuBar.add(logOutButton);

        cardPanel = new JPanel(new CardLayout());

        logInPanel.setContainer(this);

        cardPanel.add(logInPanel, logInPanel.getName());
        cardPanel.add(managerPanel, managerPanel.getName());
        cardPanel.add(supervisorPanel, supervisorPanel.getName());

        add(menuBar, BorderLayout.NORTH);
        add(cardPanel);
    }

    public void switchCard(String cardName) {
        CardLayout layout = (CardLayout) cardPanel.getLayout();
        layout.show(cardPanel, cardName);
        refresh();
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

    public void refresh() {
        managerPanel.refresh();
        supervisorPanel.refresh();
    }

}