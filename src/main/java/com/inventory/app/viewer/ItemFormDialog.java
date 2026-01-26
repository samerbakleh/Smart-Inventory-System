package com.inventory.app.viewer;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.enums.Unit;
import com.inventory.app.util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ItemFormDialog extends JDialog {

    public static class Result {
        public final String name, priceStr, qtyStr, minStr;
        public final Unit unit;
        public final int categoryId;

        public Result(String name, String priceStr, String qtyStr, String minStr, Unit unit, int categoryId) {
            this.name = name;
            this.priceStr = priceStr;
            this.qtyStr = qtyStr;
            this.minStr = minStr;
            this.unit = unit;
            this.categoryId = categoryId;
        }
    }

    private Result result;

    private final JTextField name = new JTextField(18);
    private final JTextField price = new JTextField(10);
    private final JTextField qty = new JTextField(10);
    private final JTextField min = new JTextField(10);

    private final JComboBox<Unit> unitBox = new JComboBox<>(Unit.values());
    private final JComboBox<Category> catBox = new JComboBox<>();

    public static Result showAdd(Component parent, List<Category> cats) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        ItemFormDialog d = new ItemFormDialog(w, "Add Item", null, cats);
        d.setVisible(true);
        return d.result;
    }

    public static Result showEdit(Component parent, Item item, List<Category> cats) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        ItemFormDialog d = new ItemFormDialog(w, "Edit Item", item, cats);
        d.setVisible(true);
        return d.result;
    }

    private ItemFormDialog(Window owner, String title, Item existing, List<Category> cats) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (Category c : cats) catBox.addItem(c);

        if (existing != null) {
            name.setText(existing.getName());
            price.setText(String.valueOf(existing.getPrice()));
            qty.setText(String.valueOf(existing.getQuantity()));
            min.setText(String.valueOf(existing.getMinLimit()));
            unitBox.setSelectedItem(existing.getUnit());

            if (existing.getCategory() != null) {
                int targetId = existing.getCategory().getId();
                for (int i = 0; i < catBox.getItemCount(); i++) {
                    if (catBox.getItemAt(i).getId() == targetId) {
                        catBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            unitBox.setSelectedItem(Unit.PCS);
            if (catBox.getItemCount() > 0) catBox.setSelectedIndex(0);
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.white);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addRow(form, g, r++, "Name:", name);
        addRow(form, g, r++, "Price:", price);
        addRow(form, g, r++, "Quantity:", qty);
        addRow(form, g, r++, "Min Limit:", min);
        addRow(form, g, r++, "Unit:", unitBox);
        addRow(form, g, r++, "Category:", catBox);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.setBackground(AppConstants.SECONDARY_COLOR);
        save.setForeground(Color.white);
        save.addActionListener(e -> onSave());
        cancel.setBackground(AppConstants.SECONDARY_COLOR);
        cancel.setForeground(Color.white);
        cancel.addActionListener(e -> { result = null; dispose(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btns.add(cancel);
        btns.add(save);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel p, GridBagConstraints g, int r, String label, Component field) {
        g.gridx = 0; g.gridy = r; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void onSave() {
        try {
            String n = name.getText().trim();
            if (n.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");

            String pr = price.getText().trim();
            String q  = qty.getText().trim();
            String m  = min.getText().trim();

            Double.parseDouble(pr);
            Double.parseDouble(q);
            Double.parseDouble(m);

            Category c = (Category) catBox.getSelectedItem();
            if (c == null) throw new IllegalArgumentException("Category is required");

            Unit u = (Unit) unitBox.getSelectedItem();
            if (u == null) u = Unit.PCS;

            result = new Result(n, pr, q, m, u, c.getId());
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
