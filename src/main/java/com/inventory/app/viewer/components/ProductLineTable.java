package com.inventory.app.viewer.components;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.enums.ProductLineStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductLineTable extends JTable {

    private DefaultTableModel model;
    private List<ProductLine> productLines;

    public ProductLineTable(List<ProductLine> productLines) {
        this.productLines = productLines;
        initModel();
        setModel(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumnRenderer();
    }

    private void initModel() {
        String[] columnName = {"ID", "Name", "Status", "Tasks", "Rating", "Note"};
        model = new DefaultTableModel(generateTableContent(), columnName) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int colIndex) {
                final int ID_COL_INDEX = 0;
                final int STATUS_COL_INDEX = 2;
                final int RATING_COL_INDEX = 4;
                if(colIndex == ID_COL_INDEX) {
                    return ProductLine.class;
                } else if(colIndex == STATUS_COL_INDEX) {
                    return ProductLineStatus.class;
                } else if(colIndex == RATING_COL_INDEX) {
                    return Integer.class;
                }
                return super.getColumnClass(colIndex);
            }
        };
    }

    private void setColumnRenderer() {
        DefaultTableCellRenderer productLineRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(CENTER);
                label.setText(((ProductLine) value).getID() + "");
                return label;
            }
        };
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        };
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(CENTER);
                label.setText(label.getText().toLowerCase());
                if(isSelected) {
                    label.setBackground(table.getSelectionBackground());
                } else {
                    Color c = switch((ProductLineStatus) value) {
                        case ProductLineStatus.ACTIVE -> new Color(0, 255, 140, 100);
                        case ProductLineStatus.MAINTENANCE -> new Color(255, 255, 140, 100);
                        case ProductLineStatus.STOPPED -> new Color(255, 0, 140, 100);
                    };
                    if (row % 2 == 1) {
                        c = new Color(c.getRed() * 9 / 10, c.getGreen() * 9 / 10, c.getBlue() * 9 / 10, c.getAlpha());
                    }
                    label.setBackground(c);
                }
                return label;
            }
        };
        DefaultTableCellRenderer ratingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Star s = new Star(15);
                s.setText(((Integer) value).toString());
                s.setForeground(Color.darkGray);
                s.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                return s;
            }
        };
        setDefaultRenderer(ProductLine.class, productLineRenderer);
        setDefaultRenderer(ProductLineStatus.class, statusRenderer);
        setDefaultRenderer(Integer.class, ratingRenderer);
        setDefaultRenderer(Object.class, textRenderer);
    }

    private Object[][] generateTableContent() {
        int n = productLines.size();
        Object[][] result = new Object[n][6];
        for(int i = 0; i < productLines.size(); i++) {
            ProductLine pl = productLines.get(i);
            result[i] = new Object[]{pl, pl.getName(), pl.getStatus(), pl.getNumOfTasks(), pl.getRating(), pl.getNote()};
        }
        return result;
    }

}
