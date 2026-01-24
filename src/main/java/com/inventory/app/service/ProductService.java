package com.inventory.app.service;

import com.inventory.app.model.Product;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.ProductRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private ProductRepository productRepository = new ProductRepository();

    public boolean deleteById(int id) {
        if (!productRepository.isExist(id))
            return false;
        productRepository.deleteByID(id);
        return true;
    }


    public boolean add(ProductType productType, int productLineId) {
        Product product = new Product(productType, productLineId);
        if (productRepository.isExist(product.getId()))
            return false;
        productRepository.add(product);
        return true;
    }


    public List<ProductLine> findLinesByProductAndDateRange(
            int productId,
            LocalDate fromDate,
            LocalDate toDate) {
        List<ProductLine> productLines = new ArrayList<>();
        Product product = productRepository.findById(productId);
        if (product != null) {

            for (ProductLine pL : product.getProductType().getProductLines()) {
                for (Task t : pL.getTasks()) {
                    if (t.getStartDate().isAfter(fromDate) && t.getDeliveryDate().isAfter(toDate) && t.getProduct().getProductType().toString().equals(product.getProductType().toString()))
                        ;
                    {
                        productLines.add(pL);
                        break;
                    }
                }
            }
        }
        return productLines;
    }

    List<ProductLine> findLinesByProductAndClient(
            int productId,
            String client) {
        List<ProductLine> productLines = new ArrayList<>();
        Product product = productRepository.findById(productId);
        if (product != null) {

            for (ProductLine pL : product.getProductType().getProductLines()) {
                for (Task t : pL.getTasks()) {
                    if (t.getClient().getFullName().equals(client) && t.getProduct().getProductType().toString().equals(product.getProductType().toString()))
                        ;
                    {
                        productLines.add(pL);
                        break;
                    }
                }
            }
        }
        return productLines;
    }

    List<ProductLine> findLinesByProductAndStatus(
            int productId,
            TaskStatus status) {
        List<ProductLine> productLines = new ArrayList<>();
        Product product = productRepository.findById(productId);
        if (product != null) {
            for (ProductLine pL : product.getProductType().getProductLines()) {
                boolean isAllTaskCompleted = false;
                for (Task t : pL.getTasks()) {
                    if (t.getProduct().getProductType().toString().equals(product.getProductType().toString())) ;
                    {
                        isAllTaskCompleted = (status == t.getTaskStatus());
                    }
                }
                if (isAllTaskCompleted) productLines.add(pL);
            }
        }
        return productLines;
    }


}
