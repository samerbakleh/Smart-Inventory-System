package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ItemFormDialog extends JDialog {

    public enum Mode { ADD, EDIT }

    private final ItemController controller;
    private final Mode mode;
    private final Item editing;

    private JTextField name;
    private JTextField price;
    private JTextField qty;
    private JTextField min;
    private JComboBox<Category> category;
    private JLabel error;

    private boolean saved = false;

    public ItemFormDialog(Window owner, ItemController controller, Mode mode, Item editing, List<Category> categories) {
        super(owner, mode == Mode.ADD ? "Add Item" : "Edit Item", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.mode = mode;
        this.editing = editing;

        buildUI(categories);

        if (mode == Mode.EDIT && editing != null) {
            fillFromItem(editing);
        }

        pack();
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(owner);

        ThemeManager.get().applyToContainer(getContentPane());
    }

    public boolean isSaved() { return saved; }

    private void buildUI(List<Category> cats) {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        // header
        JLabel title = new JLabel(mode == Mode.ADD ? "Create new item" : "Update item");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JLabel sub = new JLabel("Fill fields then press Save");
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(sub);

        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        name = field(form, gbc, "Name");
        price = field(form, gbc, "Price");
        qty = field(form, gbc, "Quantity");
        min = field(form, gbc, "Min Limit");

        category = new JComboBox<>();
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        if (cats != null) {
            for (Category c : cats) model.addElement(c);
        }
        category.setModel(model);

        addRow(form, gbc, "Category", category);

        error = new JLabel(" ");
        error.setFont(error.getFont().deriveFont(Font.PLAIN, 12f));
        error.setVisible(false);

        // footer
        JButton cancel = new ItemsPanel.HoverActionButton("Cancel", ItemsPanel.HoverActionButton.Kind.NEUTRAL);
        JButton save = new ItemsPanel.HoverActionButton("Save", ItemsPanel.HoverActionButton.Kind.PRIMARY);

        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> onSave());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(cancel);
        footer.add(save);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(error);
        south.add(Box.createVerticalStrut(10));
        south.add(footer);

        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JTextField field(JPanel form, GridBagConstraints gbc, String label) {
        JTextField tf = new JTextField();
        addRow(form, gbc, label, tf);
        return tf;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, String label, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(10, 6));
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
        row.add(l, BorderLayout.NORTH);
        row.add(comp, BorderLayout.CENTER);

        form.add(row, gbc);
        gbc.gridy++;
    }

    private void fillFromItem(Item it) {
        name.setText(it.getName());
        price.setText(Double.toString(it.getPrice()));
        qty.setText(Integer.toString(it.getQuantity()));
        min.setText(Integer.toString(it.getMinLimit()));

        if (it.getCategory() != null) category.setSelectedItem(it.getCategory());
    }

    private void setError(String msg) {
        error.setVisible(true);
        error.setText("⛔ " + msg);
        ThemeManager.Theme t = ThemeManager.get().theme();
        error.setForeground(t.danger);
        UIEffects.shake(this);
    }

    private void onSave() {
        Category c = (Category) category.getSelectedItem();
        if (c == null) {
            setError("Please select a category.");
            return;
        }

        String err;
        if (mode == Mode.ADD) {
            err = controller.addItem(
                    name.getText(),
                    price.getText(),
                    qty.getText(),
                    min.getText(),
                    c.getId()
            );
        } else {
            if (editing == null) {
                setError("No item selected to edit.");
                return;
            }
            err = controller.updateItem(
                    editing.getId(),
                    name.getText(),
                    price.getText(),
                    qty.getText(),
                    min.getText(),
                    c.getId()
            );
        }

        if (err != null) {
            setError(err);
            return;
        }

        saved = true;
        dispose();
    }
}
