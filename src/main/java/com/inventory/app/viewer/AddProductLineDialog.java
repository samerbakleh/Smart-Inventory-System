package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.RemovableLabelPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddProductLineDialog extends JDialog {

    private ProductLineController productLineController;

    private JTextField nameField;
    private JLabel invalidNameLabel;
    private JTextField subtypeField;
    private RemovableLabelPanel subtypePanel;

    public AddProductLineDialog(ProductLineController productLineController) {
        this.productLineController = productLineController;
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.white);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addComponents();
        pack();
    }

    public void resetDisplay() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;

        nameField = new JTextField(30);
        invalidNameLabel = new JLabel("name must only contain letters, numbers, and -");
        JTextField productNameField = new JTextField(30);
        JLabel invalidProductNameLabel = new JLabel("name must only contain letters, numbers, and -");
        nameField.setBorder(BorderFactory.createTitledBorder("Production Line Name"));
        productNameField.setBorder(BorderFactory.createTitledBorder("Product Name"));
        invalidNameLabel.setForeground(Color.white);
        invalidProductNameLabel.setForeground(Color.white);
        invalidNameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(11f));
        invalidProductNameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(11f));

        JLabel subtypeLabel = new JLabel("Add subtypes to the product");
        subtypeLabel.setHorizontalAlignment(JLabel.LEFT);
        subtypeField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
        subtypePanel = new RemovableLabelPanel();
        submitButton.setBackground(AppConstants.SECONDARY_COLOR);
        submitButton.setForeground(Color.white);
        submitButton.addActionListener((ActionEvent event) -> {
            submitSubtype();
        });
        subtypePanel.setBackground(Color.white);
        subtypePanel.setLabelColor(Color.white);
        subtypePanel.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));

        JButton addButton = new JButton("Add");
        addButton.setBackground(AppConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.white);
        addButton.addActionListener((ActionEvent event) -> {
            disposeSuccessfully();
        });


        gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);
        gbc.insets = new Insets(0, 5, 0, 0);
        add(invalidNameLabel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        add(productNameField, gbc);
        gbc.insets = new Insets(0, 5, 0, 0);
        add(invalidProductNameLabel, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 0);
        add(subtypeLabel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 5, 0);
        add(subtypeField, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 5, 5, 5);
        add(submitButton, gbc);
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        add(subtypePanel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 15, 5, 15);
        add(addButton, gbc);
    }

    private void submitSubtype() {
        String input = subtypeField.getText();
        if(!input.isBlank()/*&& check subtype name*/) {
            subtypePanel.addLabel(input);
            subtypeField.setText("");
            this.pack();
        }
    }

    private void disposeSuccessfully() {
        String productLineName = nameField.getText();
        if(productLineController.addProductLine(productLineName, /*productTypes*/null)) {
            invalidNameLabel.setForeground(Color.white);
            nameField.setText("");
            this.dispose();
        } else {
            invalidNameLabel.setForeground(Color.red);
        }
            /*

            check for product types to add with product line

             */
    }

}
