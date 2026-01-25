package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.ProductTypeController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManagerPanel extends JPanel{

    private ProductLineController productLineController;
    private ProductTypeController productTypeController;

    private AddProductLineDialog addProductLineDialog;
    private ProductLineInfoDialog productLineInfoDialog;
    private ProductLineManagerTable table;

    public ManagerPanel(ProductLineController productLineController, ProductTypeController productTypeController) {
        super(new GridBagLayout());
        this.productLineController = productLineController;
        this.productTypeController = productTypeController;
        this.addProductLineDialog = new AddProductLineDialog(productLineController, productTypeController, this);
        this.productLineInfoDialog = new ProductLineInfoDialog(productLineController, this);
        setName("Manager panel");
        setBackground(Color.white);
        addComponents();
    }

    public void refresh() {
        refreshTable();
        addProductLineDialog.updateProductTypes();
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int n = model.getColumnCount();
        String[] names = new String[n];
        for(int i = 0; i < n; i++) {
            names[i] = model.getColumnName(i);
        }
        model.setDataVector(loadProductLinesToTable(), names);
        table.changeColumnsWidth();
    }

    public void refreshTableContent() {
        int n = table.getRowCount();
        List<Float> percentages = productLineController.getProductLineProductionPercentage();
        for(int i = 0; i < n; i++) {
            refreshRowContent(i, percentages.get(i));
        }
    }

    public void refreshRowContent(int rowIndex) {
        TableModel model = table.getModel();
        List<Float> percentages = productLineController.getProductLineProductionPercentage();
        ProductLine pl = (ProductLine) model.getValueAt(rowIndex, 0);
        model.setValueAt(pl.getName(), rowIndex, 1);
        model.setValueAt(pl.getStatus(), rowIndex, 2);
        model.setValueAt(pl.getNumOfTasks(), rowIndex, 3);
        model.setValueAt(percentages.get(rowIndex), rowIndex, 4);
    }

    private void refreshRowContent(int rowIndex, float percentage) {
        TableModel model = table.getModel();
        ProductLine pl = (ProductLine) model.getValueAt(rowIndex, 0);
        model.setValueAt(pl.getName(), rowIndex, 1);
        model.setValueAt(pl.getStatus(), rowIndex, 2);
        model.setValueAt(pl.getNumOfTasks(), rowIndex, 3);
        model.setValueAt(percentage, rowIndex, 4);
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

        table = new ProductLineManagerTable();
        JScrollPane tableContainer = new JScrollPane(table);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(23);
        table.changeColumnsWidth();

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
        List<ProductLine> productLines = productLineController.fetchProductLineMainData();
        List<Float> percentages = productLineController.getProductLineProductionPercentage();
        Object[][] result = new Object[productLines.size()][5];
        for(int i = 0; i < productLines.size(); i++) {
            ProductLine pl = productLines.get(i);
            result[i] = new Object[]{pl, pl.getName(), pl.getStatus(), pl.getNumOfTasks(), percentages.get(i)};
        }
        return result;
    }

    private class ProductLineManagerTable extends JTable {
        private DefaultTableModel model;
        private ProductLine selectedProductLine = null;
        public ProductLineManagerTable() {
            initModel();
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JPopupMenu optionMenu = new JPopupMenu();
            JMenuItem option = new JMenuItem("show details");
            option.addActionListener((ActionEvent event) -> {
                productLineInfoDialog.display(selectedProductLine, getSelectedRow());
            });
            optionMenu.add(option);
            setColumnRenderer();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent event) {
                    if(getSelectedColumn() != 1) {
                        int selectedRow = getSelectedRow();
                        if(selectedRow != -1) {
                            selectedProductLine = (ProductLine) getModel().getValueAt(selectedRow, 0);
                            optionMenu.show(event.getComponent(), event.getX(), event.getY());
                        }
                    }
                }
            });
        }

        public void changeColumnsWidth() {
            double[] relativeColumnWidth = {0.05, 0.25, 0.15, 0.05, 0.4};
            for(int i = 0; i < table.getColumnCount(); i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth((int) (1000 * relativeColumnWidth[i]));
            }
        }

        private void initModel() {
            String[] columnName = {"ID", "Name", "Status", "Tasks", "Progress %"};
            model = new DefaultTableModel(loadProductLinesToTable(), columnName) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    final int NAME_COL_INDEX = 1;
                    return col == NAME_COL_INDEX;
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
            model.addTableModelListener((TableModelEvent event) -> {
                if(event.getType() != TableModelEvent.UPDATE) {
                    return;
                }
                int row = event.getFirstRow();
                if(row == -1) {
                    return;
                }
                ProductLine pl = (ProductLine) model.getValueAt(row, 0);
                boolean success = productLineController.changeProductLineName(pl, (String) model.getValueAt(row, 1));
                if(!success) {
                    JOptionPane.showMessageDialog(null, "Production line name can only contain letters, numbers, and -",
                            "Invalid Production Line Name", JOptionPane.WARNING_MESSAGE);
                    model.setValueAt(pl.getName(), row, 1);
                }
            });
            setModel(model);
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
