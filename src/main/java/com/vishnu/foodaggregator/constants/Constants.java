package com.vishnu.foodaggregator.constants;

public final class Constants {

    // Exception Messages
    public static final String ITEM_NOT_FOUND_MESSAGE = "Item : '%s' is not found";
    public static final String ITEM_NOT_FOUND_FOR_QTY_MESSAGE = "Item : '%s' is not found for quantity %d";
    public static final String ITEM_NOT_FOUND_FOR_QTY_PRICE_MESSAGE = "Item : '%s' is not found for quantity %d and price %s";
    public static final String ITEM_REQUEST_INVALID_QUANTITY = "Quantity must be 1 or more";
    public static final String ITEM_REQUEST_INVALID_PARAM_NOT_SET = "'%s' parameter is not set";
    public static final String ITEM_REQUEST_INVALID_PRICE_LESS_THAN_ZERO = "Price must be greater than $0.00";
    public static final String ITEM_REQUEST_INVALID_PRICE_NOT_USD = "Price must be in USD and have the format $<dollar>.<cents>";

    // Supplier URLs
    public static final String SUPPLIER_FRUIT_URL = "supplier.fruit.url";
    public static final String SUPPLIER_VEGETABLE_URL = "supplier.vegetable.url";
    public static final String SUPPLIER_GRAIN_URL = "supplier.grain.url";

    // LoggingAspect
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String METHOD_LOGGING_LOGGER_PATTERN = "SESSION_ID = %s %s#%s : PARAMS = {%s} ; TIME = %d";

}
