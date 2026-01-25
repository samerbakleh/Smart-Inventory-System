package com.inventory.app.viewer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TaskActionCellRenderer extends JPanel
        implements TableCellRenderer {

    private  JButton editBtn = new JButton();
    private  JButton cancelBtn = new JButton();


    public TaskActionCellRenderer(boolean isTrue) {
        editBtn.setPreferredSize(new Dimension(20,13));
        cancelBtn.setPreferredSize(new Dimension(20,13));
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        editBtn.setFocusable(false);
        cancelBtn.setFocusable(false);
        if (isTrue)
        add(editBtn);
        else
        add(cancelBtn);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        editBtn.setText("ed");
        cancelBtn.setText("x");

        return this;
    }
}

