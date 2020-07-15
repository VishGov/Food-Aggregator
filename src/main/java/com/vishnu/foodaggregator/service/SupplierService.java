package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;

import java.util.List;

public interface SupplierService {

    List<FruitSupplierDTO> getFruitItems();

    List<VegSupplierDTO> getVegetableItems();

    List<GrainSupplierDTO> getGrainItems();
}
