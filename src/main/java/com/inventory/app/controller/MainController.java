package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.repository.UserRepository;
import com.inventory.app.service.AuthenticationService;
import com.inventory.app.service.ProductLineService;
import com.inventory.app.service.ProductService;
import com.inventory.app.service.ProductTypeService;

import java.io.IOException;

public class MainController {

    private ProductTypeService productTypeService;
    private ProductLineService productLineService;
    private AuthenticationService authenticationService;
    private ProductService productService;
    /*
    private ItemService itemService;
        other class Services ....
     */

    public MainController(ProductTypeService productTypeService, ProductService productService, ProductLineService productLineService, AuthenticationService authenticationService) {
        this.productTypeService = productTypeService;
        this.productLineService = productLineService;
        this.authenticationService = authenticationService;
        this.productService=productService;
    }

    public void loadFiles() throws IOException {
        var unlinkedProductTypes = productTypeService.loadFromRepository();
        var unlinkedProductLines = productLineService.loadFromRepository();
        authenticationService.loadUsersFromFile();
        productService.load();

        /*
        itemService.loadFromRepository();
        productLineService.loadFromRepository();
            ....
         */
        productTypeService.linkItemToProductType(unlinkedProductTypes);
        productLineService.linkToProductLine(unlinkedProductLines);
    }

    public void saveFiles() throws IOException {

        productTypeService.saveToRepository();
        /*
        itemService.saveToRepository();
        productLineService.saveToRepository();
            ...
         */
    }

}
