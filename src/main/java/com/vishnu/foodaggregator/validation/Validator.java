package com.vishnu.foodaggregator.validation;

import com.vishnu.foodaggregator.exception.InvalidItemRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Validator {

    final List<ValidationRule> validationList;

    private Validator(List<ValidationRule> validationList) throws InvalidItemRequestException {
        this.validationList = validationList;
        validate();
    }

    public static ValidatorBuilder createValidator() {
        return new ValidatorBuilder();
    }

    private void validate() throws InvalidItemRequestException {
        Optional<ValidationRule> validationRule = validationList.stream()
                .filter(rule -> !rule.validate())
                .findFirst();

        if (validationRule.isPresent())
            throw new InvalidItemRequestException(validationRule.get().getErrorMessage());

    }

    public static class ValidatorBuilder {
        private final List<ValidationRule> validationList;

        ValidatorBuilder() {
            validationList = new ArrayList<>();
        }

        public ValidatorBuilder addValidation(ValidationRule validationRule) {
            this.validationList.add(validationRule);
            return this;
        }

        public void validate() {
            new Validator(validationList);
        }

    }
}
