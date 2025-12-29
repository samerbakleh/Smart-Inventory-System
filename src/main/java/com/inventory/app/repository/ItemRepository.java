package com.inventory.app.repository;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.util.AppConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {


  /*  public List<Item> loadAll() {
        List<Item> items = new ArrayList<>();
        File file = new File(AppConstants.ITEMS_FILE);

        if (!file.exists()) return items;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(AppConstants.CSV_DELIMITER);
                if (parts.length >= 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        double price = Double.parseDouble(parts[2]);
                        int qty = Integer.parseInt(parts[3]);
                        int minLimit = Integer.parseInt(parts[4]);
                        Category category = new Category(0, parts[5]);
                        items.add(new Item(id, name, price, qty, minLimit, category));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading items file: " + e.getMessage());
        }
        return items;
    }

    public void saveAll(List<Item> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AppConstants.ITEMS_FILE))) {
            for (Item item : items) {
                String line = String.format("%d,%s,%.2f,%d,%d,%s",
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getMinLimit(),
                        item.getCategory().getName()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving items file: " + e.getMessage());
        }
    }

    public void update(Item updatedItem) {
        List<Item> allItems = loadAll();
        boolean found = false;

        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getId() == updatedItem.getId()) {
                allItems.set(i, updatedItem);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(allItems);
        } else {
            System.err.println("Item with ID " + updatedItem.getId() + " not found for update.");
        }
    }
*/
}
