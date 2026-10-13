package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TaskCreationDialog extends JDialog{

    public TaskCreationDialog(JFrame frame, TaskController controller, ProductLineController productLineController, ItemController itemController){
        super(frame,"Create Task", true);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.white);

        JComboBox<String>productCombo= new JComboBox<>(controller.getProductTypesFullNames().toArray(new String[0]));

        JTextField clientField = new JTextField(30);
        JTextField quantityField = new JTextField(30);
        JTextField deliveryDateField = new JTextField(30);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 0, 20);
        add(new JLabel("Product Name"), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(productCombo, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 0, 20);
        add(new JLabel("Client"), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(clientField, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 0, 20);
        add(new JLabel("Quantity"), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(quantityField, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 0, 20);
        add(new JLabel("Delivery Date"), gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(deliveryDateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.setBackground(Color.white);
        saveBtn.setBackground(AppConstants.SECONDARY_COLOR);
        saveBtn.setForeground(Color.white);
        cancelBtn.setBackground(AppConstants.SECONDARY_COLOR);
        cancelBtn.setForeground(Color.white);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                ProductType pt =   controller.getTaskService().getProductTypeRepository().findByFullName((String) productCombo.getSelectedItem());
                int plId =  productLineController.getLeastBusyProductLineID(pt);
                if (plId < 0) {
                    JOptionPane.showMessageDialog(this, "No production line makes " + pt,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Task task = new Task(Integer.parseInt(quantityField.getText()),
                        pt,
                        clientField.getText(),
                       plId,
                        LocalDate.parse(deliveryDateField.getText())
                );
                ProductLine pl = productLineController.getProductLineService().getProductLineRepository().findByID(plId);
                if (itemController.getCanProduced(pt,Integer.parseInt(quantityField.getText()))) {
                    controller.addTask(task, pl);
                    JOptionPane.showMessageDialog(this, "Task added successfully");
                }
                else
                {
                    JOptionPane.showMessageDialog(
                            this,
                            "No enough items",
                            "Broken Up",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                dispose();
            } catch (Exception ex){
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        cancelBtn.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(frame);
    }
}
