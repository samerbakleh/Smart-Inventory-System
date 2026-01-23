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

    public boolean addProductLine(String productLineName, List<ProductType> productTypes) {
        if(productLineService.validateProductLineName(productLineName)) {
            productLineService.addNewProductLine(productLineName, productTypes);
            return true;
        }
        return false;
    }

    public void changeProductLineStatus(ProductLine pl, String action) {
        if(action.equals("activate")) {
            productLineService.changeProductLineStatus(pl, ProductLineStatus.ACTIVE);
        } else if(action.equals("stop")) {
            productLineService.changeProductLineStatus(pl, ProductLineStatus.STOPPED);
        } else if(action.equals("maintain")) {
            productLineService.changeProductLineStatus(pl, ProductLineStatus.MAINTENANCE);
        } else {
            throw new IllegalArgumentException("Invalid production line status command");
        }
    }

    public void changeProductLineRating(ProductLine pl, int rating) {
        productLineService.changeProductLineRating(pl, rating);
    }

    public void changeProductLineNote(ProductLine pl, String note) {
        productLineService.changeProductLineNote(pl, note);
    }

}
