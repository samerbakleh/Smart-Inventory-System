package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class ManagerPanel extends JPanel{

    private ProductLineController productLineController;
    private AddProductLineDialog addProductLineDialog;
    private ProductLineInfoDialog productLineInfoDialog;

    public ManagerPanel(ProductLineController productLineController) {
        super(new GridBagLayout());
        this.productLineController = productLineController;
        this.addProductLineDialog = new AddProductLineDialog(productLineController);
        this.productLineInfoDialog = new ProductLineInfoDialog(productLineController);
        setName("Manager panel");
        setBackground(Color.white);
        addComponents();
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        GradientPanel topPanel = new GradientPanel(AppConstants.PRIMARY_COLOR, AppConstants.SECONDARY_COLOR);
        topPanel.setLayout(new GridBagLayout());
        ShadowedLabel managerLabel = new ShadowedLabel("Manager Dashboard");
        managerLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(35f));
        managerLabel.setForeground(Color.white);
        managerLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        topPanel.add(managerLabel, gbc);

        JButton addProductLineButton = new JButton("+ new production line");
        addProductLineButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(17f));
        addProductLineButton.setForeground(Color.white);
        addProductLineButton.setBackground(AppConstants.PRIMARY_COLOR);
        addProductLineButton.addActionListener((ActionEvent event) -> addProductLineDialog.resetDisplay());

        String[] columnName = {"ID", "Name", "Status", "Tasks", "Progress %"};
        DefaultTableModel model = new DefaultTableModel(loadProductLinesToTable(), columnName) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int colIndex) {
                final int ID_COL_INDEX = 0;
                final int STATUS_COL_INDEX = 2;
                final int PROGRESS_COL_INDEX = 4;
                if(colIndex == ID_COL_INDEX) {
                    return ProductLine.class;
                } else if(colIndex == STATUS_COL_INDEX) {
                    return ProductLineStatus.class;
                } else if(colIndex == PROGRESS_COL_INDEX) {
                    return Float.class;
                }
                return super.getColumnClass(colIndex);
            }
        };
        ProductLineTable table = new ProductLineTable(model);
        JScrollPane tableContainer = new JScrollPane(table);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(23);
        double[] relativeColumnWidth = {0.05, 0.25, 0.15, 0.05, 0.4};
        for(int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (1000 * relativeColumnWidth[i]));
        }

        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(topPanel, gbc);
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 10, 30);
        add(addProductLineButton, gbc);
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 40, 20, 40);
        add(tableContainer, gbc);
    }

    private Object[][] loadProductLinesToTable() {
        Map<ProductLine, Float> productLines = productLineController.fetchProductLines();
        Object[][] result = new Object[productLines.size()][5];
        int i = 0;
        for(Map.Entry<ProductLine, Float> entry : productLines.entrySet()) {
            ProductLine pl = entry.getKey();
            int numOfTasks = 0;
            for(List<Task> tasks : pl.getTasks().values()) {
                numOfTasks += tasks.size();
            }
            result[i] = new Object[]{pl, pl.getName(), pl.getStatus(), numOfTasks, entry.getValue()};
            i++;
        }
        return result;
    }

    private class ProductLineTable extends JTable {
        public ProductLineTable(TableModel model) {
            super(model);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setColumnRenderer();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    ProductLine pl = (ProductLine) getModel().getValueAt(getSelectedRow(), 0);
                    productLineInfoDialog.display(pl);
                }
            });
        }

        private void setColumnRenderer() {
            DefaultTableCellRenderer productLineRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    label.setHorizontalAlignment(CENTER);
                    label.setText(((ProductLine) value).getID() + "");
                    return label;
                }
            };
            DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    label.setHorizontalAlignment(CENTER);
                    return label;
                }
            };
            DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    label.setHorizontalAlignment(CENTER);
                    label.setText(label.getText().toLowerCase());
                    if(isSelected) {
                        label.setBackground(table.getSelectionBackground());
                    } else {
                        Color c = switch((ProductLineStatus) value) {
                            case ProductLineStatus.ACTIVE -> new Color(0, 255, 140, 100);
                            case ProductLineStatus.MAINTENANCE -> new Color(255, 255, 140, 100);
                            case ProductLineStatus.STOPPED -> new Color(255, 0, 140, 100);
                        };
                        if (row % 2 == 1) {
                            c = new Color(c.getRed() * 9 / 10, c.getGreen() * 9 / 10, c.getBlue() * 9 / 10, c.getAlpha());
                        }
                        label.setBackground(c);
                    }
                    return label;
                }
            };
            DefaultTableCellRenderer progressRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setValue(((Float) value).intValue());
                    progressBar.setStringPainted(true);
                    progressBar.setOpaque(true);
                    progressBar.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    return progressBar;
                }
            };
            setDefaultRenderer(ProductLine.class, productLineRenderer);
            setDefaultRenderer(ProductLineStatus.class, statusRenderer);
            setDefaultRenderer(Float.class, progressRenderer);
            setDefaultRenderer(Object.class, textRenderer);
        }

    }

}
