package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.enums.Unit;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.ItemTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.inventory.app.service.InventoryService.EPS;

public class ItemViewerPanel extends JPanel {

    private final ItemController controller;

    private final JTextField searchField = new JTextField(18);
    private final JComboBox<Object> categoryBox = new JComboBox<>();
    private final JComboBox<Object> unitBox = new JComboBox<>();
    private final JComboBox<String> stockBox = new JComboBox<>(
            new String[]{"All", "Low Stock (<= Min)", "Out of Stock (=0)", "In Stock (>0)", "Safe (> Min)"}
    );

    private final JLabel statusLabel = new JLabel(" ");

    private ItemTable table;
    private List<Item> allItems = new ArrayList<>();

    public ItemViewerPanel(ItemController controller) {
        super(new GridBagLayout());
        this.controller = controller;

        setName("Items Viewer Panel");
        setBackground(Color.WHITE);

        addComponents();
        refresh();
        applyFilters();
        hookFilters();
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // ======= Top (Buttons + Filters) =======
        JPanel top = buildTop();
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 30, 8, 30);
        add(top, gbc);

        // ======= Table =======
        table = new ItemTable(Collections.emptyList());
        JScrollPane sp = new JScrollPane(table);

        styleTable();

        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 30, 10, 30);
        add(sp, gbc);

        // ======= Bottom (Status + Save) =======
        JPanel bottom = buildBottom();
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 30, 18, 30);
        add(bottom, gbc);

        // styles
        styleField(searchField);
        styleCombo(categoryBox);
        styleCombo(unitBox);
        styleCombo(stockBox);

        statusLabel.setForeground(new Color(80, 80, 80));
        statusLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(14f));
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Buttons row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row1.setOpaque(false);

        JButton addBtn = btn("Add");
        JButton editBtn = btn("Edit");
        JButton delBtn = btnDanger("Delete");
        JButton addCatBtn = btn("Add Category");

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        addCatBtn.addActionListener(e -> onAddCategory());

        row1.add(addBtn);
        row1.add(editBtn);
        row1.add(delBtn);
        row1.add(addCatBtn);

        // Filters row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row2.setOpaque(false);

        JLabel s = lbl("Search:");
        JLabel c = lbl("Category:");
        JLabel u = lbl("Unit:");
        JLabel st = lbl("Stock:");

        JButton reset = btn("Reset");
        reset.addActionListener(e -> {
            searchField.setText("");
            if (categoryBox.getItemCount() > 0) categoryBox.setSelectedIndex(0);
            if (unitBox.getItemCount() > 0) unitBox.setSelectedIndex(0);
            stockBox.setSelectedIndex(0);
            applyFilters();
        });

        row2.add(s);  row2.add(searchField);
        row2.add(c);  row2.add(categoryBox);
        row2.add(u);  row2.add(unitBox);
        row2.add(st); row2.add(stockBox);
        row2.add(reset);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(row1, BorderLayout.NORTH);
        wrap.add(row2, BorderLayout.SOUTH);

        top.add(wrap, BorderLayout.SOUTH);
        return top;
    }

    private JPanel buildBottom() {
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
        unitBox.addActionListener(e -> applyFilters());
        stockBox.addActionListener(e -> applyFilters());
    }

    public void refresh() {
        allItems = controller.getAllItems();

        // categories
        DefaultComboBoxModel<Object> catModel = new DefaultComboBoxModel<>();
        catModel.addElement("All Categories");
        for (Category cat : controller.getAllCategories()) catModel.addElement(cat);
        categoryBox.setModel(catModel);

        // units
        DefaultComboBoxModel<Object> unitModel = new DefaultComboBoxModel<>();
        unitModel.addElement("All Units");
        for (Unit u : Unit.values()) unitModel.addElement(u);
        unitBox.setModel(unitModel);

        statusLabel.setText("Total items: " + allItems.size());
    }

    private void applyFilters() {
        String q = (searchField.getText() == null) ? "" : searchField.getText().trim();
        Object catSel = categoryBox.getSelectedItem();
        Integer catId = (catSel instanceof Category) ? ((Category) catSel).getId() : null;

        Object unitSel = unitBox.getSelectedItem();
        Unit unit = (unitSel instanceof Unit) ? (Unit) unitSel : null;

        int stockMode = stockBox.getSelectedIndex();

        List<Item> filtered = new ArrayList<>(allItems);

        if (!q.isEmpty()) {
            List<Item> byName = controller.filterByName(q);
            filtered = intersectById(filtered, byName);
        }

        if (catId != null) {
            List<Item> byCat = controller.filterByCategory(catId);
            filtered = intersectById(filtered, byCat);
        }

        if (stockMode == 1) {
            List<Item> low = controller.lowStock();
            filtered = intersectById(filtered, low);
        } else if (stockMode == 2) { // Out
            List<Item> out = controller.outOfStock();
            filtered = intersectById(filtered, out);
        }


        ArrayList<Item> outFinal = new ArrayList<>();
        for (Item it : filtered) {

            if (unit != null && it.getUnit() != unit) continue;

            double qty = it.getQuantity();
            double min = it.getMinLimit();

            boolean okStock = switch (stockMode) {
                case 0 -> true;                  // All
                case 1 -> true;                  // Low already via controller
                case 2 -> true;                  // Out already via controller
                case 3 -> qty > EPS;             // In Stock
                case 4 -> qty > min + EPS;       // Safe
                default -> true;
            };
            if (!okStock) continue;

            outFinal.add(it);
        }

        table.setItems(outFinal);
        statusLabel.setText("Showing: " + outFinal.size() + " / " + allItems.size());
    }

    private static List<Item> intersectById(List<Item> a, List<Item> b) {
        if (a == null) return (b == null) ? new ArrayList<>() : new ArrayList<>(b);
        if (b == null) return new ArrayList<>(a);

        HashSet<Integer> ids = new HashSet<>();
        for (Item it : b) ids.add(it.getId());

        ArrayList<Item> out = new ArrayList<>();
        for (Item it : a) {
            if (ids.contains(it.getId())) out.add(it);
        }
        return out;
    }

    private Item getSelectedItem() {
        return table.getSelectedItem();
    }

    private void onAdd() {
        List<Category> cats = controller.getAllCategories();
        if (cats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No categories found. Add a category first.");
            return;
        }

        ItemFormDialog.Result r = ItemFormDialog.showAdd(this, cats);
        if (r == null) return;

        String err = controller.addItem(r.name, r.priceStr, r.qtyStr, r.minStr, r.unit, r.categoryId);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Add Item", JOptionPane.WARNING_MESSAGE);

        refresh();
        applyFilters();
    }

    private void onEdit() {
        Item sel = getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        ItemFormDialog.Result r = ItemFormDialog.showEdit(this, sel, controller.getAllCategories());
        if (r == null) return;

        String err = controller.updateItem(sel.getId(), r.name, r.priceStr, r.qtyStr, r.minStr, r.unit, r.categoryId);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Edit Item", JOptionPane.WARNING_MESSAGE);

        refresh();
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

        refresh();
        applyFilters();
    }

    private void onAddCategory() {
        String name = JOptionPane.showInputDialog(this, "Category name:");
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) return;

        String err = controller.addCategory(name);
        if (err != null) JOptionPane.showMessageDialog(this, err, "Add Category", JOptionPane.WARNING_MESSAGE);

        refresh();
        applyFilters();
    }

    private void styleTable() {
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        table.setRowHeight(30);

        // ID, Name, Price, Qty, Unit, Min, Category
        double[] relative = {0.07, 0.22, 0.12, 0.12, 0.10, 0.12, 0.25};
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth((int) (1000 * relative[i]));
        }
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(90, 90, 90));
        l.setFont(AppConstants.DEFAULT_FONT.deriveFont(14f));
        return l;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(AppConstants.DEFAULT_FONT.deriveFont(14f));

        return b;
    }

    private JButton btnPrimary(String text) {
        JButton b = btn(text);
        b.setForeground(Color.WHITE);
        b.setBackground(AppConstants.PRIMARY_COLOR);
        return b;
    }

    private JButton btnDanger(String text) {
        JButton b = btn(text);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(255, 0, 100));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private void styleField(JTextField f) {
        f.setForeground(new Color(30, 30, 30));
        f.setCaretColor(new Color(30, 30, 30));
        f.setBackground(Color.WHITE);
        f.setFont(AppConstants.DEFAULT_FONT.deriveFont(14f));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 245)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(Color.WHITE);
        box.setForeground(new Color(30, 30, 30));
        box.setFont(AppConstants.DEFAULT_FONT.deriveFont(14f));
    }
}
