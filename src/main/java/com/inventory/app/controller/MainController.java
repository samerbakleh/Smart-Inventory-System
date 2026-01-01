package com.inventory.app.controller;

import com.inventory.app.repository.UserRepository;
import com.inventory.app.service.ProductTypeService;

import java.io.IOException;

public class MainController {

    private ProductTypeService productTypeService;
    private UserRepository userRepository;
    /*
    private ItemService itemService;
    private ProductLineService productLineService;
        other class Services ....
     */

    public MainController() {
        productTypeService = new ProductTypeService();
        userRepository = new UserRepository();
        //itemService = new ItemService(this);
        // ...
    }

    public void loadFiles() throws IOException {
        var unlinkedProductTypes = productTypeService.loadFromRepository();
        userRepository.loadUsersFromFile();
        /*
        itemService.loadFromRepository();
        productLineService.loadFromRepository();
            ....
         */
        productTypeService.linkItemToProductType(unlinkedProductTypes, itemService.getItemRepository());
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
