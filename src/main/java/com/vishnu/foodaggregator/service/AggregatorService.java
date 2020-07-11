package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;

import java.util.List;

public interface AggregatorService {
    ItemResponse getByName(String itemName) throws ItemNotFoundException;

    List<ItemResponse> getAllItemsSync();

    List<FruitSupplierDTO> getFruitItems();

    List<VegSupplierDTO> getVegetableItems();

    List<GrainSupplierDTO> getGrainItems();
}
