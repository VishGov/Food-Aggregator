package com.vishnu.foodaggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrainSupplierDTO extends SupplierDTO {
    private String itemId;
    private String itemName;
}
