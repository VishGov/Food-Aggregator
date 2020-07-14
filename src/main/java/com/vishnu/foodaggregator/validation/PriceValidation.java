package com.vishnu.foodaggregator.validation;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_PRICE_LESS_THAN_ZERO;
import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_PRICE_NOT_USD;

@AllArgsConstructor
public class PriceValidation extends ValidationRule {

    private String price;

    @Override
    boolean validate() {
        try {
            BigDecimal requestPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(price)));
            if (requestPrice.compareTo(BigDecimal.ZERO) <= 0) {
                setErrorMessage(ITEM_REQUEST_INVALID_PRICE_LESS_THAN_ZERO);
                return false;
            }
        } catch (ParseException e) {
            setErrorMessage(ITEM_REQUEST_INVALID_PRICE_NOT_USD);
            return false;
        }
        return true;
    }
}
