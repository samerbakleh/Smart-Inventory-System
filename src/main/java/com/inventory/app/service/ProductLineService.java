package com.inventory.app.service;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.repository.ProductLineRepository;
import com.inventory.app.repository.ProductTypeRepository;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.util.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductLineService {

    private ProductLineRepository productLineRepository;

    public ProductLineService() {
        productLineRepository = new ProductLineRepository();
    }

    public ProductLineRepository getProductLineRepository() {
        return productLineRepository;
    }

    public void setProductLineRepository(ProductLineRepository productLineRepository) {
        this.productLineRepository = productLineRepository;
    }

    public void saveToRepository() throws IOException {
        productLineRepository.save();
    }

    public Map<ProductLine, List<String>[]> loadFromRepository() throws IOException {
        return productLineRepository.load();
    }

    public void linkToProductLine(Map<ProductLine, List<String>[]> unlinkedProductLines, TaskRepository taskRepository,
                                       ProductTypeRepository productTypeRepository) throws IOException {
        for(Map.Entry<ProductLine, List<String>[]> entry : unlinkedProductLines.entrySet()) {
            ProductLine pl = entry.getKey();
            List<String> productTypeNames = entry.getValue()[0];
            String name = productTypeNames.getFirst();
            List<ProductType> productTypes = new ArrayList<>();
            for(int i = 1; i < productTypeNames.size(); i++) {
                ProductType pt = productTypeRepository.findByFullName(name, productTypeNames.get(i));
                if(pt == null)
                    throw new IOException("Some files content is corrupted");
                productTypes.add(pt);
                pt.addProductLine(pl);
            }
            pl.setProductTypes(productTypes);
            List<String> taskIds = entry.getValue()[1];
            List<Task> tasks = new ArrayList<>();
            for(String idString : taskIds) {
                Task t = taskRepository.findById(Integer.parseInt(idString));
                if(t == null)
                    throw new IOException("Some files content is corrupted");
                tasks.add(t);
            }
            pl.setTasks(tasks);
        }
    }

    public void loadAndLink(TaskRepository taskRepository,
                            ProductTypeRepository productTypeRepository) throws IOException {
        linkToProductLine(loadFromRepository(), taskRepository, productTypeRepository);
    }

}
