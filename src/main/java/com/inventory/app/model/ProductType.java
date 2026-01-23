package com.inventory.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductType {
    private String name;
    private String subtype;
    private Map<Item, Double> requiredItems;
    private List<ProductLine> productLines;
    public ProductType(String name, String subtype) {
        this.name = name;
        this.subtype = subtype;
    }
    public ProductType(String name, String subtype, Map<Item, Double> requiredItems, List<ProductLine> productLines) {
        this.name = name;
        this.subtype = subtype;
        setRequiredItems(requiredItems);
        this.productLines = productLines;
    }

    public String getName() {
        return name;
    }
    public String getSubtype() {
        return subtype;
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
    public void setSubtype(String subtype) {
        this.subtype = subtype;
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
        return name + " " + subtype;
    }

}