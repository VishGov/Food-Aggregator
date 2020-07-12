package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vishnu.foodaggregator.constants.Constants.*;

@Service
public class AggregatorServiceImpl implements AggregatorService {

    private Environment env;
    private RestTemplate restTemplate;

    public AggregatorServiceImpl(Environment env, RestTemplate restTemplate) {
        this.env = env;
        this.restTemplate = restTemplate;
    }

    @Override
    public ItemResponse getByName(String itemName) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = getAllItemsSync();

        return itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemName)));
    }

    public ItemResponse getByNameQuantity(String itemName, Integer quantity) throws ItemNotFoundException {
        List<ItemResponse> itemResponseList = getAllItemsSync();

        return itemResponseList.stream()
                .filter(item -> itemName.toLowerCase().equals(item.getName().toLowerCase())
                        && quantity <= item.getQuantity())
                .findFirst()
                .map(item -> {
                    item.setQuantity(quantity);
                    return item;
                })
                .orElseThrow(() ->
                        new ItemNotFoundException(String.format(ITEM_NOT_FOUND_FOR_QTY_MESSAGE, itemName, quantity)));
    }

    @Override
    public List<ItemResponse> getAllItemsSync() {
        List<ItemResponse> fruitItems = getFruitItems().stream().map(this::mapSupplierDTOToItemResponse).collect(Collectors.toList());
        List<ItemResponse> vegItems = getVegetableItems().stream().map(this::mapSupplierDTOToItemResponse).collect(Collectors.toList());
        List<ItemResponse> grainItems = getGrainItems().stream().map(this::mapSupplierDTOToItemResponse).collect(Collectors.toList());

        return Stream.of(fruitItems, vegItems, grainItems).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<FruitSupplierDTO> getFruitItems() {
        String fruitSupplierUrl = env.getProperty(SUPPLIER_FRUIT_URL);
        return getItemsFromSupplier(fruitSupplierUrl, FruitSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

    @Override
    public List<VegSupplierDTO> getVegetableItems() {
        String vegSupplierUrl = env.getProperty(SUPPLIER_VEGETABLE_URL);
        return getItemsFromSupplier(vegSupplierUrl, VegSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

    @Override
    public List<GrainSupplierDTO> getGrainItems() {
        String grainSupplierUrl = env.getProperty(SUPPLIER_GRAIN_URL);
        return getItemsFromSupplier(grainSupplierUrl, GrainSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

    private <T> Optional<T[]> getItemsFromSupplier(String supplierUrl, Class<T[]> supplierDTOType) {
        T[] supplierResponse = restTemplate.getForObject(supplierUrl, supplierDTOType);
        return Optional.ofNullable(supplierResponse);
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
