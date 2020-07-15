package com.vishnu.foodaggregator.restAdapter.impl;

import com.vishnu.foodaggregator.restAdapter.RestAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class RestAdapterImpl implements RestAdapter {

    private RestTemplate restTemplate;

    public <T> Optional<T[]> getItemsFromSupplier(String supplierUrl, Class<T[]> supplierDTOType) {
        T[] supplierResponse = null;
        try {
            supplierResponse = restTemplate.getForObject(supplierUrl, supplierDTOType);
            log.info("Supplier call successful. Results Size = {}", supplierResponse.length);
        } catch (RestClientException e) {
            log.error("Unable to contact {} , MSG = {{}}", supplierUrl, e.getLocalizedMessage());
        }
        return Optional.ofNullable(supplierResponse);
    }

}
