package com.vishnu.foodaggregator.validation;

import lombok.AllArgsConstructor;

import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_QUANTITY;

@AllArgsConstructor
public class QuantityValidation extends ValidationRule {

    private Integer quantity;

    @Override
    public boolean validate() {
        if (quantity < 1) {
            setErrorMessage(ITEM_REQUEST_INVALID_QUANTITY);
            return false;
        }
        return true;
    }
}
