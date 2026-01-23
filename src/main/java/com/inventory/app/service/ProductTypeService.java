package com.inventory.app.service;

import com.inventory.app.model.Item;
import com.inventory.app.model.ProductType;
import com.inventory.app.repository.ItemRepository;
import com.inventory.app.repository.ProductTypeRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductTypeService {

    private ProductTypeRepository productTypeRepository;

    public ProductTypeService() {
        productTypeRepository = new ProductTypeRepository();
    }

    public ProductTypeRepository getProductTypeRepository() {
        return productTypeRepository;
    }

    public void setProductTypeRepository(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public void saveToRepository() throws IOException {
        productTypeRepository.save();
    }

   /* public Map<ProductType, Map<Integer, Double>> loadFromRepository() throws IOException {
        return productTypeRepository.load();
    }*/

   /* public void linkItemToProductType(Map<ProductType, Map<Integer, Double>> unlinkedProductTypes,
                                      ItemRepository itemRepository) throws IOException {
        for(Map.Entry<ProductType, Map<Integer, Double>> entry : unlinkedProductTypes.entrySet()) {
            Map<Item, Double> requiredItems = new HashMap<>();
            for(Map.Entry<Integer, Double> reqItem : entry.getValue().entrySet()) {
                Item item = itemRepository.findById(reqItem.getKey());
                if(item == null)
                    throw new IOException("Some of the files content is corrupted");
                requiredItems.put(item, reqItem.getValue());
            }
            entry.getKey().setRequiredItems(requiredItems);
        }
    }

    public void loadAndLink(ItemRepository itemRepository) throws IOException {
        linkItemToProductType(loadFromRepository(), itemRepository);
    }*/

}