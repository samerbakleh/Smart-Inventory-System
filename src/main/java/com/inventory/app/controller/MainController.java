package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.service.ProductLineService;
import com.inventory.app.service.ProductTypeService;

import java.io.IOException;

public class MainController {

    private ProductTypeService productTypeService;
    private ProductLineService productLineService;
    /*
    private ItemService itemService;
        other class Services ....
     */

    public MainController(ProductTypeService productTypeService, ProductLineService productLineService) {
        this.productTypeService = productTypeService;
        this.productLineService = productLineService;
    }

    public void loadFiles() throws IOException {
        var unlinkedProductTypes = productTypeService.loadFromRepository();
        var unlinkedProductLines = productLineService.loadFromRepository();
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
