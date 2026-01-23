package com.inventory.app;

import com.inventory.app.controller.ItemController;
import com.inventory.app.repository.CategoryRepository;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.repository.UserRepository;
import com.inventory.app.service.InventoryService;
import com.inventory.app.viewer.ItemsPanel;
import com.inventory.app.viewer.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1) Load users (so login later works)
            UserRepository.loadUsersFromFile();

            // 2) Build repositories
            CategoryRepository categoryRepo = new CategoryRepository();
            ItemRepository itemRepo = new ItemRepository(categoryRepo);

            // 3) Build service + controller
            InventoryService inventoryService = new InventoryService(itemRepo, categoryRepo);
            ItemController itemController = new ItemController(inventoryService);

            // 4) Frame
            MainFrame frame = new MainFrame();

            // 5) Views
            ItemsPanel itemsPanel = new ItemsPanel(itemController);

            frame.registerView("items", itemsPanel);

            // Start view
            frame.setSubTitle("Inventory • Items");
            frame.show("items");

            frame.setVisible(true);
        });
    }
}
