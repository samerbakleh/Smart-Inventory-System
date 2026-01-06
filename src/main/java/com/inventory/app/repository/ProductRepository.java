
package com.inventory.app.repository;

import com.inventory.app.model.Product;
import com.inventory.app.util.AppConstants;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static List<Product> products;


    public List<Product> getProducts() {
        return products;
    }

    public void add(Product product) {
        products.add(product);
    }

    public void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_FILE))) {
            bw.write("productID,productTypeName,Subtype,productLines");
            bw.newLine();
            for (Product product : products) {
                bw.write(getCsvLine(product));
                bw.newLine();
            }
        } catch (IOException e) {

        }
    }

    public void load() {
        products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AppConstants.PRODUCT_FILE))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                products.add(getProductObjectFromLine(line));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

private Product getProductObjectFromLine(String line)
{
    String[] tokens = line.split(",");
    ProductType productType = new ProductTypeRepository().findByFullName(tokens[1],tokens[2]);
    return new Product(Integer.parseInt(tokens[0]),productType);
}

    private String getCsvLine(Product product) {
        StringBuilder line = new StringBuilder(product.getId() + AppConstants.CSV_DELIMITER + product.getProductType().getName()
                + AppConstants.CSV_DELIMITER + product.getProductType().getSubtype() + AppConstants.CSV_DELIMITER);
        for (ProductLine pl : product.getProductType().getProductLines()) {
            line.append(pl.getID()).append(";");
        }
        String s=line.toString();
        return s.substring(0, s.length() - 1);
    }

public void deleteByID(int id)
{
    for(Product product :products)
    {
        if(product.getId()==id)
            products.remove(product);
    }
}
}
