package com.inventory.app.service;

import com.inventory.app.controller.MainController;
import com.inventory.app.model.Item;
import com.inventory.app.model.ProductType;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.repository.ProductTypeRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductTypeService {

    private final MainController mainController;
    private ProductTypeRepository productTypeRepository;

    public ProductTypeService(MainController mainController) {
        this.mainController = mainController;
        productTypeRepository = new ProductTypeRepository();
    }

    public ProductTypeRepository getProductTypeRepository() {
        return productTypeRepository;
    }

    public void setProductTypeRepository(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public void saveToRepository() {
        try {
            productTypeRepository.save();
        } catch(IOException e) {
            mainController.exceptionHappened(e.getMessage());
        }
    }

    public Map<ProductType, Map<Integer, Double>> loadFromRepository() {
        try {
            return productTypeRepository.load();
        } catch(IOException e) {
            mainController.exceptionHappened(e.getMessage());
            return null;
        }
    }

    public void linkItemToProductType(Map<ProductType, Map<Integer, Double>> unlinkedProductTypes,
                                      ItemRepository itemRepository) {
        for(Map.Entry<ProductType, Map<Integer, Double>> entry : unlinkedProductTypes.entrySet()) {
            Map<Item, Double> requiredItems = new HashMap<>();
            for(Map.Entry<Integer, Double> reqItem : entry.getValue().entrySet()) {
                Item item = itemRepository.findById(reqItem.getKey());
                if(item == null) {
                    mainController.exceptionHappened("Some of the files content is corrupted");
                    return;
                }
                requiredItems.put(item, reqItem.getValue());
            }
            entry.getKey().setRequiredItems(requiredItems);
        }
    }

    public void loadAndLink(ItemRepository itemRepository) {
        linkItemToProductType(loadFromRepository(), itemRepository);
    }

}
