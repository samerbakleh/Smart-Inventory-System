package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.service.ProductLineService;
import com.inventory.app.service.ProductionService;

import java.util.List;

public class ProductLineController {

    private ProductLineService productLineService;
    private ProductionService productionService;

    public ProductLineController(ProductLineService productLineService, ProductionService productionService) {
        this.productLineService = productLineService;
        this.productionService = productionService;
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
            productionService.onProductLineStatusChanged(pl);
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

    // -1 if no production line can make this product type
    public int getLeastBusyProductLineID(ProductType pt) {
        ProductLine pl = productLineService.getLeastBusyProductLine(pt);
        return pl == null ? -1 : pl.getID();
    }

    public ProductLineService getProductLineService() {
        return productLineService;
    }
}
