package com.inventory.app.viewer;
import com.inventory.app.util.AppConstants;

import javax.swing.*;
import java.awt.*;

public class AddProductLineDialog extends JDialog {

    public AddProductLineDialog() {
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.white);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addComponents();
        pack();
    }

    public void resetDisplay() {
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;

        JTextField nameField = new JTextField(30);
        JTextField productNameField = new JTextField(30);
        nameField.setBorder(BorderFactory.createTitledBorder("Production Line Name"));
        productNameField.setBorder(BorderFactory.createTitledBorder("Product Name"));

        JLabel subtypeLabel = new JLabel("Add subtypes to the product");
        subtypeLabel.setHorizontalAlignment(JLabel.LEFT);
        JTextField subtypeField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(AppConstants.SECONDARY_COLOR);
        submitButton.setForeground(Color.white);

        JButton addButton = new JButton("Add");
        addButton.setBackground(AppConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.white);


        gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);
        add(productNameField, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 0);
        add(subtypeLabel, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 0, 0);
        add(subtypeField, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 5, 0, 5);
        add(submitButton, gbc);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 15, 5, 15);
        add(addButton, gbc);
    }

}
