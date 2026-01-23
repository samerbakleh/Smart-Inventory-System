package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ItemsPanel extends JPanel {

    private final ItemController controller;

    private final ItemTableModel model = new ItemTableModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<ItemTableModel> sorter = new TableRowSorter<>(model);

    private JTextField search;
    private JComboBox<CategoryChoice> categoryFilter;

    private JToggleButton allBtn;
    private JToggleButton lowBtn;
    private JToggleButton outBtn;

    private JLabel statsLabel;

    // Fade animation wrapper
    private final FadeLayerUI fadeUI = new FadeLayerUI();
    private final JLayer<JComponent> fadeLayer;

    public ItemsPanel(ItemController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        // table config
        table.setRowHeight(34);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(sorter);

        JScrollPane sp = new JScrollPane(table);

        fadeLayer = new JLayer<>(sp, fadeUI);

        add(buildTopBar(), BorderLayout.NORTH);
        add(fadeLayer, BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        ThemeManager.get().applyToContainer(this);

        refresh(true);
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        // left: title + stats
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Items");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        statsLabel = new JLabel(" ");
        statsLabel.setFont(statsLabel.getFont().deriveFont(Font.PLAIN, 12f));

        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(statsLabel);

        // right: actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton add    = new AnimatedColorButton("➕ Add", AnimatedColorButton.Kind.PRIMARY);
        JButton edit   = new AnimatedColorButton("✎ Edit", AnimatedColorButton.Kind.NEUTRAL);
        JButton del    = new AnimatedColorButton("🗑 Delete", AnimatedColorButton.Kind.DANGER);
        JButton addCat = new AnimatedColorButton("🏷 Add Category", AnimatedColorButton.Kind.SUCCESS);


        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());
        addCat.addActionListener(e -> onAddCategory());

        actions.add(add);
        actions.add(edit);
        actions.add(del);
        actions.add(addCat);

        top.add(left, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);

        // filters row under it
        JPanel filters = buildFiltersBar();
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.add(top);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(filters);

        return wrap;
    }

    private JPanel buildFiltersBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        // search
        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setOpaque(false);

        JLabel icon = new JLabel("🔎");
        search = new JTextField();
        search.setToolTipText("Search by name...");

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        searchWrap.add(icon, BorderLayout.WEST);
        searchWrap.add(search, BorderLayout.CENTER);

        // category filter
        categoryFilter = new JComboBox<>();
        categoryFilter.setPreferredSize(new Dimension(240, 36));
        categoryFilter.addActionListener(e -> applyFilters());

        // stock segmented toggle
        allBtn = new JToggleButton("All");
        lowBtn = new JToggleButton("Low Stock");
        outBtn = new JToggleButton("Out of Stock");

        ButtonGroup g = new ButtonGroup();
        g.add(allBtn); g.add(lowBtn); g.add(outBtn);
        allBtn.setSelected(true);

        allBtn.addActionListener(e -> applyFilters());
        lowBtn.addActionListener(e -> applyFilters());
        outBtn.addActionListener(e -> applyFilters());

        JPanel stockSeg = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stockSeg.setOpaque(false);
        stockSeg.add(allBtn);
        stockSeg.add(lowBtn);
        stockSeg.add(outBtn);

        // right side layout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(new JLabel("Category:"));
        right.add(categoryFilter);

        bar.add(searchWrap, BorderLayout.CENTER);
        bar.add(stockSeg, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private JComponent buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);

        JLabel hint = new JLabel("Tip: double-click row to edit");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));

        JButton reload = new HoverActionButton("↻ Refresh", HoverActionButton.Kind.NEUTRAL);
        reload.addActionListener(e -> refresh(true));

        bottom.add(hint, BorderLayout.WEST);
        bottom.add(reload, BorderLayout.EAST);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    onEdit();
                }
            }
        });

        return bottom;
    }

    private void refresh(boolean animate) {
        Runnable doLoad = () -> {
            List<Category> cats = controller.getAllCategories();
            List<Item> items = controller.getAllItems();

            model.setItems(items);
            buildCategoryModel(cats);
            applyFilters();
            updateStats(items);
        };

        if (!animate) {
            doLoad.run();
            return;
        }

        // fade-out -> load -> fade-in
        animateFade(1f, 0.25f, 140, () -> {
            doLoad.run();
            animateFade(0.25f, 1f, 180, null);
        });
    }

    private void animateFade(float from, float to, int durMs, Runnable done) {
        long t0 = System.currentTimeMillis();
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            float tt = Math.min(1f, (System.currentTimeMillis() - t0) / (float) durMs);
            float k = tt * tt * (3f - 2f * tt);
            float a = from + (to - from) * k;
            fadeUI.setAlpha(a);
            fadeLayer.repaint();
            if (tt >= 1f) {
                t.stop();
                if (done != null) done.run();
            }
        });
        t.start();
    }

    private void buildCategoryModel(List<Category> cats) {
        DefaultComboBoxModel<CategoryChoice> m = new DefaultComboBoxModel<>();
        m.addElement(CategoryChoice.all());
        if (cats != null) {
            for (Category c : cats) m.addElement(CategoryChoice.of(c));
        }
        categoryFilter.setModel(m);
        categoryFilter.setSelectedIndex(0);
    }

    private void applyFilters() {
        final String q = (search.getText() == null) ? "" : search.getText().trim().toLowerCase();
        final CategoryChoice choice = (CategoryChoice) categoryFilter.getSelectedItem();
        final int catId = (choice == null || choice.cat == null) ? -1 : choice.cat.getId();

        final StockMode stock = outBtn.isSelected() ? StockMode.OUT
                : (lowBtn.isSelected() ? StockMode.LOW : StockMode.ALL);

        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends ItemTableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();
                Item it = model.getItemAt(modelRow);
                if (it == null) return false;

                // search by name
                if (!q.isEmpty()) {
                    String name = it.getName() == null ? "" : it.getName().toLowerCase();
                    if (!name.contains(q)) return false;
                }

                // category
                if (catId != -1) {
                    if (it.getCategory() == null || it.getCategory().getId() != catId) return false;
                }

                // stock mode
                if (stock == StockMode.OUT) {
                    if (it.getQuantity() != 0) return false;
                } else if (stock == StockMode.LOW) {
                    if (!(it.getQuantity() <= it.getMinLimit())) return false;
                }

                return true;
            }
        });
    }

    private void updateStats(List<Item> items) {
        int total = items == null ? 0 : items.size();
        int low = 0, out = 0;
        if (items != null) {
            for (Item it : items) {
                if (it.getQuantity() == 0) out++;
                if (it.getQuantity() <= it.getMinLimit()) low++;
            }
        }
        statsLabel.setText("Total: " + total + "  •  Low: " + low + "  •  Out: " + out);
        statsLabel.setForeground(ThemeManager.get().theme().textDim);
    }

    private Item getSelectedItem() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return model.getItemAt(modelRow);
    }

    private void onAdd() {
        Window w = SwingUtilities.getWindowAncestor(this);
        ItemFormDialog d = new ItemFormDialog(w, controller, ItemFormDialog.Mode.ADD, null, controller.getAllCategories());
        d.setVisible(true);

        if (d.isSaved()) {
            UIEffects.toast(this, "Item added successfully", UIEffects.ToastType.SUCCESS);
            refresh(true);
        }
    }

    private void onEdit() {
        Item selected = getSelectedItem();
        if (selected == null) {
            UIEffects.toast(this, "Select a row to edit", UIEffects.ToastType.WARNING);
            return;
        }

        Window w = SwingUtilities.getWindowAncestor(this);
        ItemFormDialog d = new ItemFormDialog(w, controller, ItemFormDialog.Mode.EDIT, selected, controller.getAllCategories());
        d.setVisible(true);

        if (d.isSaved()) {
            UIEffects.toast(this, "Item updated", UIEffects.ToastType.SUCCESS);
            refresh(true);
        }
    }

    private void onDelete() {
        Item selected = getSelectedItem();
        if (selected == null) {
            UIEffects.toast(this, "Select a row to delete", UIEffects.ToastType.WARNING);
            return;
        }

        int res = JOptionPane.showConfirmDialog(
                this,
                "Delete item #" + selected.getId() + " (" + selected.getName() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (res != JOptionPane.YES_OPTION) return;

        String err = controller.deleteItem(Integer.toString(selected.getId()));
        if (err != null) {
            UIEffects.toast(this, err, UIEffects.ToastType.ERROR);
            return;
        }

        UIEffects.toast(this, "Deleted", UIEffects.ToastType.SUCCESS);
        refresh(true);
    }

    private void onAddCategory() {
        Window w = SwingUtilities.getWindowAncestor(this);
        AddCategoryDialog d = new AddCategoryDialog(w, controller);
        d.setVisible(true);

        if (d.isSaved()) {
            UIEffects.toast(this, "Category added", UIEffects.ToastType.SUCCESS);
            refresh(true); // refresh categories + items
        }
    }

    // --- helpers ---
    private enum StockMode { ALL, LOW, OUT }

    private static final class CategoryChoice {
        final Category cat;
        final String text;

        private CategoryChoice(Category cat, String text) {
            this.cat = cat;
            this.text = text;
        }

        static CategoryChoice all() { return new CategoryChoice(null, "All Categories"); }
        static CategoryChoice of(Category c) { return new CategoryChoice(c, c == null ? "" : c.getName()); }

        @Override public String toString() { return text; }
    }

    // Fade layer UI
    private static final class FadeLayerUI extends javax.swing.plaf.LayerUI<JComponent> {
        private float alpha = 1f;

        void setAlpha(float a) { alpha = Math.max(0f, Math.min(1f, a)); }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            super.paint(g2, c);
            g2.dispose();
        }
    }

    // --- Animated buttons (theme-aware) ---
    public static class HoverActionButton extends JButton {
        public enum Kind { PRIMARY, DANGER, NEUTRAL }

        private final Kind kind;
        private float hover = 0f;
        private Timer anim;

        public HoverActionButton(String text, Kind kind) {
            super(text);
            this.kind = kind == null ? Kind.NEUTRAL : kind;
            setFocusPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { animateTo(1f); }
                @Override public void mouseExited(MouseEvent e)  { animateTo(0f); }
            });
        }

        private void animateTo(float target) {
            if (anim != null && anim.isRunning()) anim.stop();
            float start = hover;

            long t0 = System.currentTimeMillis();
            int dur = 160;

            anim = new Timer(15, null);
            anim.addActionListener(e -> {
                float tt = Math.min(1f, (System.currentTimeMillis() - t0) / (float) dur);
                float k = tt * tt * (3f - 2f * tt);
                hover = start + (target - start) * k;
                applyThemeColors();
                if (tt >= 1f) anim.stop();
            });
            anim.start();
        }

        private void applyThemeColors() {
            ThemeManager.Theme t = ThemeManager.get().theme();

            Color base = t.surface2;
            Color accent = switch (kind) {
                case PRIMARY -> t.accent;
                case DANGER -> t.danger;
                default -> t.border;
            };

            // subtle hover tint
            Color bg = UIEffects.blend(base, accent, 0.18f * hover);
            setBackground(bg);
            setForeground(t.text);
        }
    }
}
