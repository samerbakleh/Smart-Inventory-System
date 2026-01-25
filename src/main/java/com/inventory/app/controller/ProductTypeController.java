package com.inventory.app.controller;

import com.inventory.app.model.Item;
import com.inventory.app.model.ProductType;
import com.inventory.app.service.ProductTypeService;

import java.util.List;
import java.util.Map;

public class ProductTypeController {
    private ProductTypeService productTypeService;

    public ProductTypeController(ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    public boolean add(String name, String subtype, Map<Item, Double> requiredItems) {
        ProductType productType = new ProductType(name, subtype, requiredItems);
        return productTypeService.add(productType);
    }

    public List<ProductType> getProductTypes()
    {
        return productTypeService.getProductTypes();
    }
}
