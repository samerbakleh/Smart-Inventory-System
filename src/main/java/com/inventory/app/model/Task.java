package com.inventory.app.model;

import com.inventory.app.model.enums.TaskStatus;

import java.time.LocalDate;

public class Task {
    private static int IDCounter = 10000;
    private int taskID = IDCounter++;

    private int quantityRequired ;
    private String clientName;
    private int progress;// 0 - 100

    private String productName;
    private int productLineID;

    private TaskStatus taskStatus ;

    private LocalDate startDate; // when status become "IN_PROGRESS"
    private LocalDate deliveryDate;

    private boolean isTaken=false;
    private boolean isSaved=false;

    public Task(int quantityRequired, String productName ,String clientName , int productLineID,LocalDate deliveryDate) {
        this.quantityRequired = quantityRequired;
        this.clientName = clientName;
        this.productName = productName;
        this.productLineID = productLineID;
        this.deliveryDate= deliveryDate;
        this.taskStatus = TaskStatus.PENDING;
        this.progress=0;

    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductLineID() {
        return productLineID;
    }

    public void setProductLineID(int productLineID) {
        this.productLineID = productLineID;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }


    public void setTaskStatus(TaskStatus taskStatus) {
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

    public int getTaskID() {
        return taskID;
    }
    public boolean isTaken(){
        return isTaken;
    }

    public void setIsTaken(boolean taken) {
        isTaken = taken;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
