package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.ColoredProgressBar;
import com.inventory.app.viewer.components.GradientPanel;
import com.inventory.app.viewer.components.ShadowedLabel;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class ManagerPanel extends JPanel{

    private MainFrame container;
    private AddProductLineDialog addProductLineDialog;
    private ProductLineController productLineController;

    public ManagerPanel(AddProductLineDialog addProductLineDialog, ProductLineController productLineController) {
        super(new GridBagLayout());
        this.addProductLineDialog = addProductLineDialog;
        this.productLineController = productLineController;
        setName("Manager panel");
        setBackground(Color.white);
        addComponents();
    }

    public void setContainer(MainFrame container) {
        this.container = container;
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

        String[] statuses = {"active", "maintenance", "stopped"};
        String[] columnName = {"ID", "Name", "Status", "Tasks", "Progress %"};
        Object[][] data = loadProductLinesToTable();
        DefaultTableModel model = new DefaultTableModel(data, columnName) {
            private final int statusColIndex;
            {
                int i;
                for(i = 0; i < columnName.length; i++) {
                    if(columnName[i].equals("Status")) {
                        break;
                    }
                }
                statusColIndex = i;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == statusColIndex;
            }
            @Override
            public Class<?> getColumnClass(int colIndex) {
                if(colIndex == statusColIndex) {
                    return ProductLineStatus.class;
                }
                return super.getColumnClass(colIndex);
            }
        };
        JTable table = new JTable(model);
        JScrollPane tableContainer = new JScrollPane(table);
        table.getTableHeader().setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setSelectionBackground(AppConstants.PRIMARY_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(AppConstants.DEFAULT_FONT.deriveFont(16f));
        table.setRowHeight(23);
        table.setDefaultEditor(ProductLineStatus.class, new DefaultCellEditor(new JComboBox<>(statuses)));
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component comp;
                Color c = table.getBackground();
                if (value instanceof Double progress) {
                    ColoredProgressBar progressBar = new ColoredProgressBar(0, 100, AppConstants.PRIMARY_COLOR);
                    progressBar.setValue(progress.intValue());
                    progressBar.setStringPainted(true);
                    progressBar.setOpaque(true);
                    comp = progressBar;
                } else {
                    JLabel label = (JLabel)
                            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    if (value instanceof ProductLineStatus status) {
                        label.setText(label.getText().toLowerCase());
                        c = switch (status) {
                            case ProductLineStatus.ACTIVE -> new Color(0, 255, 140, 100);
                            case ProductLineStatus.MAINTENANCE -> new Color(255, 255, 140, 100);
                            case ProductLineStatus.STOPPED -> new Color(255, 0, 140, 100);
                        };
                        if (row % 2 == 1) {
                            c = new Color(c.getRed() * 9/10, c.getGreen() * 9/10, c.getBlue() * 9/10, c.getAlpha());
                        }
                    }
                    comp = label;
                }
                comp.setBackground(isSelected ? table.getSelectionBackground() : c);
                return comp;
            }
        };
        double[] relativeColumnWidth = {0.05, 0.25, 0.15, 0.05, 0.4};
        for(int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (1000 * relativeColumnWidth[i]));
            column.setCellRenderer(cellRenderer);
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
        List<ProductLine> productLineList = productLineController.fetchProductLines();
        Object[][] result = new Object[productLineList.size()][5];
        for(int i = 0; i < productLineList.size(); i++) {
            ProductLine pl = productLineList.get(i);
            int numOfTasks = 0;
            for(List<Task> tasks : pl.getTasks().values()) {
                numOfTasks += tasks.size();
            }
            result[i] = new Object[]{pl.getID(), pl.getName(), pl.getStatus(), numOfTasks, Math.random()*100};
        }
        return result;
    }

}
