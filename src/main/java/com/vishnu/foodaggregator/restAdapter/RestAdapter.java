package com.vishnu.foodaggregator.restAdapter;

import org.springframework.stereotype.Component;

import java.util.Optional;


public interface RestAdapter {
    <T> Optional<T[]> getItemsFromSupplier(String supplierUrl, Class<T[]> supplierDTOType);
}
