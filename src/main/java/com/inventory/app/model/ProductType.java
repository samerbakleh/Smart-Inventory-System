package com.inventory.app.model;

import java.util.Map;

public class ProductType {

    private String name;
    private Map<Item, Integer> requiredItems;
    private ProductLine productLine;

    public ProductType(String name, Map<Item, Integer> requiredItems, ProductLine productLine) {
        this.name = name;
        this.requiredItems = requiredItems;
        this.productLine = productLine;
    }

    public String getName() {
        return name;
    }
    public Map<Item, Integer> getRequiredItems() {
        return requiredItems;
    }
    public ProductLine getProductLine() {
        return  productLine;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setRequiredItems(Map<Item, Integer> requiredItems) {
        this.requiredItems = requiredItems;
    }
    public void setProductLine(ProductLine productLine) {
        this.productLine = productLine;
    }

}

