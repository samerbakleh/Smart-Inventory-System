package com.inventory.app.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Product {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private final int id;
    private final ProductType productType;

    public Product(ProductType productType) {
        this.id = idCounter.getAndIncrement();
        this.productType = productType;
    }

    public ProductType getProductType() {
        return productType;
    }

    public int getId() {
        return id;
    }

}


