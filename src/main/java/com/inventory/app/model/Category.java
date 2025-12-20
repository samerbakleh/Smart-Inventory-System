package com.inventory.app.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Category implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    private final int id;
    private String name;
    public Category(String name) {
        this.id = idCounter.getAndIncrement();
        this.name = name;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        if (id >= idCounter.get()) {
            idCounter.set(id + 1);
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }
}
