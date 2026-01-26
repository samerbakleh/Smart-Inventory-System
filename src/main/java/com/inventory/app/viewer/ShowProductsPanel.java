package com.inventory.app.viewer;

import com.inventory.app.controller.ProductController;
import com.inventory.app.model.Item;
import com.inventory.app.model.Product;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

public class ShowProductsPanel extends JPanel {

    private final ProductController productController;

    private JTable table;
    private ProductTableModel tableModel;
    private List<Product> products;

    public ShowProductsPanel(ProductController productController) {
        this.productController = productController;
        setLayout(new BorderLayout());
        setName("Products Viewer Panel");
        loadProductsAndBuildTable();
    }


    public void loadProductsAndBuildTable() {
        this.products = productController.getProducts();
        System.out.println("ShowProducts :" + products);

        removeAll();

        if (products == null) {
            products = java.util.Collections.emptyList();
        }

        tableModel = new ProductTableModel(products);
        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(150); // name
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // subtype
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // id
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // required items (button)
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // productLineId

        int requiredItemsColumnIndex = 3;
        table.getColumnModel().getColumn(requiredItemsColumnIndex)
                .setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(requiredItemsColumnIndex)
                .setCellEditor(new ButtonEditor(new JCheckBox(), this::showRequiredItemsDialog));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void showRequiredItemsDialog(int row) {
        Product product = tableModel.getProductAt(row);
        Map<Item, Double> requiredItems = product.getProductType().getRequiredItems();

        String[] columnNames = {"Item", "Quantity"};
        Object[][] data;

        if (requiredItems == null || requiredItems.isEmpty()) {
            data = new Object[][] {{"لا توجد مواد مطلوبة", ""}};
        } else {
            data = new Object[requiredItems.size()][2];
            int i = 0;
            for (Map.Entry<Item, Double> entry : requiredItems.entrySet()) {
                Item item = entry.getKey();
                Double qty = entry.getValue();
                data[i][0] = item != null ? item.getName() : "null";
                data[i][1] = qty;
                i++;
            }
        }

        JTable itemsTable = new JTable(data, columnNames);
        JScrollPane sp = new JScrollPane(itemsTable);
        sp.setPreferredSize(new Dimension(350, 200));

        JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                sp,
                "Required items for: " + product.getProductType().getName(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private static class ProductTableModel extends AbstractTableModel {
        private final String[] columnNames = {
                "name", "subtype", "id", "required items", "productline"
        };

        private final List<Product> products;

        public ProductTableModel(List<Product> products) {
            this.products = products;
        }

        public Product getProductAt(int rowIndex) {
            return products.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return products.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product p = products.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.getProductType().getName();
                case 1 -> p.getProductType().getSubtype();
                case 2 -> p.getId();
                case 3 -> "Show";
                case 4 -> p.getProductLineId();
                default -> null;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 3;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2 || columnIndex == 4) {
                return Integer.class;
            }
            return String.class;
        }
    }


    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            setText(value == null ? "Show" : value.toString());

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }


    private static class ButtonEditor extends AbstractCellEditor
            implements TableCellEditor, ActionListener {

        private final JButton button;
        private final JCheckBox checkBox;
        private final RequiredItemsCallback callback;
        private int currentRow = -1;

        public interface RequiredItemsCallback {
            void showRequiredItems(int row);
        }

        public ButtonEditor(JCheckBox checkBox, RequiredItemsCallback callback) {
            this.checkBox = checkBox;
            this.callback = callback;
            this.button = new JButton();
            button.setOpaque(true);
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {

            currentRow = row;
            button.setText(value == null ? "Show" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
            if (callback != null && currentRow >= 0) {
                callback.showRequiredItems(currentRow);
            }
        }
    }
}
