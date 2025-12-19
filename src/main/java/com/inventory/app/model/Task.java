package com.inventory.app.model;

import java.time.LocalDate;

public class Task {
    private static int IDCounter = 10000;
    private int taskID = IDCounter++;

    private int quantityRequired ;
    private Person client ;
    private int progress;  // 0 - 100
    private Product product;
    private ProductLine productLine;

    private enTaskStatus taskStatus ;

    private LocalDate startDate; // when status become "IN_PROGRESS"
    private LocalDate deliveryDate;

    public Task(int quantityRequired, Person client, int progress, Product product, ProductLine productLine,LocalDate deliveryDate) {
        this.quantityRequired = quantityRequired;
        this.client = client;
        this.progress = progress;
        this.product = product;
        this.productLine = productLine;
        this.deliveryDate=deliveryDate;
        this.taskStatus=enTaskStatus.PENDING;
        this.progress=0;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public Person getClient() {
        return client;
    }

    public void setClient(Person client) {
        this.client = client;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductLine getProductLine() {
        return productLine;
    }

    public void setProductLine(ProductLine productLine) {
        this.productLine = productLine;
    }

    public enTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(enTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
