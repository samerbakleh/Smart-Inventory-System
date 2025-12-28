package com.inventory.app.service;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.repository.CategoryRepository;
import com.inventory.app.repository.ItemRepository;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InventoryService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    private final List<Item> items = new ArrayList<>();
    private final Map<Integer, Category> categoriesById = new HashMap<>();

    public InventoryService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        reload();
    }


    public void reload() {
        lock.writeLock().lock();
        try {
            categoriesById.clear();
            for (Category c : categoryRepository.loadAll()) {
                categoriesById.put(c.getId(), c);
            }

            items.clear();
            items.addAll(itemRepository.loadAll());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void save() {
        lock.readLock().lock();
        try {

            categoryRepository.saveAll(new ArrayList<>(categoriesById.values()));
            itemRepository.saveAll(new ArrayList<>(items));
        } finally {
            lock.readLock().unlock();
        }
    }


    public List<Category> getAllCategories() {
        lock.readLock().lock();
        try {
            List<Category> list = new ArrayList<>(categoriesById.values());
            list.sort(Comparator.comparingInt(Category::getId));
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }



    public Category addCategory(String name) {
        lock.writeLock().lock();
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }

            // check existing by name (case-insensitive)
            for (Category c : categoriesById.values()) {
                if (c.getName().equalsIgnoreCase(name.trim())) {
                    return c;
                }
            }

            Category created = categoryRepository.addNew(name.trim());
            categoriesById.put(created.getId(), created);
            return created;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Category getCategoryById(int id) {
        lock.readLock().lock();
        try {
            return categoriesById.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }


    public List<Item> getAllItems() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(items);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Item findItemById(int id) {
        lock.readLock().lock();
        try {
            for (Item i : items) {
                if (i.getId() == id) return i;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Item addItem(String name, double price, int quantity, int minLimit, int categoryId) {
        lock.writeLock().lock();
        try {
            Category category = categoriesById.get(categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Category not found: " + categoryId);
            }


            Item created = new Item(name, price, quantity, minLimit, category);


            for (Item i : items) {
                if (i.getId() == created.getId()) {
                    throw new IllegalStateException("Duplicate item id generated: " + created.getId());
                }
            }

            items.add(created);
            itemRepository.saveAll(items);
            return created;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateItem(int id, String name, double price, int quantity, int minLimit, int categoryId) {
        lock.writeLock().lock();
        try {
            Category category = categoriesById.get(categoryId);
            if (category == null) throw new IllegalArgumentException("Category not found: " + categoryId);

            for (int idx = 0; idx < items.size(); idx++) {
                Item existing = items.get(idx);
                if (existing.getId() == id) {
                    Item updated = new Item(id, name, price, quantity, minLimit, category);
                    items.set(idx, updated);
                    itemRepository.saveAll(items);
                    return;
                }
            }

            throw new NoSuchElementException("Item with id " + id + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteItem(int id) {
        lock.writeLock().lock();
        try {
            boolean removed = items.removeIf(i -> i.getId() == id);
            if (removed) itemRepository.saveAll(items);
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Item> filterByNameContains(String query) {
        lock.readLock().lock();
        try {
            if (query == null || query.trim().isEmpty()) return new ArrayList<>(items);

            String q = query.trim().toLowerCase();
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getName().toLowerCase().contains(q)) result.add(i);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Item> filterByCategory(int categoryId) {
        lock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getCategory().getId() == categoryId) result.add(i);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Item> filterLowStock() {
        lock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getQuantity() <= i.getMinLimit()) result.add(i);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Item> filterOutOfStock() {
        lock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getQuantity() == 0) result.add(i);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
}
