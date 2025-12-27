package com.inventory.app.repository;

import com.inventory.app.model.Item;
import com.inventory.app.model.ProductType;
import com.inventory.app.util.AppConstants;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class ProductTypeRepository {

    private List<ProductType> productTypes;

    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public void add(ProductType pt) {
        productTypes.add(pt);
    }
                                             // name;itemId1,quantity1,itemId2,quantity2,...
    public void save() throws IOException {
        try(BufferedWriter bfWriter = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_TYPES_FILE))) {
            for(ProductType pt : productTypes) {
                bfWriter.write(pt.getName() + ";");
                for(Map.Entry<Item, Double> entry : pt.getRequiredItems().entrySet())
                    bfWriter.write(entry.getKey().getId() + "," + entry.getValue() + ",");
                bfWriter.newLine();
            }
        }
    }
                                                // returns a map with product types as keys and the corresponding values
                                                // are required items by id to link with the product type
    public Map<ProductType, Map<Integer, Double>> load() throws IOException {
        productTypes = new ArrayList<>();
        Map<ProductType, Map<Integer, Double>> unlinkedProductTypes = new HashMap<>();
        try(BufferedReader bfReader = new BufferedReader(new FileReader(AppConstants.PRODUCT_TYPES_FILE))) {
            for(String line = bfReader.readLine(); line != null; line = bfReader.readLine()) {
                String[] mainParts = line.split(";");
                if(mainParts.length != 2 || !mainParts[0].matches("\\w+"))
                    throw new IOException(AppConstants.PRODUCT_TYPES_FILE + " content is corrupted");
                ProductType pt = new ProductType(mainParts[0]);
                productTypes.add(pt);
                String[] requiredItemsParts = mainParts[1].split(",");
                Map<Integer, Double> requiredItemsById = new HashMap<>();
                for(int i = 0; i < requiredItemsParts.length; i += 2) {
                    Integer id = Integer.valueOf(requiredItemsParts[i]);
                    Double quantity = Double.valueOf(requiredItemsParts[i + 1]);
                    requiredItemsById.put(id, quantity);
                }
                unlinkedProductTypes.put(pt, requiredItemsById);
            }
        } catch(NumberFormatException | IndexOutOfBoundsException e) {
            throw new IOException(AppConstants.PRODUCT_TYPES_FILE + " content is corrupted", e);
        }
        return unlinkedProductTypes;
    }

    public ProductType findByName(String name) {
        for(ProductType pt : productTypes)
            if(pt.getName().equals(name))
                return pt;
        return null;
    }

}