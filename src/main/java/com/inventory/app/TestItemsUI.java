package com.inventory.app;

import com.inventory.app.controller.ItemController;
import com.inventory.app.repository.CategoryRepository;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.service.InventoryService;
import com.inventory.app.viewer.ItemViewerPanel;

import javax.swing.*;

public class TestItemsUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            CategoryRepository catRepo = new CategoryRepository();
            ItemRepository itemRepo = new ItemRepository(catRepo);

            InventoryService service = new InventoryService(itemRepo, catRepo);
            ItemController controller = new ItemController(service);

            JFrame f = new JFrame("Items - Test");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 650);
            f.setLocationRelativeTo(null);

            f.setContentPane(new ItemViewerPanel(controller));

            f.setVisible(true);
        });
    }
}
