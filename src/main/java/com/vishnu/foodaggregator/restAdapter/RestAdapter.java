package com.vishnu.foodaggregator.restAdapter;

import java.util.Optional;


public interface RestAdapter {
    <T> Optional<T[]> getItemsFromSupplier(String supplierUrl, Class<T[]> supplierDTOType);
}
