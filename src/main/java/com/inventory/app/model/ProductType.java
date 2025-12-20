package com.inventory.app.model;

import java.util.HashMap;

public class ProductType {

    private String name;
    private HashMap<Item, Integer> requiredItems;

    public ProductType(String name, HashMap<Item, Integer> requiredItems) {
        this.name = name;
        this.requiredItems = requiredItems;
    }

    public String getName() {
        return name;
    }
    public HashMap<Item, Integer> getRequiredItems() {
        return requiredItems;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setRequiredItems(HashMap<Item, Integer> requiredItems) {
        this.requiredItems = requiredItems;
    }

}

