package com.inventory.app.controller;

import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.model.enums.Unit;
import com.inventory.app.service.InventoryService;
import com.inventory.app.util.ErrorLog;

import java.util.List;
import java.util.NoSuchElementException;

public class ItemController {

    private final InventoryService service;

    public ItemController(InventoryService service) {
        this.service = service;
    }

    public List<Item> getAllItems() {
        return service.getAllItems();
    }

    public List<Category> getAllCategories() {
        return service.getAllCategories();
    }

    public String addItem(String name, String priceStr, String qtyStr, String minStr, Unit unit, int categoryId) {
        try {if (unit == null) unit = Unit.PCS;
            double price = Double.parseDouble(priceStr.trim());
            double qty = Double.parseDouble(qtyStr.trim());
            double min = Double.parseDouble(minStr.trim());

            service.addItem(name, price, qty, min, unit, categoryId);
            return null;
        } catch (NumberFormatException e) {
            return "Please enter valid numbers for price / quantity / minLimit.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unexpected error while adding item: " + e.getMessage();
        }
    }
    public String addItem(String name, String priceStr, String qtyStr, String minStr, int categoryId) {
        return addItem(name, priceStr, qtyStr, minStr, Unit.PCS, categoryId);
    }

    public String updateItem(int id, String name, String priceStr, String qtyStr, String minStr, Unit unit, int categoryId) {
        try {if (unit == null) unit = Unit.PCS;

            double price = Double.parseDouble(priceStr.trim());
            double qty = Double.parseDouble(qtyStr.trim());
            double min = Double.parseDouble(minStr.trim());

            service.updateItem(id, name, price, qty, min, unit, categoryId);
            return null;
        } catch (NumberFormatException e) {
            return "Please enter valid numbers for price / quantity / minLimit.";
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unexpected error while updating item: " + e.getMessage();
        }
    }

    public String updateItem(int id, String name, String priceStr, String qtyStr, String minStr, int categoryId) {
        return updateItem(id, name, priceStr, qtyStr, minStr, Unit.PCS, categoryId);
    }
    public String addItem(String name, String priceStr, String qtyStr, String minStr, String unitStr, int categoryId) {
        Unit unit = Unit.parse(unitStr);
        return addItem(name, priceStr, qtyStr, minStr, unit, categoryId);
    }

    public String updateItem(int id, String name, String priceStr, String qtyStr, String minStr, String unitStr, int categoryId) {
        Unit unit = Unit.parse(unitStr);
        return updateItem(id, name, priceStr, qtyStr, minStr, unit, categoryId);
    }

    public String deleteItem(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            boolean removed = service.deleteItem(id);
            return removed ? null : "Item not found.";
        } catch (NumberFormatException e) {
            ErrorLog.log(LogLevel.ERROR,"deleteItem","NumberFormatException",e);
            return "Invalid item id.";

        } catch (Exception e) {
            ErrorLog.log(LogLevel.ERROR,"deleteItem","Unexpected error while deleting item:",e);

            return "Unexpected error while deleting item: " + e.getMessage();
        }
    }

    public List<Item> filterByName(String query) {
        return service.filterByNameContains(query);
    }

    public List<Item> filterByCategory(int categoryId) {
        return service.filterByCategory(categoryId);
    }

    public List<Item> lowStock() {
        return service.filterLowStock();
    }

    public List<Item> outOfStock() {
        return service.filterOutOfStock();
    }

    public String addCategory(String name) {
        try {
            service.addCategory(name);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            ErrorLog.log(LogLevel.ERROR,"deleteItem","Unexpected error while adding category: " + e.getMessage(),e);
            return "Unexpected error while adding category: " + e.getMessage();
        }
    }

    public void save() {
        service.save();
    }

    public void reload() {
        service.reload();
    }
}
