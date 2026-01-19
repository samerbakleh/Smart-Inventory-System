package com.inventory.app.model;

import com.inventory.app.model.enums.ProductLineStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;

public class ProductLine {

    private static int idCounter = 0;

    private final int id;
    private String name;
    private ProductLineStatus status;
    private Map<ProductType, Queue<Task>> tasks;
    private int totalProduction;
    private Map<ProductType, Boolean> sublineWorkingStatus;     // true for each product type that the subline producing
                                                                // it is working, false otherwise


    public ProductLine(String name, List<ProductType> productTypes) {
        this.id = idCounter++;
        this.name = name;
        this.status = ProductLineStatus.ACTIVE;
        setProductTypes(productTypes);
        this.totalProduction = 0;
    }
    public ProductLine(int id, String name, ProductLineStatus status, int totalProduction) {
        if(id < 0) {
            throw new IllegalArgumentException("Production Line ID can't be negative");
        }
        this.id = id;
        idCounter = id + 1;
        this.name = name;
        this.status = status;
        this.totalProduction = totalProduction;
    }

    public int getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ProductLineStatus getStatus() {
        return status;
    }
    public Map<ProductType, List<Task>> getTasks() {
        Map<ProductType, List<Task>> taskListMap = new HashMap<>();
        for(Map.Entry<ProductType, Queue<Task>> e : tasks.entrySet())
            taskListMap.put(e.getKey(), new ArrayList<>(e.getValue()));
        return taskListMap;
    }
    public Queue<Task> getTasks(ProductType pt) {
        return tasks.get(pt);
    }
    public List<ProductType> getProductTypes() {
        return new ArrayList<>(tasks.keySet());
    }
    public Map<ProductType, Boolean> getSublineWorkingStatus() {
        return sublineWorkingStatus;
    }
    public int getTotalProduction() {
        return totalProduction;
    }
    public boolean getSublineWorkingStatus(ProductType pt) {
        return sublineWorkingStatus.get(pt);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setStatus(ProductLineStatus status) {
        this.status = status;
    }
    public void setTasks(List<Task> tasks) {                   //tasks have to be in order
        for(Queue<Task> q : this.tasks.values()) {
            q.clear();
        }
        for(Task t : tasks) {
            addTask(t);
        }
    }
    public void setProductTypes(List<ProductType> productTypes) {
        tasks = new HashMap<>();
        sublineWorkingStatus = new HashMap<>();
        if(!productTypes.isEmpty()) {
            String name = productTypes.getFirst().getName();
            for(ProductType pt : productTypes) {
                if (pt.getName().equals(name)) {
                    tasks.put(pt, new ArrayDeque<>());
                    sublineWorkingStatus.put(pt, false);
                } else {
                    throw new IllegalArgumentException("Can't add products of different names to one production line");
                }
            }
        }
    }
    public void setSublineWorkingStatus(ProductType pt, boolean isWorking) {
        if(sublineWorkingStatus.get(pt) == null) {
            throw new IllegalArgumentException("Can't change the status of non-existent subline " + pt + " in " + this);
        }
        sublineWorkingStatus.put(pt, isWorking);
    }

    public void incrementTotalProduction() {
        totalProduction++;
    }

    public boolean isActive() {
        return status.equals(ProductLineStatus.ACTIVE);
    }

    public void addTask(Task task) {
        Queue<Task> taskQueue = tasks.get(task.getProductType());
        if(taskQueue == null) {
            throw new IllegalArgumentException(this + " received incorrect task of type "
                                             + task.getProductType().getName());
        }
        taskQueue.add(task);
    }

                                                //returns true if task exists, false otherwise
    public boolean removeTask(Task task) {
        Queue<Task> taskQueue = tasks.get(task.getProductType());
        if(taskQueue == null) {
            throw new IllegalArgumentException("Tried to remove wrong task of type " + task.getProductType()
                                             + " from " + this);
        }
        return taskQueue.remove(task);
    }

    @Override
    public String toString() {
        return "PL#" + id + " " + name;
    }

}
