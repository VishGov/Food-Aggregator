package com.vishnu.foodaggregator.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ValidationRule {

    private String errorMessage;

    abstract boolean validate();
}
