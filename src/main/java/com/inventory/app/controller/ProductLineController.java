package com.inventory.app.controller;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.service.ProductLineService;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class ProductLineController {

    private ProductLineService productLineService;

    public ProductLineController(ProductLineService productLineService) {
        this.productLineService = productLineService;
    }

    public Map<ProductLine, Float> fetchProductLines() {
        List<ProductLine> productLines = productLineService.getLoadedProductLines();
        float[] percentages = productLineService.calculateProductionPercentage();
        int n = productLines.size();
        Map<ProductLine, Float> result = new LinkedHashMap<>();
        for(int i = 0; i < n; i++) {
            result.put(productLines.get(i), percentages[i]);
        }
        return result;
    }

    public boolean addProductLine(String productLineName, List<ProductType> productTypes) {
        if(productLineService.validateProductLineName(productLineName)) {
            productLineService.addNewProductLine(productLineName, productTypes);
            return true;
        }
        return false;
    }

}
