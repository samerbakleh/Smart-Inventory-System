package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.enums.Unit;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.inventory.app.service.InventoryService.EPS;

public class ItemViewerPanel extends GradientPanel {

    private final ItemController controller;

    private final JTextField searchField = new JTextField(18);
    private final JComboBox<Object> categoryBox = new JComboBox<>();
    private final JLabel statusLabel = new JLabel(" ");

    private final ItemTableModel model = new ItemTableModel();
    private final JTable table = new JTable(model);
    private final JComboBox<Object> unitBox = new JComboBox<>();
    private final JComboBox<String> stockBox = new JComboBox<>(
            new String[]{"All", "Low Stock (<= Min)", "Out of Stock (=0)", "In Stock (>0)", "Safe (> Min)"}
    );

    private List<Item> allItems = new ArrayList<>();

    public ItemViewerPanel(ItemController controller) {
        super(new Color(250, 252, 255), new Color(236, 244, 255), DIAGONAL2); // فاتح
        this.controller = controller;

        buildUI();
        refreshAll();
        applyFilters();
        hookFilters();
    }

    private void buildUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        styleCombo(unitBox);
        styleCombo(stockBox);

        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        card.add(buildTop(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildBottom(), BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        styleField(searchField);
        styleCombo(categoryBox);
        styleTable(table);
        statusLabel.setForeground(new Color(70, 70, 70));
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        JLabel title = new JLabel("Inventory - Items");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(20, 35, 70));
        top.add(title, BorderLayout.NORTH);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row1.setOpaque(false);

        JButton addBtn = btn("Add");
        JButton editBtn = btn("Edit");
        JButton delBtn = btnDanger("Delete");
        JButton addCatBtn = btn("Add Category");
        JButton reloadBtn = btn("Reload");

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        addCatBtn.addActionListener(e -> onAddCategory());
        reloadBtn.addActionListener(e -> {
            controller.reload();
            refreshAll();
            applyFilters();
        });

        row1.add(addBtn);
        row1.add(editBtn);
        row1.add(delBtn);
        row1.add(addCatBtn);
        row1.add(reloadBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row2.setOpaque(false);

        JLabel s = new JLabel("Search:");
        s.setForeground(new Color(90, 90, 90));
        JLabel c = new JLabel("Category:");
        c.setForeground(new Color(90, 90, 90));

        JButton reset = btn("Reset");
        reset.addActionListener(e -> {
            searchField.setText("");
            if (categoryBox.getItemCount() > 0) categoryBox.setSelectedIndex(0);
            applyFilters();
        });

        row2.add(s);
        row2.add(searchField);
        row2.add(c);
        row2.add(categoryBox);
        row2.add(reset);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(row1, BorderLayout.NORTH);
        wrap.add(row2, BorderLayout.SOUTH);

        top.add(wrap, BorderLayout.SOUTH);
        return top;
    }

    private JComponent buildCenter() {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(225, 232, 245)));
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);
        return sp;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JButton saveBtn = btnPrimary("Save");
        saveBtn.addActionListener(e -> {
            controller.save();
            JOptionPane.showMessageDialog(this, "Saved.");
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(saveBtn);

        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(right, BorderLayout.EAST);
        return bottom;
    }

    private void hookFilters() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        categoryBox.addActionListener(e -> applyFilters());
    }

    private void refreshAll() {
        allItems = controller.getAllItems();

        DefaultComboBoxModel<Object> m = new DefaultComboBoxModel<>();
        m.addElement("All Categories");
        for (Category cat : controller.getAllCategories()) m.addElement(cat);
        categoryBox.setModel(m);

        statusLabel.setText("Total items: " + allItems.size());
    }

    private void applyFilters() {
        String q = (searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);

        Object catSel = categoryBox.getSelectedItem();
        Integer catId = (catSel instanceof Category) ? ((Category) catSel).getId() : null;

        Object unitSel = unitBox.getSelectedItem();
        Unit unit = (unitSel instanceof Unit) ? (Unit) unitSel : null;

        int stockMode = stockBox.getSelectedIndex();

        List<Item> out = new ArrayList<>();
        for (Item it : allItems) {

            if (!q.isEmpty()) {
                String n = it.getName();
                if (n == null || !n.toLowerCase(Locale.ROOT).contains(q)) continue;
            }

            if (catId != null) {
                if (it.getCategory() == null || it.getCategory().getId() != catId) continue;
            }

            if (unit != null) {
                if (it.getUnit() != unit) continue;
            }

            double qty = it.getQuantity();
            double min = it.getMinLimit();

            boolean okStock = switch (stockMode) {
                case 0 -> true;                 // All
                case 1 -> qty <= min + EPS;     // Low Stock
                case 2 -> Math.abs(qty) <= EPS; // Out of Stock (==0)
                case 3 -> qty > EPS;            // In Stock (>0)
                case 4 -> qty > min + EPS;      // Safe (>Min)
                default -> true;
            };
            if (!okStock) continue;

            out.add(it);
        }

        model.setData(out);
        statusLabel.setText("Showing: " + out.size() + " / " + allItems.size());
    }

    private Item getSelectedItem() {
        int r = table.getSelectedRow();
        return model.getAt(r);
    }



    private void onAdd() {
        List<Category> cats = controller.getAllCategories();
        if (cats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No categories found. Add a category first.");
            return;
        }

        ItemForm f = ItemForm.showDialog(this, "Add Item", null, cats);
        if (f == null) return;

        String err = controller.addItem(f.name, f.price, f.qty, f.min, f.unit, f.categoryId);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Add Item", JOptionPane.WARNING_MESSAGE);

        refreshAll();
        applyFilters();
    }

    private void onEdit() {
        Item sel = getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        ItemForm f = ItemForm.showDialog(this, "Edit Item", sel, controller.getAllCategories());
        if (f == null) return;

        String err = controller.updateItem(sel.getId(), f.name, f.price, f.qty, f.min, f.unit, f.categoryId);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Edit Item", JOptionPane.WARNING_MESSAGE);

        refreshAll();
        applyFilters();
    }

    private void onDelete() {
        Item sel = getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete: " + sel.getName() + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        String err = controller.deleteItem(String.valueOf(sel.getId()));
        if (err != null) JOptionPane.showMessageDialog(this, err, "Delete", JOptionPane.WARNING_MESSAGE);

        refreshAll();
        applyFilters();
    }

    private void onAddCategory() {
        String name = JOptionPane.showInputDialog(this, "Category name:");
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) return;

        String err = controller.addCategory(name);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Add Category", JOptionPane.WARNING_MESSAGE);

        refreshAll();
        applyFilters();
    }


    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setForeground(new Color(30, 30, 30));
        b.setBackground(new Color(245, 248, 255));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 245)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return b;
    }

    private JButton btnPrimary(String text) {
        JButton b = btn(text);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(45, 110, 255));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private JButton btnDanger(String text) {
        JButton b = btn(text);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(220, 60, 70));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private void styleField(JTextField f) {
        f.setForeground(new Color(30, 30, 30));
        f.setCaretColor(new Color(30, 30, 30));
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 245)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(Color.WHITE);
        box.setForeground(new Color(30, 30, 30));
        box.setBorder(BorderFactory.createLineBorder(new Color(220, 228, 245)));
    }

    private void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowGrid(true);
        t.setGridColor(new Color(235, 238, 245));

        t.setBackground(Color.WHITE);
        t.setForeground(new Color(35, 35, 35));
        t.setSelectionBackground(new Color(220, 235, 255));
        t.setSelectionForeground(new Color(20, 20, 20));

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(245, 248, 255));
        header.setForeground(new Color(30, 30, 30));
        header.setReorderingAllowed(false);
    }


    private static class ItemTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Name", "Price", "Qty", "Unit", "Min", "Category"};
        private List<Item> data = new ArrayList<>();

        public void setData(List<Item> items) {
            this.data = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            Item it = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> it.getId();
                case 1 -> it.getName();
                case 2 -> String.format(Locale.ROOT, "%.2f", it.getPrice());
                case 3 -> String.format(Locale.ROOT, "%.2f", it.getQuantity());
                case 4 -> it.getUnit();
                case 5 -> String.format(Locale.ROOT, "%.2f", it.getMinLimit());
                case 6 -> (it.getCategory() == null ? "" : it.getCategory().getName());
                default -> "";
            };
        }
    }



    private static class ItemForm {
        String name, price, qty, min;
        Unit unit;
        int categoryId;

        static ItemForm showDialog(Component parent, String title, Item existing, List<Category> categories) {
            JTextField nameF = new JTextField(existing == null ? "" : existing.getName(), 18);
            JTextField priceF = new JTextField(existing == null ? "" : String.valueOf(existing.getPrice()), 10);
            JTextField qtyF = new JTextField(existing == null ? "" : String.valueOf(existing.getQuantity()), 10);
            JTextField minF = new JTextField(existing == null ? "" : String.valueOf(existing.getMinLimit()), 10);

            JComboBox<Unit> unitBox = new JComboBox<>(Unit.values());
            unitBox.setSelectedItem(existing == null ? Unit.PCS : existing.getUnit());

            JComboBox<Category> catBox = new JComboBox<>(categories.toArray(new Category[0]));
            if (existing != null && existing.getCategory() != null) {
                for (int i = 0; i < catBox.getItemCount(); i++) {
                    if (catBox.getItemAt(i).getId() == existing.getCategory().getId()) {
                        catBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
            p.add(new JLabel("Name:")); p.add(nameF);
            p.add(new JLabel("Price:")); p.add(priceF);
            p.add(new JLabel("Quantity:")); p.add(qtyF);
            p.add(new JLabel("Min Limit:")); p.add(minF);
            p.add(new JLabel("Unit:")); p.add(unitBox);
            p.add(new JLabel("Category:")); p.add(catBox);

            int ok = JOptionPane.showConfirmDialog(parent, p, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return null;

            ItemForm f = new ItemForm();
            f.name = nameF.getText();
            f.price = priceF.getText();
            f.qty = qtyF.getText();
            f.min = minF.getText();
            f.unit = (Unit) unitBox.getSelectedItem();
            Category c = (Category) catBox.getSelectedItem();
            f.categoryId = (c == null) ? 0 : c.getId();
            return f;
        }
    }
}
