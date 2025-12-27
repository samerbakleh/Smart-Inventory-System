package com.inventory.app.model;

import java.util.Map;

public class ProductType {

    private String name;
    private Map<Item, Double> requiredItems;
    private ProductLine productLine;

    public ProductType(String name) {
        this.name = name;
    }
    public ProductType(String name, Map<Item, Double> requiredItems, ProductLine productLine) {
        this.name = name;
        setRequiredItems(requiredItems);
        this.productLine = productLine;
    }

    public String getName() {
        return name;
    }
    public Map<Item, Double> getRequiredItems() {
        return requiredItems;
    }
    public ProductLine getProductLine() {
        return  productLine;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setRequiredItems(Map<Item, Double> requiredItems) {
        for(Map.Entry<Item, Double> e : requiredItems.entrySet())
            if(e.getValue() <= 0)
                throw new IllegalArgumentException(e.getKey().getName()
                                                    + " required amount can't be negative or zero in " + name);
        this.requiredItems = requiredItems;
    }
    public void setProductLine(ProductLine productLine) {
        this.productLine = productLine;
    }

}