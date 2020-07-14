package com.vishnu.foodaggregator.validation;

import lombok.AllArgsConstructor;

import java.util.Objects;

import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_PARAM_NOT_SET;

@AllArgsConstructor
public class PropertySetValidation extends ValidationRule {

    private String propertyName;
    private Object value;

    @Override
    boolean validate() {
        if (Objects.isNull(value)) {
            setErrorMessage(String.format(ITEM_REQUEST_INVALID_PARAM_NOT_SET, propertyName));
            return false;
        }
        return true;
    }
}
