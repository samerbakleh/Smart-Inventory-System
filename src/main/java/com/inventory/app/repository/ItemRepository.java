package com.inventory.app.repository;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.util.AppConstants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Locale;

public class ItemRepository {

    private final CategoryRepository categoryRepository;

    public ItemRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private void ensureDataDir() {
        new File(AppConstants.DATA_DIR).mkdirs();
    }

    public List<Item> loadAll() {
        ensureDataDir();

        List<Item> items = new ArrayList<>();
        File file = new File(AppConstants.ITEMS_FILE);
        if (!file.exists()) return items;

        Map<Integer, Category> categoriesById = categoryRepository.loadById();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;


                if (line.toLowerCase().startsWith("id,")) continue;

                String[] parts = line.split(AppConstants.CSV_DELIMITER, -1);
                if (parts.length < 6) continue;

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int qty = Integer.parseInt(parts[3].trim());
                    int minLimit = Integer.parseInt(parts[4].trim());
                    int categoryId = Integer.parseInt(parts[5].trim());

                    Category category = categoriesById.get(categoryId);
                    if (category == null) {
                        System.err.println("Skipping item (unknown categoryId=" + categoryId + "): " + line);
                        continue;
                    }

                    items.add(new Item(id, name, price, qty, minLimit, category));
                } catch (NumberFormatException ex) {
                    System.err.println("Skipping invalid line: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading items file: " + e.getMessage());
        }

        return items;
    }

    public void saveAll(List<Item> items) {
        ensureDataDir();

        File file = new File(AppConstants.ITEMS_FILE);

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            bw.write("id,name,price,qty,minLimit,categoryId");
            bw.newLine();

            for (Item item : items) {
                String line = String.format(Locale.US, "%d,%s,%.2f,%d,%d,%d",
                        item.getId(),
                        safe(item.getName()),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getMinLimit(),
                        item.getCategory().getId()
                );
                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error saving items file: " + e.getMessage());
        }
    }

    public void add(Item item) {
        List<Item> all = loadAll();
        boolean exists = all.stream().anyMatch(i -> i.getId() == item.getId());
        if (exists) throw new IllegalArgumentException("Item id already exists: " + item.getId());
        all.add(item);
        saveAll(all);
    }

    public void update(Item updatedItem) {
        List<Item> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == updatedItem.getId()) {
                all.set(i, updatedItem);
                saveAll(all);
                return;
            }
        }
        throw new NoSuchElementException("Item with ID " + updatedItem.getId() + " not found.");
    }

    public boolean deleteById(int id) {
        List<Item> all = loadAll();
        boolean removed = all.removeIf(i -> i.getId() == id);
        if (removed) saveAll(all);
        return removed;
    }

    public Item findById(int id) {
        for (Item i : loadAll()) {
            if (i.getId() == id) return i;
        }
        return null;
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace(AppConstants.CSV_DELIMITER, " ");
    }
}
