package com.vishnu.foodaggregator.util;

import com.vishnu.foodaggregator.response.ItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ItemsCache {
    private final Map<String, List<ItemResponse>> itemsMap;

    public ItemsCache() {
        this.itemsMap = new ConcurrentHashMap<>();
    }

    public Optional<List<ItemResponse>> getItemResponses(String itemName) {
        return Optional.ofNullable(itemsMap.get(itemName));
    }

    public void putItem(String itemName, ItemResponse itemResponse, boolean deductQuantity) {
        log.info("Putting item in cache. ITEM_NAME = {} , ITEM_RESPONSE = {}", itemName, itemResponse);

        List<ItemResponse> cachedItemResponses = itemsMap.computeIfAbsent(itemName, v -> new ArrayList<>());
        boolean cacheListModified = false;
        for (int i = 0; i < cachedItemResponses.size(); i++) {
            ItemResponse cachedItemResponse = cachedItemResponses.get(i);
            if (cachedItemResponse.equals(itemResponse)) {
                int itemResponseQuantity = deductQuantity ? -(itemResponse.getQuantity()) : itemResponse.getQuantity();
                cachedItemResponse.setQuantity(cachedItemResponse.getQuantity() + itemResponseQuantity);
                cachedItemResponse.setPrice(itemResponse.getPrice());
                cachedItemResponses.set(i, cachedItemResponse);
                cacheListModified = true;
                break;
            }
        }

        cachedItemResponses.removeIf(item -> item.getQuantity() == 0);

        if (!cacheListModified)
            cachedItemResponses.add(itemResponse);

        if (!cachedItemResponses.isEmpty())
            itemsMap.put(itemName, cachedItemResponses);
        else
            itemsMap.remove(itemName);
    }

    public void updateCache(List<ItemResponse> itemResponseList, boolean deductQuantity) {
        log.info("Items received for cache update, ITEMS = {} , DEDUCT_QUANTITY = {}", itemResponseList, deductQuantity);
        itemResponseList.forEach(itemResponse -> putItem(itemResponse.getName().toLowerCase(), itemResponse, deductQuantity));
    }

    public Map<String, List<ItemResponse>> getItemsMap() {
        return itemsMap;
    }

    public Integer size() {
        return itemsMap.size();
    }
}
