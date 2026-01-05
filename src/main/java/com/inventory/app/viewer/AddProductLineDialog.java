package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.RemovableLabelPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddProductLineDialog extends JDialog {

    private ProductLineController productLineController;

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

        JTextField nameField = new JTextField(30);
        JLabel invalidNameLabel = new JLabel("Production Line name must only contain letters, numbers, and - symbol");
        JTextField productNameField = new JTextField(30);
        JLabel invalidProductNameLabel = new JLabel("Product name must only contain letters, numbers, and - symbol");
        nameField.setBorder(BorderFactory.createTitledBorder("Production Line Name"));
        productNameField.setBorder(BorderFactory.createTitledBorder("Product Name"));
        invalidNameLabel.setForeground(Color.white);
        invalidProductNameLabel.setForeground(Color.white);

        JLabel subtypeLabel = new JLabel("Add subtypes to the product");
        subtypeLabel.setHorizontalAlignment(JLabel.LEFT);
        JTextField subtypeField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
        RemovableLabelPanel subtypePanel = new RemovableLabelPanel();
        submitButton.setBackground(AppConstants.SECONDARY_COLOR);
        submitButton.setForeground(Color.white);
        submitButton.addActionListener((ActionEvent event) -> {
            String input = subtypeField.getText();
            if(!input.isBlank()) {
                subtypePanel.addLabel(input);
                subtypeField.setText("");
                this.pack();
            }
        });
        subtypePanel.setBackground(Color.white);
        subtypePanel.setLabelColor(Color.white);
        subtypePanel.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));

        JButton addButton = new JButton("Add");
        addButton.setBackground(AppConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.white);
        addButton.addActionListener((ActionEvent event) -> {
            String productLineName = nameField.getText();
            if(productLineController.validateProductLineName(productLineName)) {
                invalidNameLabel.setForeground(Color.white);
                nameField.setText("");
                /*

                add product line

                 */
                this.dispose();
            } else {
                invalidNameLabel.setForeground(Color.red);
            }
            /*

            check for product types to add with product line

             */
        });


        gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);
        add(productNameField, gbc);
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

}
