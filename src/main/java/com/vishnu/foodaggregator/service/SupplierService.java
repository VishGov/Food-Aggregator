package com.vishnu.foodaggregator.service;

import com.vishnu.foodaggregator.dto.FruitSupplierDTO;
import com.vishnu.foodaggregator.dto.GrainSupplierDTO;
import com.vishnu.foodaggregator.dto.VegSupplierDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.vishnu.foodaggregator.constants.Constants.*;

public interface SupplierService {

    List<FruitSupplierDTO> getFruitItems();

    List<VegSupplierDTO> getVegetableItems();

    List<GrainSupplierDTO> getGrainItems();
}
