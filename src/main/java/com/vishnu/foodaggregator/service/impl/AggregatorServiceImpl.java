package com.vishnu.foodaggregator.service.impl;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import com.vishnu.foodaggregator.service.AggregatorService;
import com.vishnu.foodaggregator.service.SupplierService;
import com.vishnu.foodaggregator.util.ItemsCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vishnu.foodaggregator.constants.Constants.*;

@Slf4j
@AllArgsConstructor
@Service
public class AggregatorServiceImpl implements AggregatorService {

    private ItemsCache itemsCache;
    private SupplierService supplierService;

    @Override
    public List<ItemResponse> getByName(String itemName, boolean makeAsyncCall) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = makeAsyncCall ? getAllItemsAsync() : getAllItemsSync();

        log.info("Filtering items by name");
        List<ItemResponse> filteredItemResponses = itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .collect(Collectors.toList());

        if (filteredItemResponses.isEmpty())
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemName));

        log.info("Item : '{}' available. ItemResponse = {}", itemName, filteredItemResponses);
        return filteredItemResponses;
    }

    @Override
    public List<ItemResponse> getByNameQuantity(String itemName, Integer quantity) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = getAllItemsSync();

        log.info("Filtering items by name and quantity");
        List<ItemResponse> filteredItemResponses = itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .filter(item -> quantity <= item.getQuantity())
                .map(item -> {
                    item = item.toBuilder().build();
                    item.setQuantity(quantity);
                    return item;
                })
                .collect(Collectors.toList());

        if (filteredItemResponses.isEmpty())
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_FOR_QTY_MESSAGE, itemName, quantity));

        log.info("Item : '{}' available. ItemResponse = {}", itemName, filteredItemResponses);
        return filteredItemResponses;
    }

    @Override
    public List<ItemResponse> getByNameQuantityPrice(String itemName, Integer quantity, String price) throws ItemNotFoundException {

        Optional<List<ItemResponse>> itemResponsesFromCacheOrSuppliers = itemsCache.getItemResponses(itemName)
                .map(cachedItemResponses -> {
                    log.info("Item : '{}' is present in cache. Corresponding item responses : ITEM_RESPONSES = {}",itemName, cachedItemResponses);
                    return cachedItemResponses;
                })
                .or(() -> checkWithSuppliers(itemName));

        List<ItemResponse> filteredItemResponses = itemResponsesFromCacheOrSuppliers.stream()
                .flatMap(Collection::stream)
                .map(item -> item.toBuilder().build())
                .filter(item -> quantity <= item.getQuantity())
                .filter(item -> checkPrice(item.getPrice(), price))
                .peek(item -> item.setQuantity(quantity)).collect(Collectors.toList());


        itemsCache.updateCache(filteredItemResponses, true);

        if (filteredItemResponses.isEmpty())
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_FOR_QTY_PRICE_MESSAGE, itemName, quantity, price));

        log.info("Item : '{}' available. ItemResponse = {}", itemName, filteredItemResponses);
        return filteredItemResponses;
    }

    @Override
    public List<ItemResponse> getAllItemsSync() {
        log.info("Calling suppliers to get items synchronously");
        List<ItemResponse> vegItems = getItemsFromVegSupplier();
        List<ItemResponse> fruitItems = getItemsFromFruitSupplier();
        List<ItemResponse> grainItems = getItemsFromGrainSupplier();

        List<ItemResponse> aggregatedItems = Stream.of(fruitItems, vegItems, grainItems).flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Aggregated items from sync call to suppliers. Result = {}", aggregatedItems);

        return aggregatedItems;
    }

    @Override
    public List<ItemResponse> getAllItemsAsync() {

        CompletableFuture<List<ItemResponse>> fruitItems = CompletableFuture.supplyAsync(this::getItemsFromFruitSupplier);
        CompletableFuture<List<ItemResponse>> vegItems = CompletableFuture.supplyAsync(this::getItemsFromVegSupplier);
        CompletableFuture<List<ItemResponse>> grainItems = CompletableFuture.supplyAsync(this::getItemsFromGrainSupplier);

        List<CompletableFuture<List<ItemResponse>>> apiCallsList = List.of(fruitItems, vegItems, grainItems);

        log.info("Calling suppliers to get items asynchronously");
        CompletableFuture<List<ItemResponse>> itemResponseListCF = CompletableFuture.allOf(apiCallsList.toArray(new CompletableFuture[0]))
                .thenApply(v -> apiCallsList
                        .stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));

        List<ItemResponse> aggregatedItems = itemResponseListCF.join();
        log.info("Aggregated items from async call to suppliers. Result = {}", aggregatedItems);

        return aggregatedItems;
    }

    @Override
    public Map<String, List<ItemResponse>> getSummary() {
        return itemsCache.getItemsMap();
    }

    private List<ItemResponse> getItemsFromGrainSupplier() {
        return supplierService.getGrainItems()
                .stream()
                .map(this::mapSupplierDTOToItemResponse)
                .collect(Collectors.toList());
    }

    private List<ItemResponse> getItemsFromFruitSupplier() {
        return supplierService.getFruitItems()
                .stream()
                .map(this::mapSupplierDTOToItemResponse)
                .collect(Collectors.toList());
    }

    private List<ItemResponse> getItemsFromVegSupplier() {
        return supplierService.getVegetableItems()
                .stream()
                .map(this::mapSupplierDTOToItemResponse)
                .collect(Collectors.toList());
    }

    private Optional<List<ItemResponse>> checkWithSuppliers(String itemName) {
        log.info("Item : '{}' not present in cache. Checking with suppliers", itemName);

        List<ItemResponse> itemResponseList = getAllItemsSync();
        List<ItemResponse> filteredItemResponseByName = itemResponseList
                .stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .collect(Collectors.toList());

        itemsCache.updateCache(itemResponseList, false);

        return Optional.of(filteredItemResponseByName);
    }

    private boolean checkPrice(String itemPriceString, String requestPriceString) {

        try {
            BigDecimal requestPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(requestPriceString)));
            BigDecimal itemPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(itemPriceString)));
            return requestPrice.compareTo(itemPrice) >= 0;
        } catch (ParseException e) {
            log.error("Parsing of price failed, ERROR = {}", e.getLocalizedMessage());
            throw new IllegalStateException("Parsing of price failed", e);
        }

    }

    private ItemResponse mapSupplierDTOToItemResponse(FruitSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    private ItemResponse mapSupplierDTOToItemResponse(VegSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getProductId())
                .name(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    private ItemResponse mapSupplierDTOToItemResponse(GrainSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getItemId())
                .name(item.getItemName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
