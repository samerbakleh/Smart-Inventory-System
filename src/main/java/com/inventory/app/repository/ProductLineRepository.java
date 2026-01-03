package com.inventory.app.repository;

import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.util.AppConstants;
import com.inventory.app.model.ProductLine;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ProductLineRepository {

    private List<ProductLine> productLines = new ArrayList<>();

    public List<ProductLine> getProductLines() {
        return productLines;
    }

    public void add(ProductLine pl) {
        productLines.add(pl);
    }

                                //id;name;status;productTypeName,productsubType1,productsubType2...;taskId1,taskId2,...
    public void save() throws IOException {
        try (BufferedWriter bfWriter = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_LINES_FILE))) {
            for (ProductLine pl : productLines) {
                bfWriter.write(pl.getId() + ";" + pl.getName() + ";" + pl.getStatus() + ";"
                             + pl.getProductTypes().getFirst().getName());
                for (ProductType pt : pl.getTasks().keySet()) {
                    bfWriter.write("," + pt.getSubtype());
                }
                bfWriter.write(';');
                for(List<Task> value : pl.getTasks().values()) {
                    for (Task t : value) {
                        bfWriter.write(t.getId() + ",");
                    }
                }
                bfWriter.newLine();
            }
        }
    }

                                                    // returns a map with product lines as keys and the corresponding
                                                    // values are a list of product type names (name is the first element
                                                    // and the rest are subtypes) and a list of task IDs as a string
    public Map<ProductLine, List<String>[]> load() throws IOException {
        productLines.clear();
        Map<ProductLine, List<String>[]> unlinkedProductLines = new HashMap<>();
        int previousId = -1;
        final String ERROR_MESSAGE = AppConstants.PRODUCT_LINES_FILE + " content is corrupted";
        try(BufferedReader bfReader = new BufferedReader(new FileReader(AppConstants.PRODUCT_LINES_FILE))) {
            for (String line : bfReader.readAllLines()) {
                if(line.isEmpty()) {
                    continue;
                }
                ProductLine pl;
                String[] mainParts = line.split(";");
                if (mainParts.length < 4 || !mainParts[1].matches("[a-zA-Z0-9-]+")) {
                    throw new IOException(ERROR_MESSAGE);
                }
                try {
                    int productLineId = Integer.parseInt(mainParts[0]);
                    ProductLineStatus status = ProductLineStatus.valueOf(mainParts[2]);
                    if (productLineId <= previousId) {
                        throw new IOException(ERROR_MESSAGE);
                    }
                    previousId = productLineId;
                    pl = new ProductLine(productLineId, mainParts[1], status);
                } catch (IllegalArgumentException e) {
                    throw new IOException(ERROR_MESSAGE, e);
                }
                List<String> productTypeNames = List.of(mainParts[3].split(","));
                if(productTypeNames.size() < 2 || !productTypeNames.getFirst().matches("[a-zA-Z0-9- ]+")) {
                    throw new IOException(ERROR_MESSAGE);
                }
                for(String name : productTypeNames) {
                    if (!name.matches("[a-zA-Z0-9-. ]+")) {
                        throw new IOException(ERROR_MESSAGE);
                    }
                }
                List<String> taskIds = List.of(mainParts[4].split(","));
                for(String idString : taskIds) {
                    if (!idString.matches("[0-9]+")) {
                        throw new IOException(ERROR_MESSAGE);
                    }
                }
                List<String>[] result = new List[2];
                result[0] = productTypeNames;
                result[1] = taskIds;
                unlinkedProductLines.put(pl, result);
            }
        }
        return unlinkedProductLines;
    }

    public ProductLine findById(int id) {
        for(ProductLine pl : productLines) {
            if (pl.getId() == id) {
                return pl;
            }
        }
        return null;
    }

}