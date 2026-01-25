package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.StarRating;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProductLineInfoDialog extends JDialog {

    private ProductLineController productLineController;

    private JTextField noteField;
    private StarRating starRating;
    private JToggleButton activateButton;
    private JToggleButton maintainButton;
    private JToggleButton stopButton;

    private ProductLine productLine;
    private ManagerPanel container;
    private int rowIndex;

    public ProductLineInfoDialog(ProductLineController productLineController) {
        this.productLineController = productLineController;
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addComponents();
    }

    public void setContainer(ManagerPanel container) {
        this.container = container;
    }

    public void display(ProductLine productLine, int rowIndex) {
        this.productLine = productLine;
        this.rowIndex = rowIndex;
        setTitle(productLine.toString());
        noteField.setText(productLine.getNote());
        starRating.setRating(productLine.getRating());
        String stringStatus = productLine.getStatus().toString();
        if(stringStatus.equals(activateButton.getActionCommand())) {
            activateButton.setSelected(true);
        } else if(stringStatus.equals(stopButton.getActionCommand())) {
            stopButton.setSelected(true);
        } else {
            maintainButton.setSelected(true);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents() {
        noteField = new JTextField(30);
        noteField.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));

        activateButton = new JToggleButton("activate");
        maintainButton = new JToggleButton("maintain");
        stopButton = new JToggleButton("stop");
        activateButton.setBackground(new Color(0, 180, 90));
        activateButton.setForeground(Color.white);
        maintainButton.setBackground(new Color(180, 180, 90));
        maintainButton.setForeground(Color.white);
        stopButton.setBackground(new Color(180, 0, 90));
        stopButton.setForeground(Color.white);
        activateButton.setActionCommand(ProductLineStatus.ACTIVE.toString());
        maintainButton.setActionCommand(ProductLineStatus.MAINTENANCE.toString());
        stopButton.setActionCommand(ProductLineStatus.STOPPED.toString());
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(activateButton);
        statusGroup.add(maintainButton);
        statusGroup.add(stopButton);

        starRating = new StarRating(14);
        JButton okButton = new JButton("OK");
        okButton.setBackground(AppConstants.SECONDARY_COLOR);
        okButton.setForeground(Color.white);
        okButton.addActionListener((ActionEvent event) -> {
            productLineController.changeProductLineNote(productLine, noteField.getText());
            productLineController.changeProductLineRating(productLine, starRating.getRating());
            String command = statusGroup.getSelection().getActionCommand();
            productLineController.changeProductLineStatus(productLine, command);
            container.refreshRowContent(rowIndex);
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.33;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(activateButton, gbc);
        gbc.gridx = 1;
        add(maintainButton, gbc);
        gbc.gridx = 2;
        add(stopButton, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(noteField, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        add(starRating, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(okButton, gbc);
    }

}
