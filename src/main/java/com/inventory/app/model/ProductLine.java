package com.inventory.app.model;

import com.inventory.app.model.enums.ProductLineStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductLine {

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private final int id;
    private volatile String name;
    private volatile ProductLineStatus status;              // read by production threads, changed from the UI
    private Map<ProductType, Queue<Task>> tasks;            // one queue per subline, filled before production starts
    private volatile int rating;
    private volatile String note;
    private final AtomicInteger totalProduction;


    public ProductLine(String name, List<ProductType> productTypes) {
        this.id = idCounter.getAndIncrement();
        this.name = name;
        this.status = ProductLineStatus.ACTIVE;
        this.tasks = new HashMap<>();
        setProductTypes(productTypes);
        this.rating = 0;
        this.note = "";
        this.totalProduction = new AtomicInteger(0);
    }
    public ProductLine(int id, String name, ProductLineStatus status, int rating, String note, int totalProduction) {
        if(id < 0) {
            throw new IllegalArgumentException("Production Line ID can't be negative");
        }
        this.id = id;
        idCounter.accumulateAndGet(id + 1, Math::max);
        this.name = name;
        this.status = status;
        this.tasks = new HashMap<>();
        this.rating = rating;
        this.note = note;
        this.totalProduction = new AtomicInteger(totalProduction);
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
    public int getRating() {
        return rating;
    }
    public String getNote() {
        return note;
    }
    public int getTotalProduction() {
        return totalProduction.get();
    }
    public int getNumOfTasks() {
        int total = 0;
        for(Queue<Task> queue : tasks.values()) {
            total += queue.size();
        }
        return total;
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
        tasks.clear();
        if(!productTypes.isEmpty()) {
            String name = productTypes.getFirst().getName();
            for(ProductType pt : productTypes) {
                if (pt.getName().equals(name)) {
                    tasks.put(pt, new ConcurrentLinkedQueue<>());
                } else {
                    throw new IllegalArgumentException("Can't add products of different names to one production line");
                }
            }
        }
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public void incrementTotalProduction() {
        totalProduction.incrementAndGet();
    }

    public boolean isActive() {
        return status == ProductLineStatus.ACTIVE;
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
