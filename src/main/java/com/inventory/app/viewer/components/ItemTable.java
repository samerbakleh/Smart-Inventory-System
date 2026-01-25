package com.inventory.app.viewer.components;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.enums.Unit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemTable extends JTable {

    private DefaultTableModel model;
    private List<Item> items;

    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_PRICE = 2;
    private static final int COL_QTY = 3;
    private static final int COL_UNIT = 4;
    private static final int COL_MIN = 5;
    private static final int COL_CAT = 6;

    private static final double EPS = 1e-9;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ItemTable(List<Item> items) {
        this.items = (items == null) ? new ArrayList<>() : items;
        initModel();

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);

        setColumnRenderer();
    }

    public void setItems(List<Item> items) {
        this.items = (items == null) ? new ArrayList<>() : items;
        initModel();
    }

    public Item getSelectedItem() {
        int r = getSelectedRow();
        if (r < 0) return null;
        int mr = convertRowIndexToModel(r);
        if (mr < 0 || mr >= this.items.size()) return null;
        return this.items.get(mr);
    }

    private void initModel() {
        String[] cols = {"ID", "Name", "Price", "Qty", "Unit", "Min", "Category"};

        model = new DefaultTableModel(generateContent(), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }

            @Override
            public Class<?> getColumnClass(int colIndex) {
                return switch (colIndex) {
                    case COL_ID -> Item.class;          // نخزن Item لنجيب الـ id بالrenderer
                    case COL_PRICE, COL_QTY, COL_MIN -> Double.class;
                    case COL_UNIT -> Unit.class;
                    case COL_CAT -> Category.class;
                    default -> Object.class;
                };
            }
        };
        setModel(model);
    }

    private Object[][] generateContent() {
        Object[][] out = new Object[items.size()][7];
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            out[i] = new Object[]{
                    it,
                    it.getName(),
                    it.getPrice(),
                    it.getQuantity(),
                    it.getUnit(),
                    it.getMinLimit(),
                    it.getCategory()
            };
        }
        return out;
    }

    private void setColumnRenderer() {

        DefaultTableCellRenderer zebraText = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(CENTER);
                applyZebra(t, l, isSelected, row);
                return l;
            }
        };

        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(CENTER);
                l.setText(v instanceof Item it ? String.valueOf(it.getId()) : "");
                applyZebra(t, l, isSelected, row);
                return l;
            }
        };

        DefaultTableCellRenderer categoryRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(CENTER);
                l.setText(v instanceof Category c ? c.getName() : "");
                applyZebra(t, l, isSelected, row);
                return l;
            }
        };

        DefaultTableCellRenderer unitRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(CENTER);
                l.setText(v == null ? "" : v.toString().toLowerCase());
                applyZebra(t, l, isSelected, row);
                return l;
            }
        };

        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(CENTER);

                if (v instanceof Double d) l.setText(df.format(d));
                else l.setText("");

                // تلوين Qty حسب الحالة
                if (!isSelected && col == COL_QTY) {
                    Item it = getItemAtViewRow(row);
                    if (it != null) {
                        double qty = it.getQuantity();
                        double min = it.getMinLimit();

                        Color c;
                        if (qty <= EPS) c = new Color(255, 0, 140, 90);       // Out
                        else if (qty <= min + EPS) c = new Color(255, 255, 140, 120); // Low
                        else c = new Color(0, 255, 140, 80);                  // Safe-ish

                        if (row % 2 == 1) {
                            c = new Color(c.getRed() * 9 / 10, c.getGreen() * 9 / 10, c.getBlue() * 9 / 10, c.getAlpha());
                        }
                        l.setOpaque(true);
                        l.setBackground(c);
                        l.setForeground(t.getForeground());
                        return l;
                    }
                }

                applyZebra(t, l, isSelected, row);
                return l;
            }
        };

        setDefaultRenderer(Item.class, idRenderer);
        setDefaultRenderer(Category.class, categoryRenderer);
        setDefaultRenderer(Unit.class, unitRenderer);
        setDefaultRenderer(Double.class, numberRenderer);
        setDefaultRenderer(Object.class, zebraText);
    }

    private Item getItemAtViewRow(int viewRow) {
        int mr = convertRowIndexToModel(viewRow);
        if (mr < 0 || mr >= items.size()) return null;
        return items.get(mr);
    }

    private void applyZebra(JTable t, JLabel l, boolean isSelected, int row) {
        l.setOpaque(true);
        if (isSelected) {
            l.setBackground(t.getSelectionBackground());
            l.setForeground(t.getSelectionForeground());
        } else {
            Color base = (row % 2 == 0) ? Color.WHITE : new Color(247, 249, 253);
            l.setBackground(base);
            l.setForeground(t.getForeground());
        }
    }
}
