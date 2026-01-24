package com.inventory.app.repository;

import com.inventory.app.model.Category;
import com.inventory.app.util.AppConstants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CategoryRepository {

    private void ensureDataDir() {
        File dir = new File(AppConstants.DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    public List<Category> loadAll() {
        ensureDataDir();

        File file = new File(AppConstants.CATEGORIES_FILE);
        List<Category> categories = new ArrayList<>();

        if (!file.exists()) return categories;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("id" + AppConstants.CSV_DELIMITER)) continue;

                String[] parts = line.split(AppConstants.CSV_DELIMITER, -1);
                if (parts.length < 2) continue;

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();

                categories.add(new Category(id, name));
            }

        } catch (Exception e) {
            System.err.println("Error reading categories file: " + e.getMessage());
        }

        return categories;
    }

    public Map<Integer, Category> loadById() {
        Map<Integer, Category> map = new HashMap<>();
        for (Category c : loadAll()) {
            map.put(c.getId(), c);
        }
        return map;
    }

    public void saveAll(List<Category> categories) {
        ensureDataDir();

        File file = new File(AppConstants.CATEGORIES_FILE);

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            bw.write("id,name");
            bw.newLine();

            for (Category c : categories) {
                bw.write(c.getId() + AppConstants.CSV_DELIMITER + safe(c.getName()));
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error saving categories file: " + e.getMessage());
        }
    }

    public int nextId() {
        int maxId = 0;
        for (Category c : loadAll()) {
            if (c.getId() > maxId) maxId = c.getId();
        }
        return maxId + 1;
    }

    public Category findById(int id) {
        for (Category c : loadAll()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public Category findByNameIgnoreCase(String name) {
        if (name == null) return null;
        String target = name.trim();
        if (target.isEmpty()) return null;

        for (Category c : loadAll()) {
            if (c.getName().equalsIgnoreCase(target)) return c;
        }
        return null;
    }
    public Category addNew(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Category name cannot be empty");

        List<Category> all = loadAll();

        for (Category c : all) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                return c;
            }
        }

        Category created = new Category(nextIdFromList(all), name.trim());
        all.add(created);
        saveAll(all);
        return created;
    }

    public void updateName(int id, String newName) {
        if (newName == null || newName.trim().isEmpty())
            throw new IllegalArgumentException("New name cannot be empty");

        List<Category> all = loadAll();

        for (Category c : all) {
            if (c.getName().equalsIgnoreCase(newName.trim()) && c.getId() != id) {
                throw new IllegalArgumentException("Category name already exists: " + newName);
            }
        }

        for (Category c : all) {
            if (c.getId() == id) {
                c.setName(newName.trim());
                saveAll(all);
                return;
            }
        }

        throw new NoSuchElementException("Category with id " + id + " not found.");
    }

    public boolean deleteById(int id) {
        List<Category> all = loadAll();
        boolean removed = all.removeIf(c -> c.getId() == id);
        if (removed) saveAll(all);
        return removed;
    }

    private int nextIdFromList(List<Category> all) {
        int maxId = 0;
        for (Category c : all) {
            if (c.getId() > maxId) maxId = c.getId();
        }
        return maxId + 1;
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace(AppConstants.CSV_DELIMITER, " ");
    }
}
