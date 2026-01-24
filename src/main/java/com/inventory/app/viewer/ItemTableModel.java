package com.inventory.app.viewer;

import com.inventory.app.model.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    private final String[] cols = {"ID","Name","Price","Qty","Min","Unit","Category"};
    private List<Item> data = new ArrayList<>();

    public void setData(List<Item> items) {
        data = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
        fireTableDataChanged();
    }

    public Item getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Object getValueAt(int r, int c) {
        Item it = data.get(r);
        return switch (c) {
            case 0 -> it.getId();
            case 1 -> it.getName();
            case 2 -> it.getPrice();
            case 3 -> it.getQuantity();
            case 4 -> it.getMinLimit();
            case 5 -> it.getUnit();
            case 6 -> (it.getCategory() != null ? it.getCategory().getName() : "");
            default -> "";
        };
    }
}
