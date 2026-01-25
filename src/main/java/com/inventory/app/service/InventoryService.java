package com.inventory.app.service;

import com.inventory.app.exception.ReachedMinLimitException;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.Product;
import com.inventory.app.model.ProductType;
import com.inventory.app.repository.CategoryRepository;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.model.enums.Unit;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InventoryService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    private final ReadWriteLock dataLock = new ReentrantReadWriteLock();

    private final ReentrantLock ioLock = new ReentrantLock(true);

    private final AtomicLong version = new AtomicLong(0);

    // cache
    private final List<Item> items = new ArrayList<>();
    private final Map<Integer, Category> categoriesById = new HashMap<>();

    public InventoryService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = Objects.requireNonNull(itemRepository);
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
        reload();
    }

    public void reload() {
        Map<Integer, Category> loadedCatsById = new HashMap<>();
        List<Item> loadedItems;

        ioLock.lock();
        try {
            List<Category> loadedCats = categoryRepository.loadAll();
            for (Category c : loadedCats) {
                loadedCatsById.put(c.getId(), c);
            }

            loadedItems = itemRepository.loadAll(loadedCatsById);

        } finally {
            ioLock.unlock();
        }

        dataLock.writeLock().lock();
        try {
            categoriesById.clear();
            categoriesById.putAll(loadedCatsById);

            items.clear();
            items.addAll(loadedItems);

            version.incrementAndGet();
        } finally {
            dataLock.writeLock().unlock();
        }
    }

    public void save() {
        while (true) {
            List<Item> itemsSnap;
            List<Category> catsSnap;
            long expectedVersion;

            dataLock.readLock().lock();
            try {
                expectedVersion = version.get();
                itemsSnap = new ArrayList<>(items);
                catsSnap = new ArrayList<>(categoriesById.values());
            } finally {
                dataLock.readLock().unlock();
            }

            ioLock.lock();
            try {
                if (version.get() != expectedVersion) continue;

                categoryRepository.saveAll(catsSnap);
                itemRepository.saveAll(itemsSnap);
                return;
            } finally {
                ioLock.unlock();
            }
        }
    }
    public List<Category> getAllCategories() {
        dataLock.readLock().lock();
        try {
            List<Category> list = new ArrayList<>(categoriesById.values());
            list.sort(Comparator.comparingInt(Category::getId));
            return list;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public Category getCategoryById(int id) {
        dataLock.readLock().lock();
        try {
            return categoriesById.get(id);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public List<Item> getAllItems() {
        dataLock.readLock().lock();
        try {
            return new ArrayList<>(items);
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public Item findItemById(int id) {
        dataLock.readLock().lock();
        try {
            for (Item i : items) if (i.getId() == id) return i;
            return null;
        } finally {
            dataLock.readLock().unlock();
        }
    }


    public Category addCategory(String name) {
        name = (name == null) ? "" : name.trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Category name cannot be empty");

        List<Item> itemsSnap;
        List<Category> catsSnap;
        long expectedVersion;
        Category created;

        dataLock.writeLock().lock();
        try {
            for (Category c : categoriesById.values()) {
                if (c.getName().equalsIgnoreCase(name)) return c;
            }

            int newId = nextCategoryIdLocked();
            created = new Category(newId, name);
            categoriesById.put(created.getId(), created);

            expectedVersion = version.incrementAndGet();
            itemsSnap = new ArrayList<>(items);
            catsSnap = new ArrayList<>(categoriesById.values());
        } finally {
            dataLock.writeLock().unlock();
        }

        persistIfCurrent(expectedVersion, itemsSnap, catsSnap);
        return created;
    }

    public Item addItem(String name, double price,  double quantity, double minLimit, Unit unit, int categoryId) {
        List<Item> itemsSnap;
        List<Category> catsSnap;
        long expectedVersion;
        Item created;

        dataLock.writeLock().lock();
        try {
            Category category = categoriesById.get(categoryId);
            if (category == null) throw new IllegalArgumentException("Category not found: " + categoryId);

            created = new Item(name, price, quantity, minLimit, unit, category);
            items.add(created);

            expectedVersion = version.incrementAndGet();
            itemsSnap = new ArrayList<>(items);
            catsSnap = new ArrayList<>(categoriesById.values());
        } finally {
            dataLock.writeLock().unlock();
        }

        persistIfCurrent(expectedVersion, itemsSnap, catsSnap);
        return created;
    }
    public Item addItem(String name, double price, int quantity, int minLimit, int categoryId) {
        return addItem(name, price, quantity, minLimit, Unit.PCS, categoryId);
    }

    public void updateItem(int id, String name, double price, int quantity, int minLimit, int categoryId) {
        updateItem(id, name, price, quantity, minLimit, Unit.PCS, categoryId);
    }
    public void updateItem(int id, String name, double price, double quantity, double minLimit, Unit unit, int categoryId){
        List<Item> itemsSnap;
        List<Category> catsSnap;
        long expectedVersion;

        dataLock.writeLock().lock();
        try {
            Category category = categoriesById.get(categoryId);
            if (category == null) throw new IllegalArgumentException("Category not found: " + categoryId);

            for (int idx = 0; idx < items.size(); idx++) {
                Item existing = items.get(idx);
                if (existing.getId() == id) {
                    Item updated =  new Item(id, name, price, quantity, minLimit, unit, category);
                    items.set(idx, updated);

                    expectedVersion = version.incrementAndGet();
                    itemsSnap = new ArrayList<>(items);
                    catsSnap = new ArrayList<>(categoriesById.values());

                    dataLock.writeLock().unlock();
                    persistIfCurrent(expectedVersion, itemsSnap, catsSnap);
                    return;
                }
            }
            throw new NoSuchElementException("Item with id " + id + " not found");
        } finally {
            if (((ReentrantReadWriteLock.WriteLock) dataLock.writeLock()).isHeldByCurrentThread()) {
                dataLock.writeLock().unlock();
            }
        }
    }

    public boolean deleteItem(int id) {
        List<Item> itemsSnap;
        List<Category> catsSnap;
        long expectedVersion;
        boolean removed;

        dataLock.writeLock().lock();
        try {
            removed = items.removeIf(i -> i.getId() == id);
            if (!removed) return false;

            expectedVersion = version.incrementAndGet();
            itemsSnap = new ArrayList<>(items);
            catsSnap = new ArrayList<>(categoriesById.values());
        } finally {
            dataLock.writeLock().unlock();
        }

        persistIfCurrent(expectedVersion, itemsSnap, catsSnap);
        return true;
    }

    public List<Item> filterByNameContains(String query) {
        dataLock.readLock().lock();
        try {
            if (query == null || query.trim().isEmpty()) return new ArrayList<>(items);
            String q = query.trim().toLowerCase(Locale.ROOT);

            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                String n = i.getName();
                if (n != null && n.toLowerCase(Locale.ROOT).contains(q)) result.add(i);
            }
            return result;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public List<Item> filterByCategory(int categoryId) {
        dataLock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getCategory() != null && i.getCategory().getId() == categoryId) result.add(i);
            }
            return result;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public List<Item> filterLowStock() {
        dataLock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getQuantity() <= i.getMinLimit()) result.add(i);
            }
            return result;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public List<Item> filterOutOfStock() {
        dataLock.readLock().lock();
        try {
            List<Item> result = new ArrayList<>();
            for (Item i : items) {
                if (i.getQuantity() == 0) result.add(i);
            }
            return result;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    private int nextCategoryIdLocked() {
        int max = 0;
        for (int id : categoriesById.keySet()) {
            if (id > max) max = id;
        }
        return max + 1;
    }

    private void persistIfCurrent(long expectedVersion, List<Item> itemsSnap, List<Category> catsSnap) {
        ioLock.lock();
        try {
            if (version.get() != expectedVersion) return;

            categoryRepository.saveAll(catsSnap);
            itemRepository.saveAll(itemsSnap);
        } finally {
            ioLock.unlock();
        }
    }
    public static final double EPS = 1e-9;

    private void validate(Map<Item, Double> req) throws ReachedMinLimitException {
        if (req == null || req.isEmpty()) return;

        for (Map.Entry<Item, Double> e : req.entrySet()) {
            Item item = e.getKey();
            Double needObj = e.getValue();

            if (item == null) throw new IllegalArgumentException("Item is null");
            if (needObj == null) throw new IllegalArgumentException("Need is null for item");

            double need = needObj;
            if (need < -EPS) throw new IllegalArgumentException("Need cannot be negative for: " + item.getName());
            if (Math.abs(need) <= EPS) continue;

            double current = item.getQuantity();
            double min = item.getMinLimit();

            double after = current - need;

            if (after < -EPS) {
                throw new IllegalArgumentException(
                        "Not enough stock for " + item.getName() +
                                " need=" + need + " have=" + current
                );
            }


            if (after <= min + EPS) {
                throw new ReachedMinLimitException(item.getId(), item.getName(), current, need, min, after);
            }
        }
    }


    public void consume(Map<Item, Double> req) throws ReachedMinLimitException {
        if (req == null || req.isEmpty()) return;

        List<Item> itemsSnap;
        List<Category> catsSnap;
        long expectedVersion;

        dataLock.writeLock().lock();
        try {
            class P { Item item; double after; P(Item item, double after){ this.item=item; this.after=after; } }
            ArrayList<P> plan = new ArrayList<>();

            for (Map.Entry<Item, Double> e : req.entrySet()) {
                Item key = e.getKey();
                Double needObj = e.getValue();

                if (key == null) throw new IllegalArgumentException("Item is null");
                if (needObj == null) throw new IllegalArgumentException("Need is null for itemId=" + key.getId());

                double need = needObj;
                if (need < -EPS) throw new IllegalArgumentException("Need cannot be negative for: " + key.getName());
                if (Math.abs(need) <= EPS) continue;


                Item item = null;
                for (Item i : items) {
                    if (i.getId() == key.getId()) { item = i; break; }
                }
                if (item == null) throw new IllegalArgumentException("Item not found in inventory: id=" + key.getId());


                if (item.getUnit() != key.getUnit()) {
                    throw new IllegalArgumentException("Unit mismatch for itemId=" + item.getId() +
                            " itemUnit=" + item.getUnit() + " reqUnit=" + key.getUnit());
                }

                double current = item.getQuantity();
                double min = item.getMinLimit();
                double after = current - need;

                if (after < -EPS) {
                    throw new IllegalArgumentException("Not enough stock for " + item.getName() +
                            " need=" + need + " have=" + current);
                }

                if (after <= min + EPS) {
                    throw new ReachedMinLimitException(item.getId(), item.getName(), current, need, min, after);
                }

                plan.add(new P(item, after));
            }


            for (P p : plan) {
                double v = p.after;
                if (v < 0 && v > -EPS) v = 0;
                p.item.setQuantity(v);
            }


            expectedVersion = version.incrementAndGet();
            itemsSnap = new ArrayList<>(items);
            catsSnap = new ArrayList<>(categoriesById.values());

        } finally {
            dataLock.writeLock().unlock();
        }


        persistIfCurrent(expectedVersion, itemsSnap, catsSnap);
    }
    public boolean canProduce(ProductType productType, double productCount) {
        if (productType == null) throw new IllegalArgumentException("ProductType is null");
        if (productCount < -EPS) throw new IllegalArgumentException("Product count cannot be negative");
        if (Math.abs(productCount) <= EPS) return true;

        Map<Item, Double> perUnit = productType.getRequiredItems();
        if (perUnit == null || perUnit.isEmpty()) return true;

        dataLock.readLock().lock();
        try {
            for (Map.Entry<Item, Double> e : perUnit.entrySet()) {
                Item reqItemKey = e.getKey();
                Double perUnitNeedObj = e.getValue();

                if (reqItemKey == null) return false;
                if (perUnitNeedObj == null) return false;

                double perUnitNeed = perUnitNeedObj;
                if (perUnitNeed < -EPS) return false;
                if (Math.abs(perUnitNeed) <= EPS) continue;

                Item invItem = null;
                for (Item i : items) {
                    if (i.getId() == reqItemKey.getId()) { invItem = i; break; }
                }
                if (invItem == null) return false;

                double need = perUnitNeed * productCount;
                if (Math.abs(need) <= EPS) continue;

                double current = invItem.getQuantity();
                double after = current - need;

                if (after < -EPS) return false;


                if (after <= invItem.getMinLimit() + EPS) return false;
            }
            return true;
        } finally {
            dataLock.readLock().unlock();
        }
    }

    public boolean canProduce(Product product, double productCount) {
        if (product == null) throw new IllegalArgumentException("Product is null");
        return canProduce(product.getProductType(), productCount);
    }




}

