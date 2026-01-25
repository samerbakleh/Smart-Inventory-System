package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.service.ProductLineService;

import java.util.List;

public class ProductLineController {

    private ProductLineService productLineService;

    public ProductLineController(ProductLineService productLineService) {
        this.productLineService = productLineService;
    }

    public List<ProductLine> fetchProductLineMainData() {
        return productLineService.getLoadedProductLines();
    }

    public List<Float> getProductLineProductionPercentage() {
        return productLineService.calculateProductionPercentage();
    }

    public boolean addProductLine(String productLineName, List<String> productTypes) {
        if(productLineService.validateProductLineName(productLineName)) {
            productLineService.addNewProductLine(productLineName, productTypes);
            return true;
        }
        return false;
    }

    public boolean changeProductLineName(ProductLine pl, String name) {
        return productLineService.changeProductLineName(pl, name);
    }

    public void changeProductLineStatus(ProductLine pl, String action) {
        try {
            ProductLineStatus status = ProductLineStatus.valueOf(action);
            productLineService.changeProductLineStatus(pl, status);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid production line status command", e);
        }
    }

    public void changeProductLineRating(ProductLine pl, int rating) {
        productLineService.changeProductLineRating(pl, rating);
    }

    public void changeProductLineNote(ProductLine pl, String note) {
        productLineService.changeProductLineNote(pl, note);
    }

    public int getLeastBusyProductLineID(ProductType pt) {
        return productLineService.getLeastBusyProductLine(pt).getID();
    }

    public ProductLineService getProductLineService() {
        return productLineService;
    }
}
