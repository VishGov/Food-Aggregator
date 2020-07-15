package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;

import java.util.List;
import java.util.Map;

public interface AggregatorService {
    List<ItemResponse> getByName(String itemName, boolean makeAsyncCall) throws ItemNotFoundException;

    List<ItemResponse> getByNameQuantity(String itemName, Integer quantity) throws ItemNotFoundException;

    List<ItemResponse> getByNameQuantityPrice(String itemName, Integer quantity, String price) throws ItemNotFoundException;

    List<ItemResponse> getAllItemsSync();

    List<ItemResponse> getAllItemsAsync();

    Map<String, List<ItemResponse>> getSummary();
}
