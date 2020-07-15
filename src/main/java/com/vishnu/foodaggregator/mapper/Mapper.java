package com.vishnu.foodaggregator.mapper;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.response.ItemResponse;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public ItemResponse mapSupplierDTOToItemResponse(FruitSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    public ItemResponse mapSupplierDTOToItemResponse(VegSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getProductId())
                .name(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    public ItemResponse mapSupplierDTOToItemResponse(GrainSupplierDTO item) {
        return ItemResponse.builder()
                .id(item.getItemId())
                .name(item.getItemName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
