package com.inventory.app.controller;

import com.inventory.app.service.ProductLineService;

public class ProductLineController {

    private ProductLineService productLineService;

    public ProductLineController() {
        productLineService = new ProductLineService();
    }

}
