package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProductLineInfoDialog extends JDialog {

    private ProductLineController productLineController;
    private ProductLine productLine;

    private JTextArea noteArea;

    public ProductLineInfoDialog(ProductLineController productLineController) {
        this.productLineController = productLineController;
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {

            }
        });
        addComponents();
    }

    public void display(ProductLine productLine) {
        this.productLine = productLine;

    }

    private void addComponents() {
        noteArea = new JTextArea(3, 10);
        JRadioButton activateButton = new JRadioButton("activate");
        JRadioButton stopButton = new JRadioButton("stop");
        JRadioButton maintainButton = new JRadioButton("maintain");
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(activateButton);
        statusGroup.add(stopButton);
        statusGroup.add(maintainButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;

        add(noteArea, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(activateButton, gbc);
        gbc.gridx = 1;
        add(stopButton, gbc);
        gbc.gridx = 2;
        add(maintainButton, gbc);
    }

}
