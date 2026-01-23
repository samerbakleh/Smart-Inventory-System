package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddCategoryDialog extends JDialog {

    private final ItemController controller;
    private JTextField name;
    private JLabel error;
    private boolean saved = false;

    public AddCategoryDialog(Window owner, ItemController controller) {
        super(owner, "Add Category", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        buildUI();

        pack();
        setMinimumSize(new Dimension(460, 240));
        setLocationRelativeTo(owner);

        ThemeManager.get().applyToContainer(getContentPane());
    }

    public boolean isSaved() { return saved; }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Create a new category");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JLabel sub = new JLabel("Category name must be unique");
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(sub);

        name = new JTextField();

        JPanel center = new JPanel(new BorderLayout(10, 6));
        center.setOpaque(false);
        JLabel l = new JLabel("Name");
        l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
        center.add(l, BorderLayout.NORTH);
        center.add(name, BorderLayout.CENTER);

        error = new JLabel(" ");
        error.setVisible(false);
        error.setFont(error.getFont().deriveFont(Font.PLAIN, 12f));

        JButton cancel = new ItemsPanel.HoverActionButton("Cancel", ItemsPanel.HoverActionButton.Kind.NEUTRAL);
        JButton save = new ItemsPanel.HoverActionButton("Save", ItemsPanel.HoverActionButton.Kind.PRIMARY);

        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> onSave());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(cancel);
        footer.add(save);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(error);
        south.add(Box.createVerticalStrut(10));
        south.add(footer);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void setError(String msg) {
        error.setVisible(true);
        error.setText("⛔ " + msg);
        error.setForeground(ThemeManager.get().theme().danger);
        UIEffects.shake(this);
    }

    private void onSave() {
        String err = controller.addCategory(name.getText());
        if (err != null) {
            setError(err);
            return;
        }
        saved = true;
        dispose();
    }
}
