package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.ProductLineTable;

import javax.swing.*;
import javax.swing.table.TableColumn;
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

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        table = new ProductLineTable(productLineController.fetchProductLineMainData());
        JScrollPane tableContainer = new JScrollPane(table);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(30);
        double[] relativeColumnWidth = {0.05, 0.2, 0.15, 0.05, 0.05, 0.4};
        for(int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (1000 * relativeColumnWidth[i]));
        }

        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 40, 20, 40);
        add(tableContainer, gbc);
    }

}
