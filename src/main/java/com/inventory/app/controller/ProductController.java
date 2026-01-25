package com.inventory.app.controller;

import com.inventory.app.model.Product;
import com.inventory.app.service.ProductService;

import java.util.List;

public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    public List<Product> getProducts()
    {
        return productService.getProducts();
    }
}
