package com.vishnu.foodaggregator.restAdapter.impl;

import com.vishnu.foodaggregator.restAdapter.RestAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RestAdapterImpl implements RestAdapter {

    private RestTemplate restTemplate;

    public <T> Optional<T[]> getItemsFromSupplier(String supplierUrl, Class<T[]> supplierDTOType) {
        T[] supplierResponse = restTemplate.getForObject(supplierUrl, supplierDTOType);
        return Optional.ofNullable(supplierResponse);
    }

}
