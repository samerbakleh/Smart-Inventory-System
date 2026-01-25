package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SupervisorPanel extends JPanel {

    private JPanel cardPanel;
    private ItemViewerPanel itemViewerPanel;
    private ProductViewerPanel productViewerPanel;
    private ProductLineViewerPanel productLineViewerPanel;
    private TaskListPanel taskListPanel;

    public SupervisorPanel(JFrame container, ItemController itemController, ProductLineController productLineController,
                           TaskController taskController) {
        super(new GridBagLayout());
        this.itemViewerPanel = new ItemViewerPanel(itemController);
        this.productViewerPanel = new ProductViewerPanel();
        this.productLineViewerPanel = new ProductLineViewerPanel(productLineController);
        this.taskListPanel = new TaskListPanel(taskController, container);
        setName("Supervisor panel");
        setBackground(Color.white);
        addComponents();
    }

    public void refresh() {
        productLineViewerPanel.refresh();
    }

    private void addComponents() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        GradientPanel topPanel = new GradientPanel(AppConstants.PRIMARY_COLOR, AppConstants.SECONDARY_COLOR);
        topPanel.setLayout(new GridBagLayout());
        ShadowedLabel supervisorLabel = new ShadowedLabel("Supervisor Dashboard");
        supervisorLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(35f));
        supervisorLabel.setForeground(Color.white);
        supervisorLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        topPanel.add(supervisorLabel, gbc);

        JToggleButton itemsButton = new JToggleButton("Items");
        JToggleButton productsButton = new JToggleButton("Products");
        JToggleButton productLinesButton = new JToggleButton("Production Lines");
        JToggleButton tasksButton = new JToggleButton("Tasks");
        itemsButton.setBackground(AppConstants.PRIMARY_COLOR);
        itemsButton.setForeground(Color.white);
        itemsButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        productsButton.setBackground(AppConstants.PRIMARY_COLOR);
        productsButton.setForeground(Color.white);
        productsButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        productLinesButton.setBackground(AppConstants.PRIMARY_COLOR);
        productLinesButton.setForeground(Color.white);
        productLinesButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        tasksButton.setBackground(AppConstants.PRIMARY_COLOR);
        tasksButton.setForeground(Color.white);
        tasksButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        itemsButton.addActionListener((ActionEvent event) -> {
            switchToItemsPanel();
        });
        productsButton.addActionListener((ActionEvent event) -> {
            switchToProductsPanel();
        });
        productLinesButton.addActionListener((ActionEvent event) -> {
            switchToProductLinesPanel();
        });
        tasksButton.addActionListener((ActionEvent event) -> {
            switchToTasksPanel();
        });
        ButtonGroup navigationGroup = new ButtonGroup();
        navigationGroup.add(itemsButton);
        navigationGroup.add(productsButton);
        navigationGroup.add(productLinesButton);
        navigationGroup.add(tasksButton);


        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(itemViewerPanel, itemViewerPanel.getName());
        cardPanel.add(productViewerPanel, productViewerPanel.getName());
        cardPanel.add(productLineViewerPanel, productLineViewerPanel.getName());
        cardPanel.add(taskListPanel, taskListPanel.getName());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(topPanel, gbc);
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        gbc.weighty = 0.05;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(itemsButton, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(productsButton, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(productLinesButton, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(tasksButton, gbc);
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(cardPanel, gbc);
    }

    public void switchCard(String cardName) {
        CardLayout layout = (CardLayout) cardPanel.getLayout();
        layout.show(cardPanel, cardName);
    }
    public void switchToItemsPanel() {
        switchCard(itemViewerPanel.getName());
    }
    public void switchToProductsPanel() {
        switchCard(productViewerPanel.getName());
    }
    public void switchToProductLinesPanel() {
        switchCard(productLineViewerPanel.getName());
    }
    public void switchToTasksPanel() {
        switchCard(taskListPanel.getName());
    }

}
