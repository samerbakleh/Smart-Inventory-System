package com.inventory.app.service;
import com.inventory.app.model.Product;
import com.inventory.app.repository.ProductRepository;
public class ProductService {
    private ProductRepository productRepository = new ProductRepository();
    public boolean deleteById(int id)
    {
        if(!productRepository.isExist(id))
            return false;
        productRepository.deleteByID(id);
        return true ;
    }


    public boolean add(Product product)
    {
        if(productRepository.isExist(product.getId()))
            return false;
        productRepository.add(product);
            return true;
    }
}
