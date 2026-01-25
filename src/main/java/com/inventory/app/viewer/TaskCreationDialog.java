package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TaskCreationDialog extends JDialog{

    public TaskCreationDialog(JFrame frame, TaskController controller, ProductLineController productLineController, ItemController itemController){
            super(frame,"Create Task", true);
            setSize(600,200);
            setLocationRelativeTo(frame);

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 5));

        JComboBox<String>productCombo= new JComboBox<>(controller.getProductTypesFullNames().toArray(new String[0]));

        JTextField clientField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField deliveryDateField = new JTextField();

        formPanel.add(new JLabel("Product Name"));
        formPanel.add(new JLabel("Client"));
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(new JLabel("Delivery Date"));

        formPanel.add(productCombo);
        formPanel.add(clientField);
        formPanel.add(quantityField);
        formPanel.add(deliveryDateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        add(formPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            try {
                ProductType pt =   controller.getTaskService().getProductTypeRepository().findByFullName((String) productCombo.getSelectedItem());
                int plId =  productLineController.getLeastBusyProductLineID(pt);
                Task task = new Task(Integer.parseInt(quantityField.getText()),
                        pt,
                        clientField.getText(),
                       plId,
                        LocalDate.parse(deliveryDateField.getText())
                );
                ProductLine pl = productLineController.getProductLineService().getProductLineRepository().findByID(plId);
                if (itemController.getCanProduced(pt,Integer.parseInt(quantityField.getText()))) {
                    pl.addTask(task);
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
    }
}
