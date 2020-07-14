package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;

import java.util.List;
import java.util.Map;

public interface AggregatorService {
    ItemResponse getByName(String itemName, boolean makeAsyncCall) throws ItemNotFoundException;

    ItemResponse getByNameQuantity(String itemName, Integer quantity) throws ItemNotFoundException;

    ItemResponse getByNameQuantityPrice(String itemName, Integer quantity, String price) throws ItemNotFoundException;

    List<ItemResponse> getAllItemsSync();

    List<ItemResponse> getAllItemsAsync();

    Map<String, List<ItemResponse>> getSummary();
}
