package com.vishnu.foodaggregator.service.impl;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import com.vishnu.foodaggregator.restAdapter.RestAdapter;
import com.vishnu.foodaggregator.service.SupplierService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vishnu.foodaggregator.constants.Constants.*;

@AllArgsConstructor
@Service
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private Environment env;
    private RestAdapter restAdapter;

    @Override
    public List<FruitSupplierDTO> getFruitItems() {
        String fruitSupplierUrl = env.getProperty(SUPPLIER_FRUIT_URL);
        List<FruitSupplierDTO> fruitSupplierDTOS = restAdapter.getItemsFromSupplier(fruitSupplierUrl, FruitSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());

        log.info("Fruits from supplier. Result = {}", fruitSupplierDTOS);
        return fruitSupplierDTOS;
    }

    @Override
    public List<VegSupplierDTO> getVegetableItems() {
        String vegSupplierUrl = env.getProperty(SUPPLIER_VEGETABLE_URL);
        List<VegSupplierDTO> vegSupplierDTOS = restAdapter.getItemsFromSupplier(vegSupplierUrl, VegSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());

        log.info("Vegetables from supplier. Result = {}", vegSupplierDTOS);
        return vegSupplierDTOS;
    }

    @Override
    public List<GrainSupplierDTO> getGrainItems() {
        String grainSupplierUrl = env.getProperty(SUPPLIER_GRAIN_URL);
        List<GrainSupplierDTO> grainSupplierDTOS = restAdapter.getItemsFromSupplier(grainSupplierUrl, GrainSupplierDTO[].class).map(Arrays::asList).orElse(new ArrayList<>());

        log.info("Grains from supplier. Result = {}", grainSupplierDTOS);
        return grainSupplierDTOS;
    }

}
