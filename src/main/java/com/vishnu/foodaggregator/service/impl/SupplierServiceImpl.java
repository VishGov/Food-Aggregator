package com.vishnu.foodaggregator.service.impl;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.restAdapter.RestAdapter;
import com.vishnu.foodaggregator.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.vishnu.foodaggregator.constants.Constants.*;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private Environment env;
    private RestAdapter restAdapter;

    @Override
    public List<FruitSupplierDTO> getFruitItems() {
        String fruitSupplierUrl = env.getProperty(SUPPLIER_FRUIT_URL);
        return restAdapter.getItemsFromSupplier(fruitSupplierUrl, FruitSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

    @Override
    public List<VegSupplierDTO> getVegetableItems() {
        String vegSupplierUrl = env.getProperty(SUPPLIER_VEGETABLE_URL);
        return restAdapter.getItemsFromSupplier(vegSupplierUrl, VegSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

    @Override
    public List<GrainSupplierDTO> getGrainItems() {
        String grainSupplierUrl = env.getProperty(SUPPLIER_GRAIN_URL);
        return restAdapter.getItemsFromSupplier(grainSupplierUrl, GrainSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());
    }

}
