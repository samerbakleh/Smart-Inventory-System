package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.service.ProductLineService;

import java.util.List;

public class ProductLineController {

    private ProductLineService productLineService;

    public ProductLineController(ProductLineService productLineService) {
        this.productLineService = productLineService;
    }

    public List<ProductLine> fetchProductLines() {
        return productLineService.getLoadedProductLines();
    }

    public boolean addProductLine(String productLineName, List<ProductType> productTypes) {
        if(productLineService.validateProductLineName(productLineName)) {
            productLineService.addNewProductLine(productLineName, productTypes);
            return true;
        }
        return false;
    }

    public void addProductLine(String productLineName, int productLineID) {
        productLineService.addNewProductLine(productLineName, productLineID);
    }

}
