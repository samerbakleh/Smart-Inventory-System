package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.ProductTypeController;
import com.inventory.app.model.ProductType;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.RemovableLabelPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddProductLineDialog extends JDialog {

    private ProductLineController productLineController;
    private ProductTypeController productTypeController;

    private JTextField nameField;
    private JLabel invalidNameLabel;
    private JComboBox<String> productNameBox;
    private JLabel invalidSubtypeLabel;
    private RemovableLabelPanel subtypePanel;

    private ManagerPanel container;
    private HashMap<String, List<String>> productTypeNames;

    public AddProductLineDialog(ProductLineController productLineController, ProductTypeController productTypeController,
                                ManagerPanel container) {
        this.productLineController = productLineController;
        this.productTypeController = productTypeController;
        this.container = container;
        updateProductTypes();
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.white);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addComponents();
        pack();
    }

    public void resetDisplay() {
        pack();
        setLocationRelativeTo(container);
        setVisible(true);
    }

    public void updateProductTypes() {
        productTypeNames = new HashMap<>();
        List<ProductType> productTypes = productTypeController.getProductTypes();
        if(productTypes.isEmpty()) {
            return;
        }
        String productName = productTypes.get(0).getName();
        List<String> subtypes = new ArrayList<>();
        for(ProductType pt : productTypes) {
            if(pt.getName().equals(productName)) {
                subtypes.add(pt.getSubtype());
            } else {
                productTypeNames.put(productName, subtypes);
                productName = pt.getName();
                subtypes = new ArrayList<>();
                productNameBox.addItem(productName);
            }
            productTypeNames.put(productName, subtypes);
        }
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        nameField = new JTextField(30);
        invalidNameLabel = new JLabel("name must only contain letters, numbers, and -");
        productNameBox = new JComboBox<>();
        productNameBox.addItemListener((ItemEvent event) -> {
            if(event.getStateChange() == ItemEvent.SELECTED) {
                subtypePanel.clearContent();
                String name = (String) event.getItem();
                for(String subtype : productTypeNames.get(name)) {
                    subtypePanel.addLabel(name + " " + subtype);
                }
            }
        });
        nameField.setBorder(BorderFactory.createTitledBorder("Production Line Name"));
        JLabel productNameLabel = new JLabel("Product Name");
        productNameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        invalidSubtypeLabel = new JLabel("Add at least one product type");
        invalidNameLabel.setForeground(Color.white);
        invalidNameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(12f));
        invalidSubtypeLabel.setForeground(Color.white);
        invalidSubtypeLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(12f));

        JLabel subtypeLabel = new JLabel("Product subtypes : ");
        subtypeLabel.setHorizontalAlignment(JLabel.LEFT);
        subtypePanel = new RemovableLabelPanel();
        subtypePanel.setBackground(Color.white);
        subtypePanel.setLabelColor(Color.white);
        subtypePanel.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));

        JButton addButton = new JButton("Add");
        addButton.setBackground(AppConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.white);
        addButton.addActionListener((ActionEvent event) -> {
            submit();
        });


        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 0, 20);
        add(nameField, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        add(invalidNameLabel, gbc);
        gbc.insets = new Insets(5, 25, 0, 0);
        add(productNameLabel, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(productNameBox, gbc);
        gbc.insets = new Insets(5, 25, 5, 0);
        add(subtypeLabel, gbc);
        gbc.insets = new Insets(0, 25, 5, 0);
        add(invalidSubtypeLabel, gbc);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        add(subtypePanel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 35, 5, 35);
        add(addButton, gbc);
    }

    private void submit() {
        if(subtypePanel.isEmpty()) {
            invalidSubtypeLabel.setForeground(Color.red);
            return;
        }
        String productLineName = nameField.getText();
        if(!productLineController.addProductLine(productLineName, subtypePanel.getContent())) {
            invalidNameLabel.setForeground(Color.red);
            return;
        }
        container.refreshTable();
        invalidSubtypeLabel.setForeground(Color.white);
        invalidNameLabel.setForeground(Color.white);
        nameField.setText("");
        subtypePanel.clearContent();
        this.dispose();
    }

}
