package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.service.ProductLineService;

import java.util.List;

public class ProductLineController {

    private ProductLineService productLineService;

    public ProductLineController() {
        productLineService = new ProductLineService();
    }

    public List<ProductLine> fetchProductLines() {
        return productLineService.getLoadedProductLines();
    }

    public boolean validateProductLineName(String name) {
        return productLineService.isProductLineNameValid(name);
    }

    public void addProductLine(String productLineName, List<ProductType> productTypes) {
        productLineService.addNewProductLine(productLineName, productTypes);
    }

    public void addProductLine(String productLineName, int productLineId) {
        productLineService.addNewProductLine(productLineName, productLineId);
    }

}
