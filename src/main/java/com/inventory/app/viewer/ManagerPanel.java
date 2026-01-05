package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.Task;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class ManagerPanel extends JPanel{

    private final MainFrame container;
    private AddProductLineDialog addProductLineDialog;
    private ProductLineController productLineController;

    public ManagerPanel(MainFrame container) {
        super(new GridBagLayout());
        this.container = container;
        this.addProductLineDialog = new AddProductLineDialog(productLineController);
        this.productLineController = new ProductLineController();
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
        addProductLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductLineDialog.resetDisplay();
            }
        });

        String[] columnName = {"ID", "Name", "Status", "Tasks", "Progress %"};
        Object[][] data = loadProductLinesToTable();
        JTable table = new JTable(data, columnName);
        JScrollPane tableContainer = new JScrollPane(table);
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        table.setRowHeight(20);
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
        gbc.insets = new Insets(0, 30, 20, 30);
        add(tableContainer, gbc);
    }

    private Object[][] loadProductLinesToTable() {
        List<ProductLine> productLineList = productLineController.fetchProductLines();
        Object[][] result = new Object[productLineList.size()][5];
        for(int i = 0; i < productLineList.size(); i++) {
            ProductLine pl = productLineList.get(i);
            String status = pl.getStatus().toString().toLowerCase();
            int numOfTasks = 0;
            for(List<Task> tasks : pl.getTasks().values()) {
                numOfTasks += tasks.size();
            }
            result[i] = new Object[]{pl.getId(), pl.getName(), status, numOfTasks, null};
        }
        return result;
    }

}
