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
import org.slf4j.MDC;
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
    public ItemResponse getByName(String itemName, boolean makeAsyncCall) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = makeAsyncCall ? getAllItemsAsync() : getAllItemsSync();

        return itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemName)));
    }

    @Override
    public ItemResponse getByNameQuantity(String itemName, Integer quantity) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = getAllItemsSync();

        return itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .filter(item -> quantity <= item.getQuantity())
                .findFirst()
                .map(item -> {
                    item.setQuantity(quantity);
                    return item;
                })
                .orElseThrow(() -> {
                            //todo improve logging!
                            log.info("ITEM NOT FOUND!");
                            return new ItemNotFoundException(String.format(ITEM_NOT_FOUND_FOR_QTY_MESSAGE, itemName, quantity));
                        }
                );
    }

    @Override
    public ItemResponse getByNameQuantityPrice(String itemName, Integer quantity, String price) throws ItemNotFoundException {

        Optional<List<ItemResponse>> itemResponseList = itemsCache.getItemResponses(itemName)
                .or(() -> checkWithSuppliers(itemName));

        return itemResponseList.stream()
                .flatMap(Collection::stream)
                .filter(item -> quantity <= item.getQuantity())
                .filter(item -> checkPrice(item.getPrice(), price))
                .findFirst()
                .map(item -> item.toBuilder().build())
                .map(item -> {
                    item.setQuantity(quantity);
                    itemsCache.putItem(itemName, item);
                    return item;
                })
                .orElseThrow(() ->
                        new ItemNotFoundException(String.format(ITEM_NOT_FOUND_FOR_QTY_PRICE_MESSAGE, itemName, quantity, price)));
    }

    @Override
    public List<ItemResponse> getAllItemsSync() {
        List<ItemResponse> vegItems = getItemsFromVegSupplier();
        List<ItemResponse> fruitItems = getItemsFromFruitSupplier();
        List<ItemResponse> grainItems = getItemsFromGrainSupplier();

        return Stream.of(fruitItems, vegItems, grainItems).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> getAllItemsAsync() {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        CompletableFuture<List<ItemResponse>> fruitItems = CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(copyOfContextMap);
            return getItemsFromFruitSupplier();
        });
        CompletableFuture<List<ItemResponse>> vegItems = CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(copyOfContextMap);
            return getItemsFromVegSupplier();
        });
        CompletableFuture<List<ItemResponse>> grainItems = CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(copyOfContextMap);
            return getItemsFromGrainSupplier();
        });

        List<CompletableFuture<List<ItemResponse>>> apiCallsList = List.of(fruitItems, vegItems, grainItems);

        CompletableFuture<List<ItemResponse>> itemResponseListCF = CompletableFuture.allOf(apiCallsList.toArray(new CompletableFuture[0]))
                .thenApply(v -> apiCallsList
                        .stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));

        return itemResponseListCF.join();

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
        List<ItemResponse> itemResponseList = getAllItemsSync();
        itemsCache.updateCache(itemResponseList);

        return Optional.of(itemResponseList
                .stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .collect(Collectors.toList()));
    }

    private boolean checkPrice(String itemPriceString, String requestPriceString) {

        try {
            BigDecimal requestPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(requestPriceString)));
            BigDecimal itemPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(itemPriceString)));
            return requestPrice.compareTo(itemPrice) >= 0;
        } catch (ParseException e) {
            //todo add logger?
            System.out.println(e.getMessage());
            throw new RuntimeException("Parsing of price failed");
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
