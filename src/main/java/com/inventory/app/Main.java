package com.inventory.app;

import com.inventory.app.controller.AuthenticationController;
import com.inventory.app.controller.MainController;
import com.inventory.app.controller.ProductLineController;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.repository.ProductLineRepository;
import com.inventory.app.repository.ProductTypeRepository;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.service.ProductLineService;
import com.inventory.app.service.ProductTypeService;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.AddProductLineDialog;
import com.inventory.app.viewer.LogInPanel;
import com.inventory.app.viewer.MainFrame;
import com.inventory.app.viewer.ManagerPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        ProductTypeRepository productTypeRepository = new ProductTypeRepository();
        ItemRepository itemRepository = new ItemRepository();
        ProductLineRepository productLineRepository = new ProductLineRepository();
        TaskRepository taskRepository = new TaskRepository();

        ProductLineService productLineService = new ProductLineService(productLineRepository, taskRepository,
                                                                       productTypeRepository);
        ProductTypeService productTypeService = new ProductTypeService(productTypeRepository, itemRepository);

        MainController mainController = new MainController(productTypeService, productLineService);
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

                AddProductLineDialog addProductLineDialog = new AddProductLineDialog(productLineController);
                LogInPanel logInPanel = new LogInPanel(authenticationController);
                ManagerPanel managerPanel = new ManagerPanel(addProductLineDialog, productLineController);

                MainFrame mainFrame = new MainFrame(logInPanel, managerPanel, mainController);
            }
        });

    }

}

