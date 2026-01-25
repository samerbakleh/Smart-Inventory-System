package com.inventory.app.service;

import com.inventory.app.model.Product;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.ProductRepository;
import java.util.Queue;

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
                Queue<Task> queue = pL.getTasks(product.getProductType());
                if (queue == null) continue;

                for (Task t : queue) {
                    if (t.getStartDate().isAfter(fromDate)
                            && t.getDeliveryDate().isAfter(toDate)
                            && t.getProductName().equals(product.getProductType().toString())) {

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
                Queue<Task> queue = pL.getTasks(product.getProductType());
                if (queue == null) continue;

                for (Task t : queue) {
                    if (t.getClientName().equals(client)
                            && t.getProductName().equals(product.getProductType().toString())) {

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
                Queue<Task> queue = pL.getTasks(product.getProductType());
                if (queue == null || queue.isEmpty()) continue;

                boolean isAllTaskStatusMatch = true;
                for (Task t : queue) {
                    if (!t.getProductName().equals(product.getProductType().toString())
                            || t.getTaskStatus() != status) {

                        isAllTaskStatusMatch = false;
                        break;
                    }
                }
                if (isAllTaskStatusMatch) {
                    productLines.add(pL);
                }
            }
        }
        return productLines;
    }

    public List<Product> getProductsProductLine(int productLineId)
    {
        List<Product> products = new ArrayList<>();
        for(Product p : products)
        {
            if(p.getProductLineId()==productLineId)
            {
                products.add(p);
            }
        }
        return products;
    }


}
