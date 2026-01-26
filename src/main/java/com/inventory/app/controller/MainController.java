package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.Task;
import com.inventory.app.repository.UserRepository;
import com.inventory.app.service.*;

import java.io.IOException;

public class MainController {

    private InventoryService inventoryService;
    private ProductTypeService productTypeService;
    private ProductLineService productLineService;
    private TaskService taskService;
    private AuthenticationService authenticationService;
    private ProductService productService;

    public MainController(InventoryService inventoryService, ProductTypeService productTypeService, ProductService productService,
                          ProductLineService productLineService, TaskService taskService,
                          AuthenticationService authenticationService) {
        this.inventoryService = inventoryService;
        this.productTypeService = productTypeService;
        this.productLineService = productLineService;
        this.taskService = taskService;
        this.authenticationService = authenticationService;
        this.productService = productService;
    }

    public void loadFiles() throws IOException {
        authenticationService.loadUsersFromFile();
        inventoryService.reload();
        var unlinkedProductTypes = productTypeService.loadFromRepository();
        var unlinkedProductLines = productLineService.loadFromRepository();
        taskService.loadFromRepository();
        productTypeService.linkItemToProductType(unlinkedProductTypes);
        productLineService.linkToProductLine(unlinkedProductLines);
        productService.load();

    }

    public void saveFiles() throws IOException {
        inventoryService.save();
        productTypeService.saveToRepository();
        productLineService.saveToRepository();
        taskService.saveToRepository();
    }

}
