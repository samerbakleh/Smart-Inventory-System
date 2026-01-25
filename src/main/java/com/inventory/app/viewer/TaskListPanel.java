package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;

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

    public TaskListPanel(TaskController taskController, JFrame frame, ProductLineController productLineController) {

        this.taskController = taskController;
        this.frame = frame;

        setLayout(new BorderLayout(10, 10));
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        JComboBox<String> filterBox =
                new JComboBox<>(new String[]{"All Tasks", "In Progress", "Completed"});

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTaskBtn = new JButton("Add Task");
        JButton refreshTaskBtn = new JButton("Refresh");

        topPanel.setBackground(Color.white);
        topPanel.add(new JLabel("Filter: "));
        topPanel.add(filterBox);
        topPanel.add(addTaskBtn);
        topPanel.add(refreshTaskBtn);

        add(topPanel, BorderLayout.NORTH);

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
                    new TaskCreationDialog(frame, taskController,productLineController);
            dialog.setVisible(true);
            refresh(taskController.getAllTasks());
        });

        refreshTaskBtn.addActionListener(e ->
                refresh(taskController.getAllTasks())
        );

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
