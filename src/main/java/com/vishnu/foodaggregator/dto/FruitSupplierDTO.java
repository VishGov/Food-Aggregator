package com.vishnu.foodaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitSupplierDTO extends SupplierDTO {
    private String id;
    private String name;
}
