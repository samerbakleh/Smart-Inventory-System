package com.inventory.app.viewer;

import com.inventory.app.controller.TaskController;
import com.inventory.app.model.Task;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class TaskActionCellEditor extends AbstractCellEditor
        implements TableCellEditor{

    private JPanel panel;
    private JButton editBtn;
    private JButton cancelBtn;

    private JTable table;
    private TaskController controller;
    private JFrame parent;

    public TaskActionCellEditor(JTable table,
                            TaskController controller,
                            JFrame parent,boolean isTrue) {

        this.table = table;
        this.controller = controller;
        this.parent = parent;

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        editBtn = createButton("ed");
        cancelBtn = createButton("x");
        editBtn.setPreferredSize(new Dimension(20,13));
        cancelBtn.setPreferredSize(new Dimension(20,13));
        if (isTrue)
        panel.add(editBtn);
        else
        panel.add(cancelBtn);

        editBtn.addActionListener(e -> openEditDialog());
        cancelBtn.addActionListener(e -> cancelTask());
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(30, 25));
        return btn;
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        int taskId = (int) table.getValueAt(row, 0);

        Task task = controller.getTaskById(taskId);

        new TaskEditDialog(parent, controller, task)
                .setVisible(true);

        fireEditingStopped();
    }

    private void cancelTask() {
        int row = table.getSelectedRow();
        int taskId = (int) table.getValueAt(row, 0);

        controller.cancelTask(taskId);

        fireEditingStopped();
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column) {
        editBtn.setText("ed");
        cancelBtn.setText("x");

        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
