package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.util.AppConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

public class TaskListPanel extends JPanel{

    private DefaultTableModel tableModel;
    private TaskController taskController;
    private JFrame frame;

    private JTable table;
    private JComboBox<String> filterBox;

    public TaskListPanel(TaskController taskController, JFrame frame, ProductLineController productLineController, ItemController itemController) {

        this.taskController = taskController;
        this.frame = frame;

        setLayout(new GridBagLayout());
        setName("Tasks Viewer Panel");
        setBackground(Color.white);

        tableModel = new DefaultTableModel(
                new String[]{
                        "TaskID", "Quantity", "P-Name",
                        "PL-ID", "Client", "Status",
                        "Progress", "Edit", "Cancel"
                }, 0
        );

        table = new JTable(tableModel);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(23);

        filterBox = new JComboBox<>(new String[]{"All Tasks", "In Progress", "Completed"});

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTaskBtn = new JButton("Add Task");

        topPanel.setBackground(Color.white);
        topPanel.add(new JLabel("Filter: "));
        topPanel.add(filterBox);
        topPanel.add(addTaskBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(topPanel, gbc);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 40, 20, 40);
        add(new JScrollPane(table), gbc);

        filterBox.addActionListener(e -> {
            String selected = (String) filterBox.getSelectedItem();

            if ("In Progress".equals(selected)) {
                refresh(taskController.getTasksByStatus(TaskStatus.IN_PROGRESS));
            } else if ("Completed".equals(selected)) {
                refresh(taskController.getTasksByStatus(TaskStatus.COMPLETED));
            } else {
                refresh(taskController.getAllTasks());
            }
        });

        addTaskBtn.addActionListener(e -> {
            TaskCreationDialog dialog =
                    new TaskCreationDialog(frame, taskController,productLineController,itemController);
            dialog.setVisible(true);
            refresh(taskController.getAllTasks());
        });

        refresh(taskController.getAllTasks());

        int Edit_COLUMN_INDEX = 7;

        int Cancel_COLUMN_INDEX = 8;
        TableColumn editColumn =
                table.getColumnModel().getColumn(Edit_COLUMN_INDEX);

        editColumn.setCellRenderer(
                new TaskActionCellRenderer(true)
        );

        editColumn.setCellEditor(
                new TaskActionCellEditor(table, taskController, frame,true)
        );
        TableColumn cancelColumn =
                table.getColumnModel().getColumn(Cancel_COLUMN_INDEX);

        cancelColumn.setCellRenderer(
                new TaskActionCellRenderer(false)
        );

        cancelColumn.setCellEditor(
                new TaskActionCellEditor(table, taskController, frame,false)
        );
    }

    public void refresh() {
        String selected = (String) filterBox.getSelectedItem();

        if ("In Progress".equals(selected)) {
            refresh(taskController.getTasksByStatus(TaskStatus.IN_PROGRESS));
        } else if ("Completed".equals(selected)) {
            refresh(taskController.getTasksByStatus(TaskStatus.COMPLETED));
        } else {
            refresh(taskController.getAllTasks());
        }
    }

    private void refresh(ArrayList<Task> tasks) {
        tableModel.setRowCount(0);

        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                    task.getTaskID(),
                    task.getQuantityRequired(),
                    task.getProductName(),
                    task.getProductLineID(),
                    task.getClientName(),
                    task.getTaskStatus(),
                    task.getProgress() + "%",
                    "Edit",
                    "Cancel"
            });
        }
    }
}
