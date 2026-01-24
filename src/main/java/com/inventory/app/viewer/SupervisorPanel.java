package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import java.awt.*;

public class SupervisorPanel extends JPanel {

    private JPanel cardPanel;
    private ItemViewerPanel itemViewerPanel;
    private ProductViewerPanel productViewerPanel;
    private ProductLineViewerPanel productLineViewerPanel;
    private TaskViewerPanel taskViewerPanel;

    public SupervisorPanel(ProductLineController productLineController) {
        super(new GridBagLayout());
        /*
        this.itemViewerPanel = new ItemViewerPanel();
        this.productViewerPanel = new ProductViewerPanel();
        this.taskViewerPanel = new TaskViewerPanel();
         */
        this.productLineViewerPanel = new ProductLineViewerPanel(productLineController);
        setName("Supervisor panel");
        setBackground(Color.white);
        addComponents();
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
        cardPanel = new JPanel(new CardLayout());
        /*

        add other viewer panels too ...

         */
        cardPanel.add(productLineViewerPanel, productLineViewerPanel.getName());

        gbc.weighty = 0.13;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(topPanel, gbc);
        gbc.weightx = 1.0;
        gbc.weighty = 0.87;
        add(cardPanel, gbc);
    }

}
