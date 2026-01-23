package com.inventory.app.viewer;

import com.inventory.app.controller.ItemController;
import com.inventory.app.viewer.AppNavigator;

import javax.swing.*;
import java.awt.*;

public class InventoryManagementPanel extends JPanel {

    private final CardLayout card = new CardLayout();
    private final JPanel body = new JPanel(card);

    public InventoryManagementPanel(AppNavigator nav, ItemController itemController) {
        setLayout(new BorderLayout(10, 10));

        // Top buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton back = new JButton("← Back");
        JButton viewItems = new JButton("View Items");
        JButton viewProducts = new JButton("View Products");

        back.addActionListener(e -> nav.show("supervisor"));
        viewItems.addActionListener(e -> {
            card.show(body, "items");
        });
        viewProducts.addActionListener(e -> {
            card.show(body, "products");
        });

        top.add(back);
        top.add(viewItems);
        top.add(viewProducts);

        add(top, BorderLayout.NORTH);

        // Cards
        body.add(new ItemsPanel(itemController), "items");
        body.add(new JLabel("Products panel will be added later..."), "products");

        add(body, BorderLayout.CENTER);

        // default
        card.show(body, "items");
    }
}
