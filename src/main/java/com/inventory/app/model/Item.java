package com.inventory.app.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Item implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    private final int id;

    private String name;
    private double price;
    private int quantity;
    private int minLimit;
    private Category category;

    public Item(String name, double price, int quantity, int minLimit, Category category) {
        this.id = idCounter.getAndIncrement();
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.minLimit = minLimit;
        this.category = category;
    }

    public Item(int id, String name, double price, int quantity, int minLimit, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.minLimit = minLimit;
        this.category = category;

        if (id >= idCounter.get()) {
            idCounter.set(id + 1);
        }
    }

    public synchronized void addQuantity(int amount) {
        if (amount > 0) this.quantity += amount;
    }

    public synchronized void deductQuantity(int amount) {
        this.quantity -= amount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinLimit() {
        return minLimit;
    }
    public void setMinLimit(int minLimit) {
        this.minLimit = minLimit;
    }


    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name + " | Quantity: " + quantity + " [" + category.getName() + "]";
    }
}
