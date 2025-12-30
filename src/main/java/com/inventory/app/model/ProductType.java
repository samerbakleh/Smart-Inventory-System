package com.inventory.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductType {

    private String name;
    private String subType;
    private Map<Item, Double> requiredItems;
    private List<ProductLine> productLines;

    public ProductType(String name, String subType) {
        this.name = name;
        this.subType = subType;
    }
    public ProductType(String name, String subType, Map<Item, Double> requiredItems, List<ProductLine> productLines) {
        this.name = name;
        setRequiredItems(requiredItems);
        this.productLines = productLines;
    }

    public String getName() {
        return name;
    }
    public String getSubType() {
        return subType;
    }
    public Map<Item, Double> getRequiredItems() {
        return requiredItems;
    }
    public List<ProductLine> getProductLines() {
        return  productLines;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSubType(String subType) {
        this.subType = subType;
    }
    public void setRequiredItems(Map<Item, Double> requiredItems) {
        for(Map.Entry<Item, Double> entry : requiredItems.entrySet())
            if(entry.getValue() <= 0)
                throw new IllegalArgumentException(entry.getKey().getName()
                                                    + " required amount can't be negative or zero in " + this);
        this.requiredItems = requiredItems;
    }
    public void setProductLine(List<ProductLine> productLines) {
        this.productLines = productLines;
    }
    public void addProductLine(ProductLine pl) {
        if(productLines == null)
            productLines = new ArrayList<>();
        productLines.add(pl);
    }

    @Override
    public String toString() {
        return name + " " + subType;
    }

}