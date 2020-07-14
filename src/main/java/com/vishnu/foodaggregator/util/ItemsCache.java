package com.vishnu.foodaggregator.util;

import com.vishnu.foodaggregator.response.ItemResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ItemsCache {
    private Map<String, List<ItemResponse>> itemsMap;

    public ItemsCache() {
        this.itemsMap = new ConcurrentHashMap<>();
    }

    public Optional<List<ItemResponse>> getItemResponses(String itemName) {
        return Optional.ofNullable(itemsMap.get(itemName));
    }

    public void putItem(String itemName, ItemResponse itemResponse) {
        List<ItemResponse> cachedItemResponses = itemsMap.computeIfAbsent(itemName, v -> new ArrayList<>());
        boolean cacheListModified = false;
        for (int i = 0; i < cachedItemResponses.size(); i++) {
            ItemResponse cachedItemResponse = cachedItemResponses.get(i);
            if (cachedItemResponse.equals(itemResponse)) {
                if (itemResponse.getQuantity() <= cachedItemResponse.getQuantity())
                    cachedItemResponse.setQuantity(cachedItemResponse.getQuantity() - itemResponse.getQuantity());
                else
                    cachedItemResponse = itemResponse;

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

    public void updateCache(List<ItemResponse> itemResponseList) {
        itemResponseList.stream().forEach(itemResponse -> putItem(itemResponse.getName().toLowerCase(), itemResponse));
    }

    public Map<String, List<ItemResponse>> getItemsMap() {
        return itemsMap;
    }
}
