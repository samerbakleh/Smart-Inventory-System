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

    private static List<ProductType> productTypes;

    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public void add(ProductType pt) {
        productTypes.add(pt);
    }

                                             // name;subType;itemID1,quantity1,itemID2,quantity2,...
    public void save() throws IOException {
        BufferedWriter bfWriter = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_TYPES_FILE));
        for(ProductType pt : productTypes) {
            bfWriter.write(pt.getName() + ";" + pt.getSubtype() + ";");
            for(Map.Entry<Item, Double> entry : pt.getRequiredItems().entrySet()) {
                bfWriter.write(entry.getKey().getId() + "," + entry.getValue() + ",");
            }
            bfWriter.newLine();
        }
    }

                                                // returns a map with product types as keys and the corresponding values
                                                // are required items by ID to link with the product type
    public Map<ProductType, Map<Integer, Double>> load() throws IOException {
        productTypes = new ArrayList<>();
        Map<ProductType, Map<Integer, Double>> unlinkedProductTypes = new HashMap<>();
        try(BufferedReader bfReader = new BufferedReader(new FileReader(AppConstants.PRODUCT_TYPES_FILE))) {
            for(String line : bfReader.readAllLines()) {
                if(line.isEmpty()) {
                    continue;
                }
                String[] mainParts = line.split(";");
                if(mainParts.length < 3 || !mainParts[0].matches("[a-zA-Z0-9-]+")
                                        || !mainParts[1].matches("[a-zA-Z0-9.-]+")) {
                    throw new IOException(AppConstants.PRODUCT_TYPES_FILE + " content is corrupted");
                }
                ProductType pt = new ProductType(mainParts[0], mainParts[1]);
                productTypes.add(pt);
                Map<Integer, Double> requiredItemsByID = new HashMap<>();
                String[] requiredItemsParts = mainParts[1].split(",");
                for(int i = 0; i < requiredItemsParts.length; i += 2) {
                    try {
                        Integer id = Integer.valueOf(requiredItemsParts[i]);
                        Double quantity = Double.valueOf(requiredItemsParts[i + 1]);
                        requiredItemsByID.put(id, quantity);
                    } catch(NumberFormatException | IndexOutOfBoundsException e) {
                        throw new IOException(AppConstants.PRODUCT_TYPES_FILE + " content is corrupted", e);
                    }
                }
                unlinkedProductTypes.put(pt, requiredItemsByID);
            }
        }
        return unlinkedProductTypes;
    }

    public List<ProductType> findByName(String name) {
        List<ProductType> result = new ArrayList<>();
        for(ProductType pt : productTypes) {
            if (pt.getName().equals(name)) {
                result.add(pt);
            }
        }
        return result;
    }

    public ProductType findByFullName(String fullName) {
        for(ProductType pt : productTypes) {
            if (pt.toString().equals(fullName)) {
                return pt;
            }
        }
        return null;
    }

    public ProductType findByFullName(String name, String subType) {
        String fullName = name + " " + subType;
        return findByFullName(fullName);
    }

}