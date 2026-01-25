package com.inventory.app.service;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.ProductLineStatus;
import com.inventory.app.repository.ProductLineRepository;
import com.inventory.app.repository.ProductTypeRepository;
import com.inventory.app.repository.TaskRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductLineService {

    private ProductLineRepository productLineRepository;
    private TaskRepository taskRepository;
    private ProductTypeRepository productTypeRepository;

    public ProductLineService(ProductLineRepository productLineRepository, TaskRepository taskRepository,
                              ProductTypeRepository productTypeRepository) {
        this.productLineRepository = productLineRepository;
        this.taskRepository = taskRepository;
        this.productTypeRepository = productTypeRepository;
    }

    public void saveToRepository() throws IOException {
        productLineRepository.save();
    }

    public Map<ProductLine, List<String>[]> loadFromRepository() throws IOException {
        return productLineRepository.load();
    }

    public void linkToProductLine(Map<ProductLine, List<String>[]> unlinkedProductLines) throws IOException {
        for(Map.Entry<ProductLine, List<String>[]> entry : unlinkedProductLines.entrySet()) {
            ProductLine pl = entry.getKey();
            List<String> productTypeNames = entry.getValue()[0];
            String name = productTypeNames.getFirst();
            List<ProductType> productTypes = new ArrayList<>();
            for(int i = 1; i < productTypeNames.size(); i++) {
                ProductType pt = productTypeRepository.findByFullName(name, productTypeNames.get(i));
                if(pt == null) {
                    throw new IOException("Some files content is corrupted");
                }
                productTypes.add(pt);
                pt.addProductLine(pl);
            }
            pl.setProductTypes(productTypes);
            List<String> taskIDs = entry.getValue()[1];
            List<Task> tasks = new ArrayList<>();
            for(String idString : taskIDs) {
                Task t = taskRepository.findById(Integer.parseInt(idString));
                if(t == null) {
                    throw new IOException("Some files content is corrupted");
                }
                tasks.add(t);
            }
            pl.setTasks(tasks);
        }
    }

    public void loadAndLink() throws IOException {
        linkToProductLine(loadFromRepository());
    }

    public List<ProductLine> getLoadedProductLines() {
        return productLineRepository.getProductLines();
    }

    public ProductLine getLoadedProductLines(int id) {
        return productLineRepository.findByID(id);
    }

    public boolean validateProductLineName(String name) {
        return name.matches("[a-zA-Z0-9- ]+");
    }

    public void addNewProductLine(String name, List<String> productTypeNames) {
        List<ProductType> productTypes = new ArrayList<>();
        for(String fullName : productTypeNames) {
            productTypes.add(productTypeRepository.findByFullName(fullName));
        }
        productLineRepository.add(new ProductLine(name, productTypes));
    }

    public List<Float> calculateProductionPercentage() {
        List<ProductLine> productLines = productLineRepository.getProductLines();
        int n = productLines.size();
        List<Float> result = new ArrayList<>();
        int allProduction = 0;
        for(ProductLine pl : productLines) {
            allProduction += pl.getTotalProduction();
        }
        if(allProduction != 0) {
            for(int i = 0; i < n; i++) {
                result.add(100f * productLines.get(i).getTotalProduction() / allProduction);
            }
        } else {
            for(int i = 0; i < n; i++) {
                result.add(0f);
            }
        }
        return result;
    }

    public ProductLine getLeastBusyProductLine(ProductType pt) {
        List<ProductLine> productLines = pt.getProductLines();
        int minWork = Integer.MAX_VALUE;
        ProductLine leastBusyProductLine = null;
        for(ProductLine pl : productLines) {
            int work = 0;
            for(Task t : pl.getTasks(pt)) {
                work += t.getQuantityRequired();
            }
            if(work < minWork) {
                minWork = work;
                leastBusyProductLine = pl;
            }
        }
        return leastBusyProductLine;
    }

    public int getLeastBusyProductLineID(ProductType pt) {
        return getLeastBusyProductLine(pt).getID();
    }

    public int getLeastBusyProductLineID(String productTypeName) {
        ProductType pt = productTypeRepository.findByFullName(productTypeName);
        return getLeastBusyProductLineID(pt);
    }

    public boolean changeProductLineName(ProductLine pl, String name) {
        if(validateProductLineName(name)) {
            pl.setName(name);
            return true;
        }
        return false;
    }

    public void changeProductLineStatus(ProductLine pl, ProductLineStatus plStatus) {
        pl.setStatus(plStatus);
    }

    public void changeProductLineRating(ProductLine pl, int rating) {
        pl.setRating(rating);
    }

    public void changeProductLineNote(ProductLine pl, String note) {
        pl.setNote(note);
    }

}
