package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;

import javax.swing.*;
import java.awt.*;

public class ItemViewerPanel extends JPanel {

    private final ItemController controller;

    public ItemViewerPanel(ItemController controller) {
        this.controller = controller;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JButton("Add"));
        top.add(new JButton("Edit"));
        top.add(new JButton("Delete"));
        top.add(new JButton("Add Category"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(new JTable()), BorderLayout.CENTER);
    }
}
