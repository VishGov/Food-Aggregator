package com.vishnu.foodaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrainSupplierDTO {
    private String itemId;
    private String itemName;
    private Integer quantity;
    private String price;
}
