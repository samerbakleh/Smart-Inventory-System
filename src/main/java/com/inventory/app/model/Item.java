package com.inventory.app.model;

import com.inventory.app.model.enums.Unit;

import java.io.Serializable;
import java.util.Locale;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idCounter = 1;

    private static synchronized int nextId() {
        return idCounter++;
    }

    private static synchronized void syncCounter(int loadedId) {
        if (loadedId >= idCounter) idCounter = loadedId + 1;
    }

    private final int id;

    private String name;
    private double price;
    private double quantity;
    private double minLimit;
    private Unit unit;
    private Category category;

    public Item(String name, double price, double quantity, double minLimit, Category category) {
        this(nextId(), name, price, quantity, minLimit, Unit.PCS, category);
    }
    public Item(String name, double price, double quantity, double minLimit, Unit unit, Category category) {
        this(nextId(), name, price, quantity, minLimit, unit, category);
    }
    public Item(int id, String name, double price, double quantity, double minLimit, Unit unit, Category category) {
        if (id <= 0) throw new IllegalArgumentException("Item id must be positive");
        this.id = id;

        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setMinLimit(minLimit);
        setUnit(unit);
        setCategory(category);

        syncCounter(id);
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Item name cannot be empty");
        this.name = name.trim();
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity;
    }

    public double getMinLimit() { return minLimit; }
    public void setMinLimit(double minLimit) {
        if (minLimit < 0) throw new IllegalArgumentException("Min limit cannot be negative");
        this.minLimit = minLimit;
    }
    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        this.unit = unit;
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s | Quantity: %d %s [%s]", name, quantity, unit, category.getName());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return this.id == ((Item) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
