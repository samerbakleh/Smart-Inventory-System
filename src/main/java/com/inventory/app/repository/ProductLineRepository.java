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
                                 //id;name;status;productTypeFullName1,taskId1,taskId2|productTypeFullName2,taskId1| ...
    public void save() throws IOException {
        BufferedWriter bfWriter = new BufferedWriter(new FileWriter(AppConstants.PRODUCT_LINES_FILE));
        for(ProductLine pl : productLines) {
            bfWriter.write(pl.getId() + ";" + pl.getName() + ";" + pl.getStatus() + ";");
            for(Map.Entry<ProductType, List<Task>> entry : pl.getTasks().entrySet()) {
                bfWriter.write(entry.getKey() + ",");
                for(Task t : entry.getValue())
                    bfWriter.write(t.getId() + ",");
                bfWriter.write('|');
            }
            bfWriter.newLine();
        }
    }

    public Map<ProductLine, Map<String, List<Integer>>> load() throws IOException {
        productLines.clear();
        Map<ProductLine, Map<String, List<Integer>>> unlinkedProductLines = new HashMap<>();
        BufferedReader bfReader = new BufferedReader(new FileReader(AppConstants.PRODUCT_LINES_FILE));
        for(String line = bfReader.readLine(); line != null; line = bfReader.readLine()) {
            ProductLine pl;
            String[] mainParts = line.split(";");
            if (mainParts.length < 4 || !mainParts[1].matches("[a-zA-Z0-9-]+"))
                throw new IOException(AppConstants.PRODUCT_LINES_FILE + " content is corrupted");
            try {
                int id = Integer.parseInt(mainParts[0]);
                ProductLineStatus status = ProductLineStatus.valueOf(mainParts[2]);
                pl = new ProductLine(id, mainParts[1], status);
            } catch(IllegalArgumentException e) {
                throw new IOException(AppConstants.PRODUCT_LINES_FILE + " content is corrupted");
            }
            Map<String, Integer> taskMap = new HashMap<>();
            String[] taskMapParts = mainParts[3].split("[|]");
            if(!taskMapParts[0].matches("[a-zA-Z0-9-. ]+"))
                throw new IOException(AppConstants.PRODUCT_LINES_FILE + " content is corrupted ");
            for(String taskMapWord : taskMapParts) {

            }
        }
    }

}