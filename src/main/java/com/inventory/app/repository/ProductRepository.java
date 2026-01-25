
package com.inventory.app.repository;

import com.inventory.app.model.Product;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.util.AppConstants;
import com.inventory.app.model.ProductType;
import com.inventory.app.util.ErrorLog;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {
    private static Map<Integer, Product> products = new HashMap<>();


    public void add(Product product) {
        products.put(product.getId(), product);
    }

    public void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_FILE))) {
            bw.write("ID,ProductTypeName,SubType,ProductLineID");
            bw.newLine();
            for (Map.Entry<Integer, Product> entry : products.entrySet()) {
                bw.write(getCsvLine(entry.getValue()));
                bw.newLine();
            }
        } catch (IOException e) {
            ErrorLog.log(LogLevel.ERROR," IO ","IOException",e);
        }
    }

    public void load() {
        products = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AppConstants.PRODUCT_FILE))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                try {
                    Product p = getProductObjectFromLine(line);
                    products.put(p.getId(), p);
                } catch (IllegalArgumentException e) {
                    ErrorLog.log(LogLevel.ERROR," load ","IllegalArgumentException",e);
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Product getProductObjectFromLine(String line) {
        String[] tokens = line.split(AppConstants.CSV_DELIMITER);
        int id = Integer.parseInt(tokens[0]);
        String productTypeName = tokens[1];
        String subtype = tokens[2];
        int productLineId = Integer.parseInt(tokens[3]);
        ProductType productType = new ProductTypeRepository().findByFullName(productTypeName,subtype);

        if (productType == null) {
            throw new IllegalArgumentException(
                    "ProductType not found for  : " + productTypeName+subtype
            );
        }

        return new Product(id, productType, productLineId);
    }


    private String getCsvLine(Product product) {
        StringBuilder line = new StringBuilder(product.getId() + AppConstants.CSV_DELIMITER + product.getProductType().getName()+AppConstants.CSV_DELIMITER+product.getProductType().getSubtype() + AppConstants.CSV_DELIMITER + product.getProductLineId());
        return line.toString();
    }

    public void deleteByID(int id) {
        products.remove(id);
    }

    public Product findById(int id) {
        return products.get(id);
    }

    public boolean isExist(int id) {
        if (findById(id) != null)
            return true;

        return false;
    }

    public Map<Integer, Product> getProducts() {
        return products;
    }
}
