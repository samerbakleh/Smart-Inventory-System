package com.inventory.app.controller;

import com.inventory.app.service.ProductTypeService;

public class MainController {

    //private MainFrame mainFrame;
    private ProductTypeService productTypeService;
    /*
    private ItemService itemService;
    private ProductLineService productLineService;
        other class Services ....
     */

    public MainController() {
        //mainFrame = new MainFrame(this);
        productTypeService = new ProductTypeService(this);
        //itemService = new ItemService(this);
        // ...
    }

    public void appStarted() {
        //mainFrame.displayLogin();
        var unlinkedProductTypes = productTypeService.loadFromRepository();
        /*
        itemService.loadFromRepository();
        productLineService.loadFromRepository();
            ....
         */
        productTypeService.linkItemToProductType(unlinkedProductTypes, itemService.getItemRepository());
    }

    public void appExited() {

        productTypeService.saveToRepository();
        /*
        itemService.saveToRepository();
        productLineService.saveToRepository();
            ...
        mainFrame.close();
         */
    }

    public void exceptionHappened(String message) {
        // mainFrame.displayError(message);
    }

}
