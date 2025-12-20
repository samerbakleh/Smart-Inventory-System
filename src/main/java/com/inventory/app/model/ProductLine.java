package com.inventory.app.model;

import com.inventory.app.model.enums.ProductLineState;
import java.util.HashMap;
import java.util.Queue;
import java.util.ArrayDeque;

public class ProductLine {

    private static int idCounter = 0;

    private final int id = idCounter++;
    private String name;
    private ProductLineState state = ProductLineState.ACTIVE;
    private HashMap<Product, Queue<Task>> tasks = new HashMap<>();   // ( maybe changed later ) Product is a placeholder for key


    public ProductLine(String name, Product... products) {
        this.name = name;
        for(Product p : products)
            tasks.put(p, new ArrayDeque<Task>());
    }

    public int getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ProductLineState getState() {
        return state;
    }
    public HashMap<Product, Queue<Task>> getTasks() {
        return tasks;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setState(ProductLineState state) {
        this.state = state;
    }

    public void addTask(Task task) {
        Queue<Task> taskQueue = tasks.get(task.getProduct());
        if(taskQueue == null)
            throw new IllegalArgumentException("");
    }

}
