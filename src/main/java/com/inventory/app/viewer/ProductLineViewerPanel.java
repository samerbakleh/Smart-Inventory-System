package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.ProductLineTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductLineViewerPanel extends JPanel {

    private ProductLineController productLineController;
    private ProductLineTable table;

    public ProductLineViewerPanel(ProductLineController productLineController) {
        super(new GridBagLayout());
        this.productLineController = productLineController;
        setName("Product Lines Viewer Panel");
        setBackground(Color.white);
        addComponents();
    }

    public void refresh() {
        refreshTable();
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int n = model.getColumnCount();
        String[] names = new String[n];
        for(int i = 0; i < n; i++) {
            names[i] = model.getColumnName(i);
        }
        model.setDataVector(table.generateTableContent(), names);
        table.changeColumnsWidth();
    }


    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        table = new ProductLineTable(productLineController.fetchProductLineMainData());
        JScrollPane tableContainer = new JScrollPane(table);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(30);
        table.changeColumnsWidth();

        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 40, 20, 40);
        add(tableContainer, gbc);
    }

}
