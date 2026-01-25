package com.inventory.app.service.threads;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.exception.ReachedMinLimitException;
import com.inventory.app.service.InventoryService;
import com.inventory.app.service.ProductService;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.ErrorLog;

import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class ProductionWorker extends SwingWorker<Object, Object> {

    private ProductLine productLine;
    private ProductType productType;
    private InventoryService inventoryService;
    private ProductService productService;
    private TaskService taskService;

    public ProductionWorker(ProductLine productLine, ProductType productType, InventoryService inventoryService,
                            ProductService productService, TaskService taskService) {
        this.productLine = productLine;
        this.productType = productType;
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.taskService = taskService;
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
            taskService.startTask(task.getTaskID());
            double time = 0;
            for(Double amount : productType.getRequiredItems().values()) {
                time += amount;
            }
            time *= 5000.0/productLine.getName().length();
            while(task.getProgress() != 100 && !task.getTaskStatus().equals(TaskStatus.CANCELLED)) {
                inventoryService.consumeReserved(productType.getRequiredItems());
                try {
                    Thread.sleep((int) time);
                } catch(InterruptedException e) {
                    ErrorLog.log(LogLevel.ERROR," Thread ","InterruptedException",e);
                }
                productService.add(productType, productLine.getID());
                productLine.incrementTotalProduction();
                taskService.completeTask(task.getTaskID());
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
            ErrorLog.log(LogLevel.ERROR," Thread ","InterruptedException",e);
        }
    }

}