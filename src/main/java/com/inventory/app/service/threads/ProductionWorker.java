package com.inventory.app.service.threads;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.model.exceptions.ReachedMinLimitException;
import com.inventory.app.service.InventoryService;
import com.inventory.app.service.ProductService;

import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class ProductionWorker extends SwingWorker<Object, Object> {

    private ProductLine productLine;
    private ProductType productType;
    private InventoryService inventoryService;
    private ProductService productService;

    public ProductionWorker(ProductLine productLine, ProductType productType, InventoryService inventoryService,
                            ProductService productService) {
        this.productLine = productLine;
        this.productType = productType;
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    public ProductLine getProductLine() {
        return productLine;
    }
    public ProductType getProductType() {
        return productType;
    }

    @Override
    protected Object doInBackground() throws ReachedMinLimitException {
        if(productLine.getSublineWorkingStatus(productType)) {
            return null;
        }
        Queue<Task> tasksQueue = productLine.getTasks(productType);
        while(!tasksQueue.isEmpty() && productLine.isActive()) {
            Task task = tasksQueue.peek();
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            double time = 0;
            for(Double amount : productType.getRequiredItems().values()) {
                time += amount;
            }
            time *= 5000.0/productLine.getName().length();
            while(task.getProgress() != 100 && !task.getTaskStatus().equals(TaskStatus.CANCELLED)) {
                inventoryService.consume(productType.getRequiredItems());
                try {
                    Thread.sleep((int) time);
                } catch(InterruptedException e) {
                    /*

                    log to error file

                     */
                }
                productService.addNewProduct(productType, productLine);
                productLine.incrementTotalProduction();
                /*

                update task progress & maybe status

                 */
            }
            tasksQueue.remove();
        }
        productLine.setSublineWorkingStatus(productType, false);
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch(ExecutionException e) {
            Throwable throwable = e.getCause();
            if(throwable instanceof ReachedMinLimitException) {
                /*

                show pop-up window

                 */
            }
        } catch(InterruptedException e) {
            /*

            log to error file

             */
        }
    }

}