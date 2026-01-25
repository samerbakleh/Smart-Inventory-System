package com.inventory.app.viewer;

import com.inventory.app.controller.TaskController;
import com.inventory.app.model.Task;

import javax.swing.*;
import java.time.LocalDate;
import java.awt.*;

import java.time.format.DateTimeFormatter;

public class TaskEditDialog extends JDialog {

    private final TaskController controller;
    private final Task task;

    public TaskEditDialog(JFrame parent,
                          TaskController controller,
                          Task task) {

        super(parent, "Edit Task", true);

        this.controller = controller;
        this.task = task;

        setSize(600, 230);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 5));

        JComboBox<String>productCombo= new JComboBox<>(controller.getProductTypesFullNames().toArray(new String[0]));

        JTextField clientField = new JTextField(task.getClientName());
        JTextField quantityField =
                new JTextField(String.valueOf(task.getQuantityRequired()));
        JTextField dateField =
                new JTextField(task.getDeliveryDate().toString());

        formPanel.add(new JLabel("Product"));
        formPanel.add(new JLabel("Client"));
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(new JLabel("Delivery Date"));

        formPanel.add(productCombo);
        formPanel.add(clientField);
        formPanel.add(quantityField);
        formPanel.add(dateField);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        saveBtn.addActionListener(e -> {
            try {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd");

                task.setProductName( (String) productCombo.getSelectedItem());
                task.setClientName(clientField.getText());
                task.setQuantityRequired(
                        Integer.parseInt(quantityField.getText()));
                task.setDeliveryDate(
                        LocalDate.parse(dateField.getText(), formatter));

                controller.updateTask(task);
                dispose();

            } catch (Exception ex) {
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
