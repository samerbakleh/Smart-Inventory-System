package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.viewer.components.StarRating;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProductLineInfoDialog extends JDialog {

    private ProductLineController productLineController;
    private ProductLine productLine;

    private JTextField noteField;
    private StarRating starRating;

    public ProductLineInfoDialog(ProductLineController productLineController) {
        this.productLineController = productLineController;
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                productLineController.changeProductLineNote(productLine, noteField.getText());
                productLineController.changeProductLineRating(productLine, starRating.getRating());
            }
        });
        addComponents();
    }

    public void display(int id) {
        productLine = productLineController.getProductLineByID(id);
        setTitle(productLine.toString());
        noteField.setText(productLine.getNote());
        starRating.setRating(productLine.getRating());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponents() {
        noteField = new JTextField(40);
        JRadioButton activateButton = new JRadioButton("activate");
        JRadioButton stopButton = new JRadioButton("stop");
        JRadioButton maintainButton = new JRadioButton("maintain");
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(activateButton);
        statusGroup.add(stopButton);
        statusGroup.add(maintainButton);
        starRating = new StarRating(17);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        gbc.gridwidth = 1;
        add(activateButton, gbc);
        gbc.gridx = 1;
        add(stopButton, gbc);
        gbc.gridx = 2;
        add(maintainButton, gbc);
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        add(noteField, gbc);
        add(starRating, gbc);
    }

}
