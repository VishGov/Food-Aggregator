package com.vishnu.foodaggregator.controller;


import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import com.vishnu.foodaggregator.service.AggregatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.vishnu.foodaggregator.constants.Mappings.*;

@RequestMapping(FOOD_AGGREGATOR_V1)
@RestController
public class BuyController {

    private AggregatorService aggregatorService;

    public BuyController(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping(value = BUY_ITEM)
    public ResponseEntity<ItemResponse> buyItem(@PathVariable String itemName) throws ItemNotFoundException {
        return new ResponseEntity<>(aggregatorService.getByName(itemName), HttpStatus.OK);
    }
}
