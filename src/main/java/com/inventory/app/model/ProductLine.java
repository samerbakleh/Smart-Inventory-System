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


    public ProductLine(int id, String name, ProductLineStatus status) {
        if(id < 0) {
            throw new IllegalArgumentException("Production Line ID can't be negative");
        }
        this.id = id;
        idCounter = id + 1;
        this.name = name;
        this.status = status;
    }
    public ProductLine(String name, List<ProductType> productTypes) {
        this.id = idCounter++;
        this.name = name;
        this.status = ProductLineStatus.ACTIVE;
        setProductTypes(productTypes);
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ProductLineStatus getStatus() {
        return status;
    }
    public Map<ProductType, List<Task>> getTasks() {
        Map<ProductType, List<Task>> taskList = new HashMap<>();
        for(Map.Entry<ProductType, Queue<Task>> e : tasks.entrySet())
            taskList.put(e.getKey(), new ArrayList<>(e.getValue()));
        return taskList;
    }
    public List<ProductType> getProductTypes() {
        return new ArrayList<>(tasks.keySet());
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
        if(!productTypes.isEmpty()) {
            String name = productTypes.getFirst().getName();
            for(ProductType pt : productTypes) {
                if (pt.getName().equals(name)) {
                    tasks.put(pt, new ArrayDeque<>());
                } else {
                    throw new IllegalArgumentException("Can't add products of different names to production line");
                }
            }
        }
    }

    public void addTask(Task task) {
        Queue<Task> taskQueue = tasks.get(task.getProductType());
        if(taskQueue == null) {
            throw new IllegalArgumentException(this + " received incorrect task of type "
                                             + task.getProductType().getName());
        }
        taskQueue.add(task);
    }

    public boolean removeTask(ProductType productType) {        //returns true if task exists, false otherwise
        Queue<Task> taskQueue = tasks.get(productType);
        if(taskQueue == null) {
            throw new IllegalArgumentException("Tried to remove wrong task type " + productType.getName()
                                             + " from " + this);
        }
        return taskQueue.poll() != null;
    }

    @Override
    public String toString() {
        return "PL#" + id + " " + name;
    }

}
