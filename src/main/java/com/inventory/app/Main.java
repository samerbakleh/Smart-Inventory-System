package com.inventory.app;

import com.inventory.app.controller.AuthenticationController;
import com.inventory.app.controller.ItemController;
import com.inventory.app.controller.MainController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.repository.*;
import com.inventory.app.service.InventoryService;
import com.inventory.app.service.ProductLineService;
import com.inventory.app.service.ProductTypeService;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        ProductTypeRepository productTypeRepository = new ProductTypeRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        ItemRepository itemRepository = new ItemRepository(categoryRepository);
        ProductLineRepository productLineRepository = new ProductLineRepository();
        TaskRepository taskRepository = new TaskRepository();

        InventoryService inventoryService = new InventoryService(itemRepository, categoryRepository);
        ProductLineService productLineService = new ProductLineService(productLineRepository, taskRepository,
                                                                       productTypeRepository);
        ProductTypeService productTypeService = new ProductTypeService(productTypeRepository, itemRepository);

        MainController mainController = new MainController(productTypeService, productLineService);
        ItemController itemController = new ItemController(inventoryService);
        ProductLineController productLineController = new ProductLineController(productLineService);
        AuthenticationController authenticationController = new AuthenticationController();


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if (info.getName().equals("Nimbus")) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MainFrame mainFrame = new MainFrame(mainController, authenticationController, itemController, productLineController);
            }
        });

    }

}

