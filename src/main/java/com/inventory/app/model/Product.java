package com.inventory.app.model;

import java.util.concurrent.atomic.AtomicInteger;
public class Product {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private final int id;
    private ProductType productType ;
    private int  productLineId;
    // this constructor used when load data from file
    public Product(int id , ProductType productType , int productLineId)
    {
        this.id=id;
        idCounter.accumulateAndGet(id + 1, Math::max);
        this.productType=productType;
        this.productLineId = productLineId;
    }



    //this constructor used when creating new product
    public Product(ProductType productType , int productLineId )
    {
        this.id=idCounter.getAndIncrement();
        this.productType=productType;
        this.productLineId= productLineId;
    }

    public int getId() {
        return id;
    }

    public ProductType getProductType() {
        return productType;
    }

    public int getProductLineId() {
        return productLineId;
    }
}


