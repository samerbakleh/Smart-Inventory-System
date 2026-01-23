package com.inventory.app.viewer;

import com.inventory.app.model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    private final String[] cols = {"ID", "Name", "Category", "Price", "Qty", "Min"};
    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
        fireTableDataChanged();
    }

    public Item getItemAt(int modelRow) {
        if (modelRow < 0 || modelRow >= items.size()) return null;
        return items.get(modelRow);
    }

    public int getRowCount() { return items.size(); }
    public int getColumnCount() { return cols.length; }
    public String getColumnName(int col) { return cols[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> Integer.class;
            case 3 -> Double.class;
            case 4, 5 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item it = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> it.getId();
            case 1 -> it.getName();
            case 2 -> it.getCategory() == null ? "" : it.getCategory().getName();
            case 3 -> it.getPrice();
            case 4 -> it.getQuantity();
            case 5 -> it.getMinLimit();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
