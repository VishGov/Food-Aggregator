package com.vishnu.foodaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitSupplierDTO {
    private String id;
    private String name;
    private Integer quantity;
    private String price;
}
